package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewDetailAndSurveyCreateDtoRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.request.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewDetail;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.exception.ManipulateDeletedReviewsException;
import bbangduck.bd.bbangduck.domain.review.exception.NoGenreToRegisterForReviewSurveyException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewHasNotSurveyException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ManipulateDeletedThemeException;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class ReviewServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("리뷰 생성")
    @Transactional
    public void createReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when
        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        Review findReview = reviewService.getReview(reviewId);

        Member reviewMember = findReview.getMember();
        Theme reviewTheme = findReview.getTheme();
        List<Genre> reviewThemeGenres = reviewTheme.getGenres();
//        List<ReviewImage> reviewImages = findReview.getReviewImages();

        assertEquals(signUpId, reviewMember.getId(), "리뷰 생성 요청 회원의 ID 와 생성된 리뷰의 회원 ID 는 같아야한다.");

        assertEquals(savedTheme.getId(), reviewTheme.getId(), "리뷰 생성 요청된 테마의 ID 와 생성된 리뷰의 테마 ID 는 같아야한다.");
        assertEquals(savedTheme.getName(), reviewTheme.getName(), "리뷰 생성 요청된 테마의 이름과 생성된 리뷰의 테마 이름은 같아야한다.");

        List<MemberPlayInclination> memberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(reviewMember.getId());
        memberPlayInclinations.forEach(memberPlayInclination -> {
            Genre memberPlayInclinationGenre = memberPlayInclination.getGenre();
            boolean reviewThemeGenresExists = reviewThemeGenres.stream().anyMatch(genre -> genre.getId().equals(memberPlayInclinationGenre.getId()));
            assertTrue(reviewThemeGenresExists, "생성된 리뷰를 작성한 회원의 플레이 성향에 생성된 리뷰가 등록된 테마의 장르가 반영되어 있어야 한다.");
            assertEquals(1, memberPlayInclination.getPlayCount(), "회원가입 이후 처음 리뷰를 작성한 것으로 회원 성향의 play count 는 모두 1이어야 한다.");
        });


//        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())),
//                "생성된 리뷰 이미지 목록에 리뷰 작성 시 등록한 리뷰 이미지가 모두 있어야 한다.");

        findReview.getPlayTogetherMembers().forEach(member -> {
            System.out.println("Play together member  = " + member.toString());
            boolean reviewPlayTogetherExists = friendIds.stream().anyMatch(friendId -> member.getId().equals(friendId));
            assertTrue(reviewPlayTogetherExists, "생성된 리뷰의 함께 플레이한 친구 목록에 생성 요청 시 등록한 친구 id 가 모두 포함되어 있어야 한다.");
        });

        assertEquals(1, findReview.getRecodeNumber(), "해당 회원은 리뷰를 처음 생성했으므로, 생성된 리뷰의 번호는 1번");
        assertEquals(reviewCreateDto.isClearYN(), findReview.isClearYN());
    }

    @Test
    @DisplayName("리뷰 생성 - 테마가 삭제된 테마일 경우")
    public void createReview_ThemeIsDeleted() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createDeletedThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(ManipulateDeletedThemeException.class, () -> reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("리뷰 생성 - 리뷰를 두번 생성할 경우 2번째 생성된 리뷰의 recode number 가 2인지 검증")
    public void createReview_recodeNumberTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);
        //when
        System.out.println("==========================================================================================================================================================");
        Long reviewId2 = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);
        System.out.println("==========================================================================================================================================================");

        //then
        Review review2 = reviewService.getReview(reviewId2);
        assertEquals(2, review2.getRecodeNumber(), "해당 회원의 두 번째 생성된 리뷰의 recodeNumber 는 2여야 한다.");

    }

    @Test
    @DisplayName("리뷰 생성 시 회원을 찾을 수 없는 경우")
    public void createReview_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> reviewService.createReview(100000L, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("리뷰 생성 시 테마를 찾을 수 없는 경우")
    public void createReview_ThemeNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(ThemeNotFoundException.class, () -> reviewService.createReview(signUpId, 100000L, reviewCreateDto));

    }

    @Test
    @DisplayName("리뷰 생성 시 테마에 장르가 등록되어 있지 않은 경우")
    public void createReview_ThemeGenreNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createNotRegisterGenreTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when
        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        assertTrue(memberPlayInclinationQueryRepository.findAllByMember(signUpId).isEmpty(), "테마에 등록된 장르가 없을 경우 회원의 성향은 반영되지 않는다.");

    }

    @Test
    @DisplayName("리휴 생성 시 기존에 해당 테마에 등록된 장르를 플레이 한 적이 있을 경우 해당 장르에 대한 play count 가 잘 증가되는지 확인")
    public void createReview_IncreasePlayCount() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        Review savedReview = reviewService.getReview(reviewId);
        Member reviewMember = savedReview.getMember();

        List<MemberPlayInclination> reviewMemberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(reviewMember.getId());
        reviewMemberPlayInclinations.forEach(memberPlayInclination -> {
            int playCount = memberPlayInclination.getPlayCount();
            assertEquals(1, playCount, "처음 회원가입 이후 처음 댓글 작성 시 회원 플레이 성향의 장르별 플레이 횟수는 1 이 나와야 한다.");
        });

        //when
        Long reviewId2 = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        Review findReview2 = reviewService.getReview(reviewId2);
        Member review2Member = findReview2.getMember();
        List<MemberPlayInclination> review2MemberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(review2Member.getId());
        review2MemberPlayInclinations.forEach(memberPlayInclination -> {
            int playCount = memberPlayInclination.getPlayCount();
            assertEquals(2, playCount, "같은 테마에 대해서 두번 리뷰를 작성한 것이므로 회원 플레이 성향의 모든 장르에 대한 플레이 횟수는 2 가 나와야 한다.");
        });

    }

    @Test
    @DisplayName("리뷰에 친구 함께한 친구 등록 시 리뷰를 작성한 회원이 등록하는 회원이 친구 관계가 아닌 경우")
    public void createReview_PlayTogetherWithNotFriend() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Member savedRequestStateFriend = createRequestStateFriendToMember(memberSignUpRequestDto, signUpId);
        friendIds.add(savedRequestStateFriend.getId());


        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(RelationOfMemberAndFriendIsNotFriendException.class, () -> reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("리뷰 조회 - 삭제된 리뷰일 경우")
    public void getReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        reviewService.deleteReview(reviewId);

        em.flush();
        em.clear();
        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.getReview(reviewId));

    }

    @ParameterizedTest
    @MethodSource("provideParametersForGetThemeReviewList")
    @Transactional
    @DisplayName("테마에 등록된 리뷰 목록 조회")
    public void getThemeReviewList(ReviewSortCondition sortCondition) {
        //given
        Theme theme = createThemeSample();
        ReviewSearchDto reviewSearchDto = ReviewSearchDto.builder()
                .criteria(new CriteriaDto())
                .sortCondition(sortCondition)
                .build();

        List<Review> tmpReviewList = createTmpReviewList(theme);

        Theme theme2 = createThemeSample();
        createTmpReviewList(theme2);

        em.flush();
        em.clear();

        Long deletedReviewId1 = tmpReviewList.get(13).getId();
        Long deletedReviewId2 = tmpReviewList.get(14).getId();
        Long deletedReviewId3 = tmpReviewList.get(15).getId();
        reviewService.deleteReview(deletedReviewId1);
        reviewService.deleteReview(deletedReviewId2);
        reviewService.deleteReview(deletedReviewId3);

        em.flush();
        em.clear();


        //when
        System.out.println("================================================================================================================================================================");
        QueryResults<Review> reviewQueryResults = reviewService.getThemeReviewList(theme.getId(), reviewSearchDto);
        List<Review> findReviews = reviewQueryResults.getResults();
        System.out.println("================================================================================================================================================================");

        //then
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId1)), "조회된 리뷰 목록에는 deletedReviewId1 이 없어야 한다.");
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId2)), "조회된 리뷰 목록에는 deletedReviewId2 이 없어야 한다.");
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId3)), "조회된 리뷰 목록에는 deletedReviewId3 이 없어야 한다.");

        findReviews.forEach(review -> {
            Theme reviewTheme = review.getTheme();
            assertEquals(theme.getId(), reviewTheme.getId());
            System.out.println("review = " + review);
        });

        boolean sortFlag = true;
        switch (sortCondition) {
            case LIKE_COUNT_DESC:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    if (nowReview.getLikeCount() < nextReview.getLikeCount()) {
                        sortFlag = false;
                        break;
                    } else if (nowReview.getLikeCount() == nextReview.getLikeCount()) {
                        LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                        LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                        if (nowReviewRegisterTimes.isBefore(nextReviewRegisterTimes)) {
                            sortFlag = false;
                            break;
                        }
                    }
                }
                break;
            case LIKE_COUNT_ASC:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    if (nowReview.getLikeCount() > nextReview.getLikeCount()) {
                        sortFlag = false;
                        break;
                    } else if (nowReview.getLikeCount() == nextReview.getLikeCount()) {
                        LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                        LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                        if (nowReviewRegisterTimes.isBefore(nextReviewRegisterTimes)) {
                            sortFlag = false;
                            break;
                        }
                    }
                }
                break;
            case LATEST:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                    LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                    if (nowReviewRegisterTimes.isBefore(nextReviewRegisterTimes)) {
                        sortFlag = false;
                        break;
                    }
                }
                break;
            case OLDEST:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                    LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                    if (nowReviewRegisterTimes.isAfter(nextReviewRegisterTimes)) {
                        sortFlag = false;
                        break;
                    }
                }
                break;
            case RATING_DESC:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    if (nowReview.getRating() < nextReview.getRating()) {
                        sortFlag = false;
                        break;
                    } else if (nowReview.getRating() == nextReview.getRating()) {
                        LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                        LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                        if (nowReviewRegisterTimes.isBefore(nextReviewRegisterTimes)) {
                            sortFlag = false;
                            break;
                        }
                    }
                }
                break;
            case RATING_ASC:
                for (int i = 0; i < findReviews.size()-1; i++) {
                    Review nowReview = findReviews.get(i);
                    Review nextReview = findReviews.get(i + 1);

                    if (nowReview.getRating() > nextReview.getRating()) {
                        sortFlag = false;
                        break;
                    } else if (nowReview.getRating() == nextReview.getRating()) {
                        LocalDateTime nowReviewRegisterTimes = nowReview.getRegisterTimes();
                        LocalDateTime nextReviewRegisterTimes = nextReview.getRegisterTimes();

                        if (nowReviewRegisterTimes.isBefore(nextReviewRegisterTimes)) {
                            sortFlag = false;
                            break;
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortCondition);
        }

        assertTrue(sortFlag);

    }

    private List<Review> createTmpReviewList(Theme theme) {
        Member adminMemberSample = createAdminMemberSample();
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Review newReview = Review.builder()
                    .theme(theme)
                    .member(adminMemberSample)
                    .rating(new Random().nextInt(10))
                    .likeCount(new Random().nextInt(20))
                    .build();

            Review savedReview = reviewRepository.save(newReview);
            reviewList.add(savedReview);
        }

        return reviewList;
    }

    private static Stream<Arguments> provideParametersForGetThemeReviewList() {
        return Stream.of(
                Arguments.of(ReviewSortCondition.LATEST),
                Arguments.of(ReviewSortCondition.OLDEST),
                Arguments.of(ReviewSortCondition.LIKE_COUNT_DESC),
                Arguments.of(ReviewSortCondition.LIKE_COUNT_ASC),
                Arguments.of(ReviewSortCondition.RATING_DESC),
                Arguments.of(ReviewSortCondition.RATING_ASC)
        );
    }

    @Test
    @DisplayName("리뷰에 설문 정보 추가")
    @Transactional
    public void addSurveyToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(List.of(storedFile), friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        //when
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateDto);

        em.flush();
        em.clear();

        //then
        Review findReview = reviewService.getReview(reviewId);
        ReviewSurvey findReviewReviewSurvey = findReview.getReviewSurvey();

        assertEquals(findReview.getId(), findReviewReviewSurvey.getReview().getId());

        assertEquals(reviewSurveyCreateDto.getPerceivedDifficulty(), findReviewReviewSurvey.getPerceivedDifficulty());
        assertEquals(reviewSurveyCreateDto.getPerceivedHorrorGrade(), findReviewReviewSurvey.getPerceivedHorrorGrade());
        assertEquals(reviewSurveyCreateDto.getPerceivedActivity(), findReviewReviewSurvey.getPerceivedActivity());
        assertEquals(reviewSurveyCreateDto.getScenarioSatisfaction(), findReviewReviewSurvey.getScenarioSatisfaction());
        assertEquals(reviewSurveyCreateDto.getInteriorSatisfaction(), findReviewReviewSurvey.getInteriorSatisfaction());
        assertEquals(reviewSurveyCreateDto.getProblemConfigurationSatisfaction(), findReviewReviewSurvey.getProblemConfigurationSatisfaction());

        List<Genre> perceivedThemeGenres = findReviewReviewSurvey.getPerceivedThemeGenres();
        genreCodes.forEach(genreCode -> {
            boolean genreCodesAnyMatch = perceivedThemeGenres.stream().anyMatch(genre -> genre.getCode().equals(genreCode));
            assertTrue(genreCodesAnyMatch, "리뷰 설문 추가 메소드 호출 시 입력한 장르 코드 중 하나는 추가된 설문에 등록된 장르의 장르 코드 중 하나로 있어야 한다.");
        });

    }

    @Test
    @DisplayName("리뷰에 설문 정보 추가 - 삭제된 리뷰에 설문을 추가하는 경우")
    public void addSurveyToReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(List.of(storedFile), friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        reviewService.deleteReview(reviewId);

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.addSurveyToReview(reviewId, reviewSurveyCreateDto));

    }

    @Test
    @DisplayName("리뷰에 설문 정보 추가 - 리뷰를 찾을 수 없는 경우")
    @Transactional
    public void addSurveyToReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(List.of(storedFile), friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ReviewNotFoundException.class, () -> reviewService.addSurveyToReview(10000L, reviewSurveyCreateDto));

    }

    @Test
    @DisplayName("리뷰에 설문 정보 추가 - 리뷰 설문에 등록할 장르를 찾을 수 없는 경우")
    @Transactional
    public void addSurveyToReview_GenreNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(List.of(storedFile), friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<String> genreCodes = List.of("AMGN1", "AMGN2");

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(GenreNotFoundException.class, () -> reviewService.addSurveyToReview(reviewId, reviewSurveyCreateDto));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정")
    @Transactional
    public void updateSurveyFromReview() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "RMC1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        em.flush();
        em.clear();

        //when
        reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto());

        em.flush();
        em.clear();

        //then
        Review findReview = reviewService.getReview(reviewId);
        ReviewSurvey findReviewSurvey = findReview.getReviewSurvey();

        List<Genre> findReviewSurveyPerceivedThemeGenres = findReviewSurvey.getPerceivedThemeGenres();
        findReviewSurveyPerceivedThemeGenres.forEach(genre -> System.out.println("genre = " + genre));
        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().anyMatch(genre -> genre.getCode().equals("HR1")), "변경된 설문에 등록된 체감 테마 장르에는 HR1 장르 코드의 장르가 있어야 한다.");
        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().anyMatch(genre -> genre.getCode().equals("RMC1")),"변경된 설문에 등록된 체감 테마 장르에는 RMC1 장르 코드의 장르가 있어야 한다.");
        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().noneMatch(genre -> genre.getCode().equals("RSN1")), "변경된 설문에 등록된 체감 테마 장르에는 RSN1 장르 코드의 장르가 없어야 한다.");

        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedDifficulty(), findReviewSurvey.getPerceivedDifficulty());
        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedActivity(), findReviewSurvey.getPerceivedActivity());
        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedHorrorGrade(), findReviewSurvey.getPerceivedHorrorGrade());
        assertEquals(reviewSurveyUpdateRequestDto.getScenarioSatisfaction(), findReviewSurvey.getScenarioSatisfaction());
        assertEquals(reviewSurveyUpdateRequestDto.getInteriorSatisfaction(), findReviewSurvey.getInteriorSatisfaction());
        assertEquals(reviewSurveyUpdateRequestDto.getProblemConfigurationSatisfaction(), findReviewSurvey.getProblemConfigurationSatisfaction());

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 삭제된 리뷰의 설문을 수정")
    public void updateSurveyFromReview_DeletedReview() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "RMC1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        em.flush();
        em.clear();

        reviewService.deleteReview(reviewId);

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰를 찾을 수 없는 경우")
    public void updateSurveyFromReview_ReviewNotFound() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "RMC1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);


        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ReviewNotFoundException.class, ()-> reviewService.updateSurveyFromReview(100000L, reviewSurveyUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 장르를 찾을 수 없는 경우")
    public void updateSurveyFromReview_GenreNotFound() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("AMRN1", "AMRN2");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);


        em.flush();
        em.clear();


        //when

        //then
        assertThrows(GenreNotFoundException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 장르 코드 목록을 기입하지 않은 경우")
    public void updateSurveyFromReview_GenreCodesEmpty() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = new ArrayList<>();
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(NoGenreToRegisterForReviewSurveyException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰에 설문이 등로되어 있지 않을 경우")
    @Transactional
    public void updateSurveyFromReview_ReviewHasNotSurvey() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());


        List<String> oldGenreCodes = List.of("HR1", "RSN1");
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
//        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "RMC1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ReviewHasNotSurveyException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰 수정 - 간단 리뷰 to 상세 리뷰")
    public void updateReview_SimpleToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(2));

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when
        reviewService.updateReview(createdReviewId, reviewUpdateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        //then

        Review findReview = reviewService.getReview(createdReviewId);

        assertEquals(reviewUpdateRequestDto.getReviewType(), findReview.getReviewType());
        assertEquals(reviewUpdateRequestDto.getClearYN(), findReview.isClearYN());
        assertEquals(reviewUpdateRequestDto.getClearTime(), findReview.getClearTime());
        assertEquals(reviewUpdateRequestDto.getHintUsageCount(), findReview.getHintUsageCount());
        assertEquals(reviewUpdateRequestDto.getRating(), findReview.getRating());
        ReviewDetail findReviewReviewDetail = findReview.getReviewDetail();
        assertEquals(reviewUpdateRequestDto.getComment(), findReviewReviewDetail.getComment());

        List<Member> playTogetherMembers = findReview.getPlayTogetherMembers();
        assertTrue(playTogetherMembers.stream().anyMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(0))), "수정된 리뷰에 등록된 친구 목록에는 0 번 친구가 포함되어 있어야 한다.");
        assertTrue(playTogetherMembers.stream().noneMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(1))), "수정된 리뷰에 등록된 친구 목록에는 1 번 친구가 포함되어 있지 않아야 한다.");
        assertTrue(playTogetherMembers.stream().anyMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(2))), "수정된 리뷰에 등록된 친구 목록에는 2 번 친구가 포함되어 있어야 한다.");

        List<ReviewImage> reviewImages = findReviewReviewDetail.getReviewImages();
        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())), "수정된 리뷰에 등록된 이미지에는 수정 요청 시 기입한 이미지 파일에 대한 정보가 있어야 한다.");

    }

    @Test
    @DisplayName("리뷰 수정 - 상세 리뷰 to 간단 리뷰")
    public void updateReview_DetailToSimple() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewService.addDetailToReview(createdReviewId, reviewDetailCreateRequestDto.toServiceDto());

        ReviewUpdateRequestDto reviewUpdateRequestDto = createSimpleReviewUpdateRequestDto(friendIds);

        em.flush();
        em.clear();

        //when
        System.out.println("====================================================================================================================================");
        reviewService.updateReview(createdReviewId, reviewUpdateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        System.out.println("====================================================================================================================================");

        //then
        Review findReview = reviewService.getReview(createdReviewId);

        assertEquals(reviewUpdateRequestDto.getReviewType(), findReview.getReviewType());
        assertEquals(reviewUpdateRequestDto.getClearYN(), findReview.isClearYN());
        assertEquals(reviewUpdateRequestDto.getClearTime(), findReview.getClearTime());
        assertEquals(reviewUpdateRequestDto.getHintUsageCount(), findReview.getHintUsageCount());
        assertEquals(reviewUpdateRequestDto.getRating(), findReview.getRating());

        List<Member> playTogetherMembers = findReview.getPlayTogetherMembers();
        playTogetherMembers.forEach(member -> assertTrue(friendIds.stream().anyMatch(friendId -> friendId.equals(member.getId())), "수정된 리뷰에는 리뷰 생성 시 등록하고, 수정 시 바꾸지 않고 다시 등록했던 친구들이 그대로 있어야 한다."));

//        List<ReviewImage> reviewImages = findReview.getReviewImages();
//        reviewImages.forEach(reviewImage -> System.out.println("reviewImage = " + reviewImage));
//        assertTrue(reviewImages.isEmpty(), "상세 리뷰에서 간단 리뷰로 수정될 경우 review image 는 비어 있어야 한다.");
//        assertNull(findReview.getComment(), "수정된 review 의 코멘트는 null 이어야 한다.");

    }

    @Test
    @DisplayName("리뷰 수정 - 상세 리뷰 to 상세 리뷰")
    public void updateReview_DetailToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(friendIds.get(0), friendIds.get(1));

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewService.addDetailToReview(createdReviewId, reviewDetailCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendIds.get(1), friendIds.get(2));
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        ReviewImageRequestDto reviewImageRequestDto1 = reviewImageRequestDtos.get(0);
        ReviewImageRequestDto reviewImageRequestDto2 = reviewImageRequestDtos.get(1);
        ReviewImageRequestDto reviewImageRequestDto3 = new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName());


        List<ReviewImageRequestDto> updateReviewImageRequestDtos = List.of(reviewImageRequestDto1, reviewImageRequestDto3);

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, updateReviewImageRequestDtos);

        em.flush();
        em.clear();

        //when
        System.out.println("====================================================================================================================================");
        reviewService.updateReview(createdReviewId, reviewUpdateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        System.out.println("====================================================================================================================================");

        //then
        Review findReview = reviewService.getReview(createdReviewId);
//        List<ReviewImage> findReviewImages = findReview.getReviewImages();

//        assertTrue(findReviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto1.getFileStorageId())), "수정 된 리뷰에는 1 번 이미지가 포함되어 있어야 한다.");
//        assertTrue(findReviewImages.stream().noneMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto2.getFileStorageId())), "수정 된 리뷰에는 2 번 이미지가 포함되어 있지 않아야 한다.");
//        assertTrue(findReviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto3.getFileStorageId())), "수정 된 리뷰에는 3 번 이미지가 포함되어 있어야 한다.");

    }

    @Test
    @DisplayName("리뷰 수정 - 삭제된 리뷰일 경우")
    public void updateReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(2));

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        System.out.println("================================================================================================================");
        reviewService.deleteReview(createdReviewId);

        em.flush();
        em.clear();
        System.out.println("================================================================================================================");

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.updateReview(createdReviewId, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰 수정 - 리뷰를 찾을 수 없는 경우")
    public void updateReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(2));

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when

        //then
        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(100000L, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰 수정 - 수정 시 등록하는 친구와 리뷰를 작성한 회원이 실제 친구 관계가 아닐 경우")
    public void updateReview_ReviewMemberAndFriendIdMemberAreNotFriend() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = new ArrayList<>();
        newFriendIds.add(signUpMemberFriendsIds.get(0));
        newFriendIds.add(signUpMemberFriendsIds.get(2));

        Member requestStateFriendToMember = createRequestStateFriendToMember(memberSignUpRequestDto, signUpId);
        newFriendIds.add(requestStateFriendToMember.getId());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when

        //then
        assertThrows(RelationOfMemberAndFriendIsNotFriendException.class, () -> reviewService.updateReview(createdReviewId, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("리뷰에 리뷰 상세 추가")
    public void addDetailToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        em.flush();
        em.clear();
        //when
        reviewService.addDetailToReview(reviewId, reviewDetailCreateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        //then

        Review findReview = reviewService.getReview(reviewId);
        ReviewDetail findReviewReviewDetail = findReview.getReviewDetail();
        findReviewReviewDetail.getReviewImages().forEach(reviewImage -> {
            List<ReviewImageRequestDto> reviewImageRequestDtoList = reviewDetailCreateRequestDto.getReviewImages();
            assertTrue(reviewImageRequestDtoList.stream().anyMatch(reviewImageRequestDto -> reviewImageRequestDto.getFileStorageId().equals(reviewImage.getFileStorageId())),
                    "조회된 리뷰에는 리슈 상세 추가 요청 시 기입한 이미지 파일 저장소의 ID 가 모두 있어야 한다.");
        });
        assertEquals(reviewDetailCreateRequestDto.getComment(), findReviewReviewDetail.getComment(), "조회된 리뷰에는 리뷰 상세 추가 시 기입한 코멘트가 있어야 한다.");
        assertEquals(ReviewType.DETAIL, findReview.getReviewType(), "리뷰 상세가 추가된 리뷰는 ReviewType 이 Detail 이어야 한다.");

    }

    @Test
    @DisplayName("리뷰에 리뷰 상세 및 설문 추가")
    public void addDetailAndSurveyToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        List<String> genreCodes = createGenreCodes();

        ReviewDetailAndSurveyCreateDtoRequestDto reviewDetailAndSurveyCreateDtoRequestDto = createReviewDetailAndSurveyCreateDtoRequestDto(reviewImageRequestDtos, genreCodes);

        //when
        reviewService.addDetailAndSurveyToReview(reviewId, reviewDetailAndSurveyCreateDtoRequestDto.toDetailServiceDto(), reviewDetailAndSurveyCreateDtoRequestDto.toSurveyServiceDto());

        //then
        Review findReview = reviewService.getReview(reviewId);

        assertEquals(reviewCreateRequestDto.getClearYN(), findReview.isClearYN());
        assertEquals(reviewCreateRequestDto.getClearTime(), findReview.getClearTime());
        assertEquals(reviewCreateRequestDto.getHintUsageCount(), findReview.getHintUsageCount());
        assertEquals(reviewCreateRequestDto.getRating(), findReview.getRating());

        ReviewDetail findReviewReviewDetail = findReview.getReviewDetail();
        findReviewReviewDetail.getReviewImages().forEach(reviewImage -> {
            List<ReviewImageRequestDto> reviewImageRequestDtoList = reviewDetailAndSurveyCreateDtoRequestDto.getReviewImages();
            assertTrue(reviewImageRequestDtoList.stream().anyMatch(reviewImageRequestDto -> reviewImageRequestDto.getFileStorageId().equals(reviewImage.getFileStorageId())),
                    "조회된 리뷰에는 리슈 상세 추가 요청 시 기입한 이미지 파일 저장소의 ID 가 모두 있어야 한다.");
        });
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getComment(), findReviewReviewDetail.getComment(), "조회된 리뷰에는 리뷰 상세 추가 시 기입한 코멘트가 있어야 한다.");
        assertEquals(ReviewType.DETAIL, findReview.getReviewType(), "리뷰 상세가 추가된 리뷰는 ReviewType 이 Detail 이어야 한다.");

        ReviewSurvey findReviewReviewSurvey = findReview.getReviewSurvey();
        findReviewReviewSurvey.getPerceivedThemeGenres().forEach(genre -> assertTrue(genreCodes.stream().anyMatch(genreCode -> genreCode.equals(genre.getCode())),
                "조회된 리뷰에 등록된 체감 장르 목록에는 리뷰 설문 추가 시 등록한 장르코드에 해당하는 장르가 있어야 한다."));
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedDifficulty(), findReviewReviewSurvey.getPerceivedDifficulty());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedHorrorGrade(), findReviewReviewSurvey.getPerceivedHorrorGrade());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedActivity(), findReviewReviewSurvey.getPerceivedActivity());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getScenarioSatisfaction(), findReviewReviewSurvey.getScenarioSatisfaction());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getInteriorSatisfaction(), findReviewReviewSurvey.getInteriorSatisfaction());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getProblemConfigurationSatisfaction(), findReviewReviewSurvey.getProblemConfigurationSatisfaction());
    }

    @Test
    @DisplayName("리뷰 삭제")
    public void deleteReview() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("3198696876");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);

        Long member1review1Id = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review2Id = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review3Id = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review4Id = reviewService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member2review1Id = reviewService.createReview(member2Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member2review2Id = reviewService.createReview(member2Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        Review member1review1 = reviewService.getReview(member1review1Id);
        Review member1review2 = reviewService.getReview(member1review2Id);
        Review member1review3 = reviewService.getReview(member1review3Id);
        Review member1review4 = reviewService.getReview(member1review4Id);
        Review member2review1 = reviewService.getReview(member2review1Id);
        Review member2review2 = reviewService.getReview(member2review2Id);

        assertEquals(1, member1review1.getRecodeNumber());
        assertEquals(2, member1review2.getRecodeNumber());
        assertEquals(3, member1review3.getRecodeNumber());
        assertEquals(4, member1review4.getRecodeNumber());
        assertEquals(1, member2review1.getRecodeNumber());
        assertEquals(2, member2review2.getRecodeNumber());

        em.flush();
        em.clear();

        //when
        System.out.println("============================================================================================");
        reviewService.deleteReview(member1review2Id);

        em.flush();
        em.clear();
        System.out.println("============================================================================================");

        //then
        Review findMember1Review1 = reviewService.getReview(member1review1Id);
        Review findMember1Review3 = reviewService.getReview(member1review3Id);
        Review findMember1Review4 = reviewService.getReview(member1review4Id);
        Review findMember2Review1 = reviewService.getReview(member2review1Id);
        Review findMember2Review2 = reviewService.getReview(member2review2Id);

        assertEquals(1, findMember1Review1.getRecodeNumber());
        assertEquals(2, findMember1Review3.getRecodeNumber());
        assertEquals(3, findMember1Review4.getRecodeNumber());
        assertEquals(1, findMember2Review1.getRecodeNumber());
        assertEquals(2, findMember2Review2.getRecodeNumber());

        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.getReview(member1review2Id));
        Review findMember1Review2 = em.find(Review.class, member1review2Id);
        assertEquals(-1, findMember1Review2.getRecodeNumber(), "삭제된 리뷰의 레코드 번호는 -1 이 나와야 한다.");
        assertTrue(findMember1Review2.isDeleteYN(), "삭제될 리뷰의 deleteYN 은 true 가 나와야 한다.");

    }

}
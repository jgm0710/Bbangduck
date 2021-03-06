package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.follow.exception.NotTwoWayFollowRelationException;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewDetail;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.exception.ManipulateDeletedReviewsException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemePlayMember;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class ReviewServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when
        Long reviewId = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        Review findReview = reviewService.getReview(reviewId);

        Member reviewMember = findReview.getMember();
        Theme reviewTheme = findReview.getTheme();
        Genre genre = reviewTheme.getGenre();
//        List<ReviewImage> reviewImages = findReview.getReviewImages();

        assertEquals(signUpId, reviewMember.getId(), "?????? ?????? ?????? ????????? ID ??? ????????? ????????? ?????? ID ??? ???????????????.");

        assertEquals(savedTheme.getId(), reviewTheme.getId(), "?????? ?????? ????????? ????????? ID ??? ????????? ????????? ?????? ID ??? ???????????????.");
        assertEquals(savedTheme.getName(), reviewTheme.getName(), "?????? ?????? ????????? ????????? ????????? ????????? ????????? ?????? ????????? ???????????????.");

        List<MemberPlayInclination> memberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(reviewMember.getId());
        memberPlayInclinations.forEach(memberPlayInclination -> {
            Genre memberPlayInclinationGenre = memberPlayInclination.getGenre();
            assertEquals(1, memberPlayInclination.getPlayCount(), "???????????? ?????? ?????? ????????? ????????? ????????? ?????? ????????? play count ??? ?????? 1????????? ??????.");
        });


//        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())),
//                "????????? ?????? ????????? ????????? ?????? ?????? ??? ????????? ?????? ???????????? ?????? ????????? ??????.");

        findReview.getPlayTogetherMembers().forEach(member -> {
            System.out.println("Play together member  = " + member.toString());
            boolean reviewPlayTogetherExists = friendIds.stream().anyMatch(friendId -> member.getId().equals(friendId));
            assertTrue(reviewPlayTogetherExists, "????????? ????????? ?????? ???????????? ?????? ????????? ?????? ?????? ??? ????????? ?????? id ??? ?????? ???????????? ????????? ??????.");
        });

        assertEquals(1, findReview.getRecodeNumber(), "?????? ????????? ????????? ?????? ??????????????????, ????????? ????????? ????????? 1???");
        assertEquals(reviewCreateDto.isClearYN(), findReview.isClearYN());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ????????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(ManipulateDeletedThemeException.class, () -> reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????? ????????? ?????? 2?????? ????????? ????????? recode number ??? 2?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);
        //when
        System.out.println("==========================================================================================================================================================");
        Long reviewId2 = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);
        System.out.println("==========================================================================================================================================================");

        //then
        Review review2 = reviewService.getReview(reviewId2);
        assertEquals(2, review2.getRecodeNumber(), "?????? ????????? ??? ?????? ????????? ????????? recodeNumber ??? 2?????? ??????.");

    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ?????? ??? ?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> reviewApplicationService.createReview(100000L, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ?????? ??? ?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(ThemeNotFoundException.class, () -> reviewApplicationService.createReview(signUpId, 100000L, reviewCreateDto));

    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ????????? ???????????? ?????? ?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when
        Long reviewId = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        assertTrue(memberPlayInclinationQueryRepository.findAllByMember(signUpId).isEmpty(), "????????? ????????? ????????? ?????? ?????? ????????? ????????? ???????????? ?????????.");

    }

    @Test
    @DisplayName("?????? ?????? ??? ????????? ?????? ????????? ????????? ????????? ????????? ??? ?????? ?????? ?????? ?????? ????????? ?????? play count ??? ??? ??????????????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        Review savedReview = reviewService.getReview(reviewId);
        Member reviewMember = savedReview.getMember();

        List<MemberPlayInclination> reviewMemberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(reviewMember.getId());
        reviewMemberPlayInclinations.forEach(memberPlayInclination -> {
            Long playCount = memberPlayInclination.getPlayCount();
            assertEquals(1, playCount, "?????? ???????????? ?????? ?????? ?????? ?????? ??? ?????? ????????? ????????? ????????? ????????? ????????? 1 ??? ????????? ??????.");
        });

        //when
        Long reviewId2 = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        Review findReview2 = reviewService.getReview(reviewId2);
        Member review2Member = findReview2.getMember();
        List<MemberPlayInclination> review2MemberPlayInclinations = memberPlayInclinationQueryRepository.findAllByMember(review2Member.getId());
        review2MemberPlayInclinations.forEach(memberPlayInclination -> {
            Long playCount = memberPlayInclination.getPlayCount();
            assertEquals(2, playCount, "?????? ????????? ????????? ?????? ????????? ????????? ???????????? ?????? ????????? ????????? ?????? ????????? ?????? ????????? ????????? 2 ??? ????????? ??????.");
        });

    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? ?????? ??? ????????? ????????? ????????? ???????????? ????????? ?????? ????????? ?????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        //when

        //then
        assertThrows(NotTwoWayFollowRelationException.class, () -> reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ??????")
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

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        reviewApplicationService.deleteReview(signUpId, reviewId);

        em.flush();
        em.clear();
        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.getReview(reviewId));

    }

    @ParameterizedTest
    @MethodSource("provideParametersForGetThemeReviewList")
    @Transactional
    @DisplayName("????????? ????????? ?????? ?????? ??????")
    public void getThemeReviewList(ReviewSortCondition sortCondition) {
        //given
        Theme theme = createThemeSample();
        ReviewSearchDto reviewSearchDto = ReviewSearchDto.builder()
                .criteria(new CriteriaDto())
                .sortCondition(sortCondition)
                .build();

        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member member = memberService.getMember(signUpId);

        List<Review> tmpReviewList = createTmpReviewList(member, theme);

        Theme theme2 = createThemeSample();
        List<Review> reviewList = createTmpReviewList(member, theme2);

        long reviewLikeCount = 0;
        for (Review review : reviewList) {
            reviewLikeCount += review.getLikeCount();
        }

        ThemePlayMember themePlayMember = ThemePlayMember.builder()
                .theme(theme)
                .member(member)
                .reviewLikeCount(reviewLikeCount)
                .build();

        themePlayMemberRepository.save(themePlayMember);

        em.flush();
        em.clear();

        Long deletedReviewId1 = tmpReviewList.get(13).getId();
        Long deletedReviewId2 = tmpReviewList.get(14).getId();
        Long deletedReviewId3 = tmpReviewList.get(15).getId();
        reviewApplicationService.deleteReview(member.getId(), deletedReviewId1);
        reviewApplicationService.deleteReview(member.getId(), deletedReviewId2);
        reviewApplicationService.deleteReview(member.getId(), deletedReviewId3);

        em.flush();
        em.clear();


        //when
        System.out.println("================================================================================================================================================================");
        QueryResults<Review> reviewQueryResults = reviewService.getThemeReviewList(theme.getId(), reviewSearchDto);
        List<Review> findReviews = reviewQueryResults.getResults();
        System.out.println("================================================================================================================================================================");

        //then
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId1)), "????????? ?????? ???????????? deletedReviewId1 ??? ????????? ??????.");
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId2)), "????????? ?????? ???????????? deletedReviewId2 ??? ????????? ??????.");
        assertTrue(findReviews.stream().noneMatch(review -> review.getId().equals(deletedReviewId3)), "????????? ?????? ???????????? deletedReviewId3 ??? ????????? ??????.");

        findReviews.forEach(review -> {
            Theme reviewTheme = review.getTheme();
            assertEquals(theme.getId(), reviewTheme.getId());
            System.out.println("review = " + review);
        });

        boolean sortFlag = true;
        switch (sortCondition) {
            case LIKE_COUNT_DESC:
                for (int i = 0; i < findReviews.size() - 1; i++) {
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
                for (int i = 0; i < findReviews.size() - 1; i++) {
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
                for (int i = 0; i < findReviews.size() - 1; i++) {
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
                for (int i = 0; i < findReviews.size() - 1; i++) {
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
                for (int i = 0; i < findReviews.size() - 1; i++) {
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
                for (int i = 0; i < findReviews.size() - 1; i++) {
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

    private List<Review> createTmpReviewList(Member member, Theme theme) {
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            boolean clearYN = i % 2 != 0;

            Review newReview = Review.builder()
                    .theme(theme)
                    .member(member)
                    .rating(new Random().nextInt(10))
                    .likeCount(new Random().nextInt(20))
                    .clearYN(clearYN)
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
    @DisplayName("????????? ?????? ?????? ??????")
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

        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        //when
        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateDto);

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

        List<Genre> findReviewReviewSurveyPerceivedThemeGenres = findReviewReviewSurvey.getPerceivedThemeGenres();
        findReviewReviewSurveyPerceivedThemeGenres.forEach(findGenre -> {
            boolean genreCodesAnyMatch = perceivedThemeGenres.stream().anyMatch(genre -> genre == findGenre);
            assertTrue(genreCodesAnyMatch, "?????? ?????? ?????? ????????? ?????? ??? ????????? ?????? ?????? ??? ????????? ????????? ????????? ????????? ????????? ?????? ?????? ??? ????????? ????????? ??????.");
        });

    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ????????? ????????? ????????? ???????????? ??????")
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

        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        reviewApplicationService.deleteReview(signUpId, reviewId);

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateDto));

    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ????????? ?????? ??? ?????? ??????")
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

        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), reviewCreateDto);

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
        ReviewSurveyCreateDto reviewSurveyCreateDto = reviewSurveyCreateRequestDto.toServiceDto();

        em.flush();
        em.clear();

        //when

        //then
        assertThrows(ReviewNotFoundException.class, () -> reviewApplicationService.addSurveyToReview(10000L, signUpId, reviewSurveyCreateDto));

    }

//    @Test
//    @DisplayName("????????? ????????? ?????? ??????")
//    @Transactional
//    public void updateSurveyFromReview() {
//        //given
//        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//
//        List<String> oldGenreCodes = List.of("HR1", "RSN1");
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "RMC1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        em.flush();
//        em.clear();
//
//        //when
//        reviewApplicationService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto());
//
//        em.flush();
//        em.clear();
//
//        //then
//        Review findReview = reviewService.getReview(reviewId);
//        ReviewSurvey findReviewSurvey = findReview.getReviewSurvey();
//
//        List<Genre> findReviewSurveyPerceivedThemeGenres = findReviewSurvey.getPerceivedThemeGenres();
//        findReviewSurveyPerceivedThemeGenres.forEach(genre -> System.out.println("genre = " + genre));
//        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().anyMatch(genre -> genre.getCode().equals("HR1")), "????????? ????????? ????????? ?????? ?????? ???????????? HR1 ?????? ????????? ????????? ????????? ??????.");
//        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().anyMatch(genre -> genre.getCode().equals("RMC1")), "????????? ????????? ????????? ?????? ?????? ???????????? RMC1 ?????? ????????? ????????? ????????? ??????.");
//        assertTrue(findReviewSurveyPerceivedThemeGenres.stream().noneMatch(genre -> genre.getCode().equals("RSN1")), "????????? ????????? ????????? ?????? ?????? ???????????? RSN1 ?????? ????????? ????????? ????????? ??????.");
//
//        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedDifficulty(), findReviewSurvey.getPerceivedDifficulty());
//        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedActivity(), findReviewSurvey.getPerceivedActivity());
//        assertEquals(reviewSurveyUpdateRequestDto.getPerceivedHorrorGrade(), findReviewSurvey.getPerceivedHorrorGrade());
//        assertEquals(reviewSurveyUpdateRequestDto.getScenarioSatisfaction(), findReviewSurvey.getScenarioSatisfaction());
//        assertEquals(reviewSurveyUpdateRequestDto.getInteriorSatisfaction(), findReviewSurvey.getInteriorSatisfaction());
//        assertEquals(reviewSurveyUpdateRequestDto.getProblemConfigurationSatisfaction(), findReviewSurvey.getProblemConfigurationSatisfaction());
//
//    }

//    @Test
//    @DisplayName("????????? ????????? ?????? ?????? - ????????? ????????? ????????? ??????")
//    public void updateSurveyFromReview_DeletedReview() {
//        //given
//        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//
//        List<String> oldGenreCodes = List.of("HR1", "RSN1");
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "RMC1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        em.flush();
//        em.clear();
//
//        reviewService.deleteReview(reviewId);
//
//        em.flush();
//        em.clear();
//
//        //when
//
//        //then
//        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));
//
//    }

//    @Test
//    @DisplayName("????????? ????????? ?????? ?????? - ????????? ?????? ??? ?????? ??????")
//    public void updateSurveyFromReview_ReviewNotFound() {
//        //given
//        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//
//        List<String> oldGenreCodes = List.of("HR1", "RSN1");
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "RMC1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//
//        em.flush();
//        em.clear();
//
//        //when
//
//        //then
//        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateSurveyFromReview(100000L, reviewSurveyUpdateRequestDto.toServiceDto()));
//
//    }

//    @Test
//    @DisplayName("????????? ????????? ?????? ?????? - ????????? ?????? ??? ?????? ??????")
//    public void updateSurveyFromReview_GenreNotFound() {
//        //given
//        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//
//        List<String> oldGenreCodes = List.of("HR1", "RSN1");
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("AMRN1", "AMRN2");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//
//        em.flush();
//        em.clear();
//
//
//        //when
//
//        //then
//        assertThrows(GenreNotFoundException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));
//
//    }

//    @Test
//    @DisplayName("????????? ????????? ?????? ?????? - ????????? ????????? ???????????? ?????? ?????? ??????")
//    @Transactional
//    public void updateSurveyFromReview_ReviewHasNotSurvey() {
//        //given
//        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//
//        List<String> oldGenreCodes = List.of("HR1", "RSN1");
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(oldGenreCodes);
////        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "RMC1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        em.flush();
//        em.clear();
//
//        //when
//
//        //then
//        assertThrows(ReviewHasNotSurveyException.class, () -> reviewService.updateSurveyFromReview(reviewId, reviewSurveyUpdateRequestDto.toServiceDto()));
//
//    }

    @Test
    @DisplayName("?????? ?????? - ?????? ?????? to ?????? ??????")
    public void updateReview_SimpleToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(2));

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when
        reviewApplicationService.updateReview(createdReviewId,signUpId, reviewUpdateRequestDto.toServiceDto());

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
        assertTrue(playTogetherMembers.stream().anyMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(0))), "????????? ????????? ????????? ?????? ???????????? 0 ??? ????????? ???????????? ????????? ??????.");
        assertTrue(playTogetherMembers.stream().noneMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(1))), "????????? ????????? ????????? ?????? ???????????? 1 ??? ????????? ???????????? ?????? ????????? ??????.");
        assertTrue(playTogetherMembers.stream().anyMatch(member -> member.getId().equals(signUpMemberFriendsIds.get(2))), "????????? ????????? ????????? ?????? ???????????? 2 ??? ????????? ???????????? ????????? ??????.");

        List<ReviewImage> reviewImages = findReviewReviewDetail.getReviewImages();
        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())), "????????? ????????? ????????? ??????????????? ?????? ?????? ??? ????????? ????????? ????????? ?????? ????????? ????????? ??????.");

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ?????? to ?????? ??????")
    public void updateReview_DetailToSimple() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewApplicationService.addDetailToReview(createdReviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

        ReviewUpdateRequestDto reviewUpdateRequestDto = createSimpleReviewUpdateRequestDto(friendIds);

        em.flush();
        em.clear();

        //when
        System.out.println("====================================================================================================================================");
        reviewApplicationService.updateReview(createdReviewId, signUpId, reviewUpdateRequestDto.toServiceDto());

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
        playTogetherMembers.forEach(member -> assertTrue(friendIds.stream().anyMatch(friendId -> friendId.equals(member.getId())), "????????? ???????????? ?????? ?????? ??? ????????????, ?????? ??? ????????? ?????? ?????? ???????????? ???????????? ????????? ????????? ??????."));

//        List<ReviewImage> reviewImages = findReview.getReviewImages();
//        reviewImages.forEach(reviewImage -> System.out.println("reviewImage = " + reviewImage));
//        assertTrue(reviewImages.isEmpty(), "?????? ???????????? ?????? ????????? ????????? ?????? review image ??? ?????? ????????? ??????.");
//        assertNull(findReview.getComment(), "????????? review ??? ???????????? null ????????? ??????.");

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ?????? to ?????? ??????")
    public void updateReview_DetailToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(friendIds.get(0), friendIds.get(1));

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewApplicationService.addDetailToReview(createdReviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

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
        reviewApplicationService.updateReview(createdReviewId, signUpId, reviewUpdateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        System.out.println("====================================================================================================================================");

        //then
        Review findReview = reviewService.getReview(createdReviewId);
//        List<ReviewImage> findReviewImages = findReview.getReviewImages();

//        assertTrue(findReviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto1.getFileStorageId())), "?????? ??? ???????????? 1 ??? ???????????? ???????????? ????????? ??????.");
//        assertTrue(findReviewImages.stream().noneMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto2.getFileStorageId())), "?????? ??? ???????????? 2 ??? ???????????? ???????????? ?????? ????????? ??????.");
//        assertTrue(findReviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(reviewImageRequestDto3.getFileStorageId())), "?????? ??? ???????????? 3 ??? ???????????? ???????????? ????????? ??????.");

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ??????")
    public void updateReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(2));

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageId);

        em.flush();
        em.clear();

        System.out.println("================================================================================================================");
        reviewApplicationService.deleteReview(signUpId, createdReviewId);

        em.flush();
        em.clear();
        System.out.println("================================================================================================================");

        List<ReviewImageRequestDto> reviewImageRequestDtos = List.of(new ReviewImageRequestDto(storedFile.getId(), storedFile.getFileName()));

        ReviewUpdateRequestDto reviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        //when

        //then
        assertThrows(ManipulateDeletedReviewsException.class, () -> reviewApplicationService.updateReview(createdReviewId, signUpId, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????? ??? ?????? ??????")
    public void updateReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

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
        assertThrows(ReviewNotFoundException.class, () -> reviewApplicationService.updateReview(100000L, signUpId, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ??? ???????????? ????????? ????????? ????????? ????????? ?????? ?????? ????????? ?????? ??????")
    public void updateReview_ReviewMemberAndFriendIdMemberAreNotFriend() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> signUpMemberFriendsIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        List<Long> oldFriendIds = List.of(signUpMemberFriendsIds.get(0), signUpMemberFriendsIds.get(1));

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Theme theme = createThemeSample();

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

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
        assertThrows(NotTwoWayFollowRelationException.class, () -> reviewApplicationService.updateReview(createdReviewId, signUpId, reviewUpdateRequestDto.toServiceDto()));

    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    public void addDetailToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        em.flush();
        em.clear();
        //when
        reviewApplicationService.addDetailToReview(reviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

        em.flush();
        em.clear();
        //then

        Review findReview = reviewService.getReview(reviewId);
        ReviewDetail findReviewReviewDetail = findReview.getReviewDetail();
        findReviewReviewDetail.getReviewImages().forEach(reviewImage -> {
            List<ReviewImageRequestDto> reviewImageRequestDtoList = reviewDetailCreateRequestDto.getReviewImages();
            assertTrue(reviewImageRequestDtoList.stream().anyMatch(reviewImageRequestDto -> reviewImageRequestDto.getFileStorageId().equals(reviewImage.getFileStorageId())),
                    "????????? ???????????? ?????? ?????? ?????? ?????? ??? ????????? ????????? ?????? ???????????? ID ??? ?????? ????????? ??????.");
        });
        assertEquals(reviewDetailCreateRequestDto.getComment(), findReviewReviewDetail.getComment(), "????????? ???????????? ?????? ?????? ?????? ??? ????????? ???????????? ????????? ??????.");
        assertEquals(ReviewType.DETAIL, findReview.getReviewType(), "?????? ????????? ????????? ????????? ReviewType ??? Detail ????????? ??????.");

    }

    @Test
    @DisplayName("????????? ?????? ?????? ??? ?????? ??????")
    public void addDetailAndSurveyToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewDetailAndSurveyCreateDtoRequestDto reviewDetailAndSurveyCreateDtoRequestDto = createReviewDetailAndSurveyCreateDtoRequestDto(reviewImageRequestDtos, perceivedThemeGenres);

        //when
        reviewApplicationService.addDetailToReview(reviewId, signUpId, reviewDetailAndSurveyCreateDtoRequestDto.toDetailServiceDto());
        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewDetailAndSurveyCreateDtoRequestDto.toSurveyServiceDto());

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
                    "????????? ???????????? ?????? ?????? ?????? ?????? ??? ????????? ????????? ?????? ???????????? ID ??? ?????? ????????? ??????.");
        });
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getComment(), findReviewReviewDetail.getComment(), "????????? ???????????? ?????? ?????? ?????? ??? ????????? ???????????? ????????? ??????.");
        assertEquals(ReviewType.DETAIL, findReview.getReviewType(), "?????? ????????? ????????? ????????? ReviewType ??? Detail ????????? ??????.");

        ReviewSurvey findReviewReviewSurvey = findReview.getReviewSurvey();
        findReviewReviewSurvey.getPerceivedThemeGenres().forEach(findGenre -> assertTrue(perceivedThemeGenres.stream().anyMatch(genre -> genre == findGenre),
                "????????? ????????? ????????? ?????? ?????? ???????????? ?????? ?????? ?????? ??? ????????? ??????????????? ???????????? ????????? ????????? ??????."));
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedDifficulty(), findReviewReviewSurvey.getPerceivedDifficulty());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedHorrorGrade(), findReviewReviewSurvey.getPerceivedHorrorGrade());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getPerceivedActivity(), findReviewReviewSurvey.getPerceivedActivity());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getScenarioSatisfaction(), findReviewReviewSurvey.getScenarioSatisfaction());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getInteriorSatisfaction(), findReviewReviewSurvey.getInteriorSatisfaction());
        assertEquals(reviewDetailAndSurveyCreateDtoRequestDto.getProblemConfigurationSatisfaction(), findReviewReviewSurvey.getProblemConfigurationSatisfaction());
    }

    @Test
    @DisplayName("?????? ??????")
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

        Long member1review1Id = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review2Id = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review3Id = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member1review4Id = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member2review1Id = reviewApplicationService.createReview(member2Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());
        Long member2review2Id = reviewApplicationService.createReview(member2Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

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
        reviewApplicationService.deleteReview(member1Id, member1review2Id);

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
        assertEquals(-1, findMember1Review2.getRecodeNumber(), "????????? ????????? ????????? ????????? -1 ??? ????????? ??????.");
        assertTrue(findMember1Review2.isDeleteYN(), "????????? ????????? deleteYN ??? true ??? ????????? ??????.");

    }

    @ParameterizedTest
    @MethodSource("provideParametersForGetMemberReviewList")
    @DisplayName("?????? ????????? ????????? ?????? ?????? ??????")
    public void getMemberReviewList(ReviewSearchType searchType) {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("37218372189");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        createReviewSampleList(member1Id, themeSample.getId());
        createReviewSampleList(member2Id, themeSample.getId());

        ReviewSearchDto reviewSearchDto = ReviewSearchDto.builder()
                .criteria(new CriteriaDto(1, 100))
                .searchType(searchType)
                .sortCondition(null)
                .build();

        //when
        System.out.println("==============================================================================================================");
        QueryResults<Review> reviewQueryResults = reviewService.getMemberReviewList(member1Id, reviewSearchDto);
        List<Review> findReviews = reviewQueryResults.getResults();
        System.out.println("==============================================================================================================");

        //then
        findReviews.forEach(review -> System.out.println("review = " + review));

        findReviews.forEach(review -> {
            assertFalse(review.isDeleteYN(), "????????? ???????????? ?????? ???????????? ?????? ???????????? ????????? ??????.");
            Member reviewMember = review.getMember();
            assertEquals(member1Id, reviewMember.getId(), "????????? ????????? ?????? member1 ??? ????????? ????????? ??????????????? ??????.");
        });

        for (int i = 0; i < findReviews.size() - 1; i++) {
            Review nowReview = findReviews.get(i);
            Review nextReview = findReviews.get(i + 1);
            assertTrue(nowReview.getRecodeNumber() > nextReview.getRecodeNumber(), "????????? ????????? ?????? ????????? ???????????? ??????????????? ??????.");
        }

        switch (searchType) {
            case FAIL:
                findReviews.forEach(review -> assertFalse(review.isClearYN(), "????????? ?????? ?????? ?????? ?????? ??? ????????? ?????? ????????? ????????? ????????? ??????????????? ??????."));
                break;
            case SUCCESS:
                findReviews.forEach(review -> assertTrue(review.isClearYN(), "????????? ?????? ?????? ?????? ?????? ??? ????????? ?????? ????????? ????????? ????????? ??????????????? ??????."));
                break;
            default:
                List<Boolean> clearYnList = findReviews.stream().map(Review::isClearYN).collect(Collectors.toList());
                boolean containsTrue = clearYnList.contains(true);
                boolean containsFalse = clearYnList.contains(false);
                assertTrue(containsTrue, "????????? ?????? ???????????? ????????? ????????? ????????? ????????? ??????.");
                assertTrue(containsFalse, "????????? ?????? ???????????? ????????? ????????? ????????? ????????? ??????.");
        }

    }

    private void createReviewSampleList(Long member1Id, Long themeSampleId) {
        for (int i = 0; i < 30; i++) {
            boolean clearYN = true;
            LocalTime clearTime = LocalTime.of(0, new Random().nextInt(15) + 30, new Random().nextInt(59));
            if (i % 2 == 0) {
                clearYN = false;
                clearTime = null;
            }

            ReviewCreateRequestDto reviewCreateRequestDto = ReviewCreateRequestDto.builder()
                    .clearYN(clearYN)
                    .clearTime(clearTime)
                    .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                    .rating(new Random().nextInt(8) + 2)
                    .friendIds(null)
                    .build();

            Long reviewId = reviewApplicationService.createReview(member1Id, themeSampleId, reviewCreateRequestDto.toServiceDto());

            if (i % 3 == 0) {
                reviewApplicationService.deleteReview(member1Id, reviewId);
            }
        }
    }

    private static Stream<Arguments> provideParametersForGetMemberReviewList() {
        return Stream.of(
                Arguments.of(ReviewSearchType.TOTAL),
                Arguments.of(ReviewSearchType.SUCCESS),
                Arguments.of(ReviewSearchType.FAIL)
        );
    }

}
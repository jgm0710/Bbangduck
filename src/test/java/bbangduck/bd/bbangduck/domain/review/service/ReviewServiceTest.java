package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberFriendState;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendRepository;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewPerceivedThemeGenre;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("리뷰 생성")
    @Transactional
    public void createReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

        //when
        Long reviewId = reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto);

        //then
        Review findReview = reviewService.getReview(reviewId);

        Member reviewMember = findReview.getMember();
        List<ReviewPerceivedThemeGenre> reviewPerceivedThemeGenres = findReview.getPerceivedThemeGenres();
        Theme reviewTheme = findReview.getTheme();
        List<Genre> reviewThemeGenres = reviewTheme.getGenres();
        List<ReviewImage> reviewImages = findReview.getReviewImages();

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


        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())),
                "생성된 리뷰 이미지 목록에 리뷰 작성 시 등록한 리뷰 이미지가 모두 있어야 한다.");

        reviewPerceivedThemeGenres.forEach(reviewPerceivedThemeGenre -> {
            Genre genre = reviewPerceivedThemeGenre.getGenre();
            System.out.println("reviewPerceivedThemeGenre.getName() = " + genre.getName());
            String code = genre.getCode();
            assertTrue(reviewCreateDto.getGenreCodes().stream().anyMatch(genreCode -> genreCode.equals(code)),
                    "생성된 리뷰의 체감 테마 목록 안에는 리뷰 생성 요청 시 등록된 체감 장르가 모두 있어야한다.");
        });

        findReview.getPlayTogetherMembers().forEach(member -> {
            System.out.println("Play together member  = " + member.toString());
            boolean reviewPlayTogetherExists = friendIds.stream().anyMatch(friendId -> member.getId().equals(friendId));
            assertTrue(reviewPlayTogetherExists, "생성된 리뷰의 함께 플레이한 친구 목록에 생성 요청 시 등록한 친구 id 가 모두 포함되어 있어야 한다.");
        });

        assertEquals(1, findReview.getRecodeNumber(), "해당 회원은 리뷰를 처음 생성했으므로, 생성된 리뷰의 번호는 1번");
        assertEquals(reviewCreateDto.isClearYN(), findReview.isClearYN());
    }

    @Test
    @DisplayName("리뷰 생성 - 리뷰를 두번 생성할 경우 2번째 생성된 리뷰의 recode number 가 2인지 검증")
    public void createReview_recodeNumberTest() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

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

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

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

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

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
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

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

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

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
    @DisplayName("리뷰 생성 시 체감 테마 장르를 등록하는데, 해당 장르가 실제 존재하지 않는 장르일 경우")
    public void createReview_PerceivedThemeGenreNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSignUpRequestDto, signUpId);

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        genreCodes.add("AMGN1");
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

        //when

        //then
        assertThrows(GenreNotFoundException.class, () -> reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));


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


        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        List<FileStorage> storedFiles = List.of(storedFile);

        List<String> genreCodes = createGenreCodes();
        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFiles, friendIds, genreCodes);

        //when

        //then
        assertThrows(RelationOfMemberAndFriendIsNotFriendException.class, () -> reviewService.createReview(signUpId, savedTheme.getId(), reviewCreateDto));

    }

}
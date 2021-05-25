package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewPerceivedThemeGenre;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewServiceTest extends BaseJGMServiceTest {

    @Test
    @DisplayName("리뷰 생성")
    @Transactional
    public void createReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme savedTheme = createTheme();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        ReviewCreateDto reviewCreateDto = createReviewCreateDto(storedFile);

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

        reviewThemeGenres.forEach(genre -> {
            System.out.println("themeGenre.getName() = " + genre.getName());
            List<Genre> savedThemeGenres = savedTheme.getGenres();
            assertTrue(savedThemeGenres.stream().anyMatch(genre1 -> genre1.getId().equals(genre.getId())),
                    "생성된 리뷰의 테마의 장르 목록 안에는 리뷰 생성 요청된 테마에 등록된 장르 목록 중 하나가 있어야한다.");
        });

        assertTrue(reviewImages.stream().anyMatch(reviewImage -> reviewImage.getFileStorageId().equals(storedFile.getId())));

        reviewPerceivedThemeGenres.forEach(reviewPerceivedThemeGenre -> {
            Genre genre = reviewPerceivedThemeGenre.getGenre();
            System.out.println("reviewPerceivedThemeGenre.getName() = " + genre.getName());
            String code = genre.getCode();
            assertTrue(reviewCreateDto.getGenreCodes().stream().anyMatch(genreCode -> genreCode.equals(code)),
                    "생성된 리뷰의 체감 테마 목록 안에는 리뷰 생성 요청 시 등록된 장르가 있어야한다.");
        });


    }

    private ReviewCreateDto createReviewCreateDto(FileStorage storedFile) {
        List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
        reviewImageDtoList.add(new ReviewImageDto(storedFile.getId(), storedFile.getFileName()));

        List<String> genreCodes = new ArrayList<>();
        genreCodes.add("RSN1");
        genreCodes.add("RMC1");

        return ReviewCreateDto.builder()
                .reviewType(ReviewType.DEEP)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .reviewImages(reviewImageDtoList)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n" +
                        "일반모드로 하실분들은 2인이 최적입니다.")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.NORMAL)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.BAD)
                .build();
    }

    private Theme createTheme() {
        Theme theme = Theme.builder()
                .shop(null)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        Genre rsn1 = genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new);
        theme.addGenre(rsn1);

        return themeRepository.save(theme);
    }

}
package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewDetailCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewSurveyCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewUpdateBaseDto;
import bbangduck.bd.bbangduck.domain.review.entity.*;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.exception.DetailIsAlreadyRegisteredInReviewException;
import bbangduck.bd.bbangduck.domain.review.exception.ExpirationOfReviewSurveyAddPeriodException;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewCreatedByOtherMembersException;
import bbangduck.bd.bbangduck.domain.review.exception.SurveyIsAlreadyRegisteredInReviewException;
import bbangduck.bd.bbangduck.domain.review.repository.*;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("ReviewService 단위 테스트")
class ReviewServiceUnitTest {

    ReviewRepository reviewRepository = mock(ReviewRepository.class);
    ReviewQueryRepository reviewQueryRepository = mock(ReviewQueryRepository.class);
    ReviewImageRepository reviewImageRepository = mock(ReviewImageRepository.class);
    ReviewDetailRepository reviewDetailRepository = mock(ReviewDetailRepository.class);
    ReviewPlayTogetherRepository reviewPlayTogetherRepository = mock(ReviewPlayTogetherRepository.class);
    ReviewProperties reviewProperties = mock(ReviewProperties.class);


    ReviewService reviewService = new ReviewService(
            reviewRepository,
            reviewQueryRepository,
            reviewImageRepository,
            reviewDetailRepository,
            reviewPlayTogetherRepository,
            reviewProperties
    );

    @Test
    @DisplayName("조작하는 리뷰가 자신의 리뷰인지 검증")
    public void checkIfMyReview() {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .member(member)
                .build();

        Long othersMemberId = 1000L;

        //when

        //then
        assertThrows(ReviewCreatedByOtherMembersException.class, () -> reviewService.checkIfMyReview(othersMemberId, review));

    }

    @Test
    @DisplayName("리뷰 기본 정보 수정")
    public void updateReviewBase() {
        //given
        Review review = Review.builder()
                .reviewType(ReviewType.DETAIL)
                .clearYN(false)
                .clearTime(null)
                .hintUsageCount(ReviewHintUsageCount.TWO)
                .rating(1)
                .build();

        ReviewUpdateBaseDto reviewUpdateBaseDto = new ReviewUpdateBaseDto(ReviewType.BASE,
                true,
                LocalTime.of(1, 0),
                ReviewHintUsageCount.THREE_OR_MORE,
                4);

        //when
        reviewService.updateReviewBase(review, reviewUpdateBaseDto);

        //then
        assertEquals(reviewUpdateBaseDto.getReviewType(), review.getReviewType());
        assertEquals(reviewUpdateBaseDto.isClearYN(), review.isClearYN());
        assertEquals(reviewUpdateBaseDto.getClearTime(), review.getClearTime());
        assertEquals(reviewUpdateBaseDto.getHintUsageCount(), review.getHintUsageCount());
        assertEquals(reviewUpdateBaseDto.getRating(), review.getRating());

    }

    @Test
    @DisplayName("리뷰 상세 추가")
    public void addDetailToReview() {
        //given
        Review review = Review.builder()
                .deleteYN(false)
                .build();

        ReviewImageDto reviewImageDto1 = new ReviewImageDto(1L, "fileName1");
        ReviewImageDto reviewImageDto2 = new ReviewImageDto(2L, "fileName2");

        List<ReviewImageDto> reviewImageDtos = List.of(reviewImageDto1, reviewImageDto2);
        ReviewDetailCreateDto reviewDetailCreateDto = new ReviewDetailCreateDto(reviewImageDtos, "comment");


        //when
        reviewService.addDetailToReview(review, reviewDetailCreateDto);

        //then
        ReviewDetail reviewDetail = review.getReviewDetail();
        List<ReviewImage> reviewImages = reviewDetail.getReviewImages();

        reviewImages.forEach(reviewImage -> {
            Long fileStorageId = reviewImage.getFileStorageId();
            String fileName = reviewImage.getFileName();

            boolean anyMatch = reviewImageDtos.stream().anyMatch(reviewImageDto -> {
                Long dtoFileStorageId = reviewImageDto.getFileStorageId();
                String dtoFileName = reviewImageDto.getFileName();

                boolean fileStorageIdMatch = dtoFileStorageId.equals(fileStorageId);
                boolean fileNameMatch = dtoFileName.equals(fileName);

                return fileStorageIdMatch && fileNameMatch;
            });

            assertTrue(anyMatch, "리뷰의 리뷰 상세의 이미지 목록 중 하나의 파일 저장소 ID 와 파일 이름이 " +
                    "리뷰 상세 추가 시 기입한 이미지 정보 목록 중 하나의 파일 저장소 ID, 파일 이름과 일치해야 한다.");
        });

        assertEquals(reviewDetailCreateDto.getComment(), reviewDetail.getComment(), "리뷰의 리뷰 상세의 코멘트는 리뷰 상세 추가 시 기입한 코멘트와 같아야 한다.");
    }

    @Test
    @DisplayName("리뷰 상세 추가 - 이미 리뷰 상세가 등록된 리뷰일 경우")
    public void addDetailToReview_DetailAlreadyRegisteredInReview() {
        //given
        Review review = Review.builder()
                .id(1L)
                .build();

        ReviewDetail reviewDetail = ReviewDetail.builder()
                .id(1L)
                .build();

        review.addReviewDetail(reviewDetail);

        ReviewDetailCreateDto reviewDetailCreateDto = ReviewDetailCreateDto.builder().build();

        //when

        //then
        assertThrows(DetailIsAlreadyRegisteredInReviewException.class, () -> reviewService.addDetailToReview(review, reviewDetailCreateDto));

    }

    @Test
    @DisplayName("리뷰에 설문 추가 - 이미 설문이 등록되어 있는 리뷰인 경우")
    public void addSurveyToReview_SurveyAlreadyRegisteredInReview() {
        //given
        Review review = Review.builder()
                .id(1L)
                .build();

        ReviewSurvey reviewSurvey = ReviewSurvey.builder()
                .id(1L)
                .build();

        review.addReviewSurvey(reviewSurvey);

        ReviewSurveyCreateDto reviewSurveyCreateDto = ReviewSurveyCreateDto.builder().build();

        //when

        //then
        assertThrows(SurveyIsAlreadyRegisteredInReviewException.class, () -> reviewService.addSurveyToReview(review, reviewSurveyCreateDto));
    }

    @Test
    @DisplayName("리뷰에 설문 추가 - 설문 등록 가능 기간이 지난 경우")
    public void addSurveyToReview_ExpirationOfReviewSurveyAddPeriod() {
        //given
        long periodForAddingSurveys = 7;

        Review review = Review.builder()
                .id(1L)
                .registerTimes(LocalDateTime.now().minusDays(periodForAddingSurveys + 2))
                .build();

        ReviewSurveyCreateDto reviewSurveyCreateDto = ReviewSurveyCreateDto.builder().build();


        //when

        //then
        assertThrows(ExpirationOfReviewSurveyAddPeriodException.class, () -> reviewService.addSurveyToReview(review, reviewSurveyCreateDto));

    }

    @Test
    @DisplayName("리뷰에 친구 목록 등록")
    public void addPlayTogetherFriendsToReview() {
        //given
        Review review = Review.builder()
                .id(1L)
                .build();

        Member friend1 = Member.builder()
                .id(1L)
                .build();

        Member friend2 = Member.builder()
                .id(2L)
                .build();

        Member friend3 = Member.builder()
                .id(3L)
                .build();

        Member friend4 = Member.builder()
                .id(4L)
                .build();

        List<Member> friends = List.of(friend1, friend2, friend3, friend4);

        //when
        reviewService.addPlayTogetherFriendsToReview(review, friends);

        //then
        List<ReviewPlayTogether> reviewPlayTogetherEntities = review.getReviewPlayTogetherEntities();
        reviewPlayTogetherEntities.forEach(reviewPlayTogether -> {
            Review reviewPlayTogetherReview = reviewPlayTogether.getReview();
            Member reviewPlayTogetherMember = reviewPlayTogether.getMember();

            assertEquals(review.getId(), reviewPlayTogetherReview.getId(), "리뷰의 함께 플레이한 친구 Entity 의 리뷰는 항상 자기 자신이어야 한다.");
            boolean anyMatch = friends.stream().anyMatch(member -> member.getId().equals(reviewPlayTogetherMember.getId()));
            assertTrue(anyMatch, "리뷰의 함께 플레이한 회원 중 하나는 리뷰 함께 플레이한 친구 등록 시 기입한 회원 중 한명이 포함되어 있어야 한다.");
        });
    }

    @Test
    @DisplayName("특정 회원이 테마에 리뷰를 생성한 내역이 있는지 조회")
    public void isExistReviewHistory() {
        //given
        Long memberId = 1L;
        Long themeId = 1L;
        given(reviewQueryRepository.getReviewsCountByMemberIdAndThemeId(memberId,themeId)).willReturn(0L);

        //when
        boolean existReviewHistory = reviewService.isExistReviewHistory(memberId, themeId);

        //then
        assertFalse(existReviewHistory, "리뷰 내역이 0일 경우 리뷰는 존재하지 않음");
    }

}
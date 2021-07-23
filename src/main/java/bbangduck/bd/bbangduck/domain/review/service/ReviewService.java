package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.*;
import bbangduck.bd.bbangduck.domain.review.entity.*;
import bbangduck.bd.bbangduck.domain.review.exception.*;
import bbangduck.bd.bbangduck.domain.review.repository.*;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.existsList;
import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰와 관련된 비즈니스 로직을 정의하기 위해 구현한 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewDetailRepository reviewDetailRepository;
    private final ReviewPlayTogetherRepository reviewPlayTogetherRepository;
    private final ReviewProperties reviewProperties;

    @Transactional
    public Long saveReview(Member member, Theme theme, ReviewCreateDto reviewCreateDto) {
        ReviewRecodesCountsDto recodesCountsDto = getReviewRecodesCounts(member.getId());
        Review review = Review.create(member, theme, recodesCountsDto.getNextRecodeNumber(), reviewCreateDto);
        Review savedReview = reviewRepository.save(review);

        return savedReview.getId();
    }

    @Transactional
    public void addDetailToReview(Review review, ReviewDetailCreateDto reviewDetailCreateDto) {
        checkIfDetailAlreadyRegisteredInReview(review);

        ReviewDetail reviewDetail = ReviewDetail.create(reviewDetailCreateDto);
        review.addReviewDetail(reviewDetail);
    }

    private void checkIfDetailAlreadyRegisteredInReview(Review review) {
        if (isNotNull(review.getReviewDetail())) {
            throw new DetailIsAlreadyRegisteredInReviewException();
        }
    }

    @Transactional
    public void clearReviewDetail(Review review) {
        ReviewDetail reviewDetail = review.getReviewDetail();
        if (isNotNull(reviewDetail)) {
            reviewDetailRepository.delete(reviewDetail);
        }
        review.clearDetail();
    }

    @Transactional
    public void addSurveyToReview(Review review, ReviewSurveyCreateDto reviewSurveyCreateDto) {
        checkIfSurveyAlreadyRegisteredInReview(review);
        checkIfReviewCanAddSurvey(review.getRegisterTimes());

        ReviewSurvey reviewSurvey = ReviewSurvey.create(reviewSurveyCreateDto);

        review.addReviewSurvey(reviewSurvey);
    }

    private void checkIfSurveyAlreadyRegisteredInReview(Review review) {
        if (isNotNull(review.getReviewSurvey())) {
            throw new SurveyIsAlreadyRegisteredInReviewException();
        }
    }

    private void checkIfReviewCanAddSurvey(LocalDateTime reviewRegisterTimes) {
        boolean possibleOfAddReviewSurvey = isPossibleOfAddReviewSurvey(reviewRegisterTimes);
        if (!possibleOfAddReviewSurvey) {
            throw new ExpirationOfReviewSurveyAddPeriodException();
        }
    }

    public boolean isPossibleOfAddReviewSurvey(LocalDateTime reviewRegisterTimes) {
        long periodForAddingSurveys = reviewProperties.getPeriodForAddingSurveys();
        LocalDateTime periodForAddingSurveysDateTime = LocalDateTime.now().minusDays(periodForAddingSurveys);
        return reviewRegisterTimes.isAfter(periodForAddingSurveysDateTime);
    }

//    // TODO: 2021-06-13 우선은 사용하지 않는 기능
//    // TODO: 2021-07-08 추후 사용하는 기능이 된다면, 테마 분석 반영된 부분 삭제, 새로운 테마 분석 반영 로직을 추가해야함
//    @Transactional
//    public void updateSurveyFromReview(Review review, ReviewSurveyUpdateDto reviewSurveyUpdateDto) {
//        if (!isNotNull(review.getReviewSurvey())) {
//            throw new ReviewHasNotSurveyException();
//        }
//
//        checkIfReviewCanAddSurvey(review.getRegisterTimes(), reviewProperties.getPeriodForAddingSurveys());
//
//        review.updateSurvey(reviewSurveyUpdateDto);
//        updatePerceivedGenresFromReviewSurvey(review.getReviewSurvey(), reviewSurveyUpdateDto.getGenreCodes());
//    }
//
//    private void updatePerceivedGenresFromReviewSurvey(ReviewSurvey reviewSurvey, List<String> genreCodes) {
//        reviewPerceivedThemeGenreRepository.deleteInBatch(reviewSurvey.getPerceivedThemeGenreEntities());
//        addPerceivedGenresToReviewSurvey(reviewSurvey, genreCodes);
//    }

    public Review getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        if (review.isDeleteYN()) {
            throw new ManipulateDeletedReviewsException();
        }
        return review;
    }

    public QueryResults<Review> getThemeReviewList(Long themeId, ReviewSearchDto reviewSearchDto) {
        return reviewQueryRepository.findListByTheme(themeId, reviewSearchDto);
    }

    public ReviewRecodesCountsDto getReviewRecodesCounts(Long memberId) {
        return reviewQueryRepository.findRecodesCountsByMember(memberId).orElse(new ReviewRecodesCountsDto());
    }

    // TODO: 2021-06-13 필요 없으면 삭제
    // TODO: 2021-06-13 test
    /**
     * 기능 테스트
     * - 리뷰 상세가 잘 변경되는지 확인
     * -- 기존에 있던 리뷰 이미지가 잘 삭제되는지
     * -- 기존에 있었고 수정 후에도 있는 이미지가 잘 등록되어 있는지
     * -- 새로 입력한 이미지가 잘 등록되어 있는지 확인
     * -- 코멘트가 잘 변경되어 있는지 확인
     *
     * 실패 테스트
     * - 리뷰를 찾을 수 없는 경우
     * - 삭제된 리뷰일 경우
     * - 리뷰 상세가 등록되어 있지 않을 경우
     */
    @Transactional
    public void updateDetailFromReview(Long reviewId, ReviewDetailUpdateDto reviewDetailUpdateDto) {
        Review review = getReview(reviewId);
        ReviewDetail reviewDetail = review.getReviewDetail();

        if (!isNotNull(reviewDetail)) {
            throw new ReviewHasNotDetailException();
        }

        clearReviewImages(reviewDetail);

        reviewDetail.update(reviewDetailUpdateDto);
    }
    // TODO: 2021-06-13 필요 없으면 삭제
    private void clearReviewImages(ReviewDetail reviewDetail) {
        if (isNotNull(reviewDetail)) {
            List<ReviewImage> reviewImages = reviewDetail.getReviewImages();
            reviewImageRepository.deleteInBatch(reviewImages);
        }
    }

    /**
     * 기능 테스트 o
     * - 리뷰가 제대로 삭제 상태가 되는지 확인
     * -- 삭제된 리뷰의 레코드 번호가 -1 로 잘 저장되는지 확인
     *
     * - 해당 회원이 생성한 리뷰의 레코드 번호만 잘 감소하는지 확인
     * -- 다른 회원의 레코드 번호는 감소하면 안됨
     *
     * TODO: 2021-06-12 실패 테스트 미완
     * 오류 테스트
     * - 리뷰를 찾을 수 없는 경우
     * - 이미 삭제된 리뷰일 경우
     * fixme : 리뷰 삭제 시 레코드 변호 update 하지 않도록 변경
     */
    // TODO: 2021-07-22 리뷰 삭제 시 테마 플레이 회원의 리뷰 좋아요 수 감소
    // TODO: 2021-07-22 리뷰 삭제 시 테마에 리뷰에 해당하는 회원이 해당 테마에 리뷰를 생성한 내역이 없을 경우 테마 플레이 내역 삭제
    @Transactional
    public void deleteReview(Review review) {
        long updateCount = reviewQueryRepository.decreaseRecodeNumberWhereInGreaterThenThisRecodeNumber(review.getId(), review.getRecodeNumber());
        log.debug("decreaseRecodeNumberWhereInGreaterThenThisRecodeNumber update count : {}", updateCount);
        review.delete();
    }

    public QueryResults<Review> getMemberReviewList(Long memberId, ReviewSearchDto reviewSearchDto) {
        return reviewQueryRepository.findListByMember(memberId, reviewSearchDto);
    }

    public void checkIfMyReview(Long authenticatedMemberId, Review review) {
        Member reviewMember = review.getMember();
        if (!reviewMember.getId().equals(authenticatedMemberId)) {
            throw new ReviewCreatedByOtherMembersException();
        }
    }

    public void updateReviewBase(Review review, ReviewUpdateBaseDto reviewBaseUpdateDto) {
        review.updateBase(reviewBaseUpdateDto);
    }

    public void addPlayTogetherFriendsToReview(Review review, List<Member> friends) {
        if (existsList(friends)) {
            friends.forEach(review::addPlayTogether);
        }
    }

    @Transactional
    public void clearReviewPlayTogether(Review review) {
        List<ReviewPlayTogether> reviewPlayTogetherEntities = review.getReviewPlayTogetherEntities();
        reviewPlayTogetherRepository.deleteInBatch(reviewPlayTogetherEntities);
        review.clearPlayTogether();
    }

    public boolean isExistReviewHistory(Long memberId, Long themeId) {
        long reviewsCount = reviewQueryRepository.getReviewsCountByMemberIdAndThemeId(memberId, themeId);
        return reviewsCount != 0;
    }
}

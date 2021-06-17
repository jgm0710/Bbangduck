package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.dto.service.*;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewDetail;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewSurvey;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.exception.*;
import bbangduck.bd.bbangduck.domain.review.repository.*;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ManipulateDeletedThemeException;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    private final MemberRepository memberRepository;

    private final MemberPlayInclinationRepository memberPlayInclinationRepository;

    private final MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    private final MemberFriendQueryRepository memberFriendQueryRepository;

    private final ThemeRepository themeRepository;

    private final GenreRepository genreRepository;

    private final ReviewProperties reviewProperties;

    private final ReviewPerceivedThemeGenreRepository reviewPerceivedThemeGenreRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final ReviewPlayTogetherRepository reviewPlayTogetherRepository;

    private final ReviewDetailRepository reviewDetailRepository;

    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Theme findTheme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        throwExceptionIfThemeHasBeenDeleted(findTheme);
        // TODO: 2021-06-16 test 완료되면 주석 삭제
//        ReviewRecodesCountsDto recodesCountsDto = reviewQueryRepository.findRecodesCountsByMember(memberId).orElse(new ReviewRecodesCountsDto());
        ReviewRecodesCountsDto recodesCountsDto = getReviewRecodesCounts(memberId);

        Review review = Review.create(findMember, findTheme, recodesCountsDto.getNextRecodeNumber(), reviewCreateDto);
        addPlayTogetherFriendsToReview(review, memberId, reviewCreateDto.getFriendIds());
        reviewRepository.save(review);

        reflectingPropensityOfMemberToPlay(findMember, findTheme.getGenres());

        return review.getId();
    }

    /**
     * 기능 테스트 o
     * - 리뷰에 리뷰 상세가 잘 등록 되는지 확인
     * -- 이미지가 잘 등록되는지 확인
     * -- 코멘트가 잘 등록되는지 확인
     * - 리뷰 타입이 바뀌는지 확인
     *
     * TODO: 2021-06-13 실패 테스트 미완
     * 실패 테스트
     * - 리뷰가 삭제된 리뷰일 경우
     * - 리뷰를 찾을 수 없는 경우
     * - 이미 리뷰 상세가 등록되어 있는 리뷰일 경우
     */
    @Transactional
    public void addDetailToReview(Long reviewId, ReviewDetailCreateDto reviewDetailCreateDto) {
        Review review = getReview(reviewId);

        if (isNotNull(review.getReviewDetail())) {
            throw new DetailIsAlreadyRegisteredInReviewException();
        }

        ReviewDetail reviewDetail = ReviewDetail.create(reviewDetailCreateDto);
        review.addReviewDetail(reviewDetail);
    }

    // TODO: 2021-06-13 이미 설문이 등록된 리뷰일 경우 예외 발생 테스트 구현
    @Transactional
    public void addSurveyToReview(Long reviewId, ReviewSurveyCreateDto reviewSurveyCreateDto) {
        Review review = getReview(reviewId);

        if (isNotNull(review.getReviewSurvey())) {
            throw new SurveyIsAlreadyRegisteredInReviewException();
        }

        checkIfReviewCanAddSurvey(review.getRegisterTimes(), reviewProperties.getPeriodForAddingSurveys());

        ReviewSurvey reviewSurvey = ReviewSurvey.create(reviewSurveyCreateDto);
        addPerceivedGenresToReviewSurvey(reviewSurvey, reviewSurveyCreateDto.getGenreCodes());

        review.setReviewSurvey(reviewSurvey);
    }

    public Review getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        throwExceptionIfReviewHasBeenDeleted(review);
        return review;
    }

    public QueryResults<Review> getThemeReviewList(Long themeId, ReviewSearchDto reviewSearchDto) {
        return reviewQueryRepository.findListByTheme(themeId, reviewSearchDto);
    }

    public ReviewRecodesCountsDto getReviewRecodesCounts(Long memberId) {
        return reviewQueryRepository.findRecodesCountsByMember(memberId).orElse(new ReviewRecodesCountsDto());
    }

    /**
     * 테스트 완료
     *
     * 기능 테스트
     * - 변경 사항이 잘 저장되는지 o
     * - 간단 리뷰에서 상세 리뷰로 잘 변경되는지 확인 o
     * - 상세 리뷰에서 간단 리뷰로 변경될 경우 reviewImage, comment 가 제대로 null 로 기입되는지 확인 o
     *
     * - 새로 등록한 친구들이 잘 들어 있는지 o
     * - 기존에 있었지만 수정했을 때 등록되지 않는 친구들이 잘 사라져 있는지 o
     *
     * - 새로 등록한 리뷰 이미지가 잘 등록되어 있는지 o
     * - 기존에 등록되어 있었지만 수정했을 때 등록되지 않은 리뷰 이미지들이 잘 사라져 있는지 o
     *
     * 실패
     * - 삭제된 리뷰일 경우 수정 불가 o
     * - 리뷰를 찾을 수 없는 경우 o
     * - 수정 시 등록하는 친구와 리뷰를 작성한 회원이 실제 친구 관계가 아닐 경우 o
     */
    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateDto reviewUpdateDto) {
        Review review = getReview(reviewId);
        Member reviewMember = review.getMember();

        review.clearDetail(reviewDetailRepository);
        review.clearPlayTogether(reviewPlayTogetherRepository);

        review.updateBase(reviewUpdateDto);
        addPlayTogetherFriendsToReview(review, reviewMember.getId(), reviewUpdateDto.getFriendIds());

        ReviewType newReviewType = reviewUpdateDto.getReviewType();
        if (newReviewType == ReviewType.DETAIL) {
            ReviewDetailCreateDto reviewDetailCreateDto = ReviewDetailCreateDto.builder()
                    .reviewImageDtos(reviewUpdateDto.getReviewImages())
                    .comment(reviewUpdateDto.getComment())
                    .build();

            addDetailToReview(reviewId, reviewDetailCreateDto);
        }
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

    // TODO: 2021-06-13 우선은 사용하지 않는 기능
    @Transactional
    public void updateSurveyFromReview(Long reviewId, ReviewSurveyUpdateDto reviewSurveyUpdateDto) {
        Review review = getReview(reviewId);

        if (!isNotNull(review.getReviewSurvey())) {
            throw new ReviewHasNotSurveyException();
        }

        checkIfReviewCanAddSurvey(review.getRegisterTimes(), reviewProperties.getPeriodForAddingSurveys());

        review.updateSurvey(reviewSurveyUpdateDto);
        updatePerceivedGenresFromReviewSurvey(review.getReviewSurvey(), reviewSurveyUpdateDto.getGenreCodes());
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
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = getReview(reviewId);
        long updateCount = reviewQueryRepository.decreaseRecodeNumberWhereInGreaterThenThisRecodeNumber(reviewId, review.getRecodeNumber());
        log.debug("decreaseRecodeNumberWhereInGreaterThenThisRecodeNumber update count : {}", updateCount);
        review.delete();
    }




    private void addPlayTogetherFriendsToReview(Review review, Long memberId, List<Long> friendIds) {
        if (friendIdsExists(friendIds)) {
            friendIds.forEach(friendId -> {
                Optional<MemberFriend> optionalMemberFriend = memberFriendQueryRepository.findAllowedFriendByMemberAndFriend(memberId, friendId);
                if (optionalMemberFriend.isEmpty()) {
                    throw new RelationOfMemberAndFriendIsNotFriendException(memberId, friendId);
                }
                MemberFriend memberFriend = optionalMemberFriend.get();
                review.addPlayTogether(memberFriend.getFriend());
            });
        }
    }

    private boolean friendIdsExists(List<Long> friendIds) {
        return friendIds != null && !friendIds.isEmpty();
    }

    private void reflectingPropensityOfMemberToPlay(Member member, List<Genre> themeGenres) {
        themeGenres.forEach(genre -> {
            Optional<MemberPlayInclination> optionalMemberPlayInclination = memberPlayInclinationQueryRepository.findOneByMemberAndGenre(member.getId(), genre.getId());
            if (optionalMemberPlayInclination.isPresent()) {
                MemberPlayInclination memberPlayInclination = optionalMemberPlayInclination.get();
                memberPlayInclination.increasePlayCount();
            } else {
                MemberPlayInclination newPlayInclination = MemberPlayInclination.builder()
                        .member(member)
                        .genre(genre)
                        .playCount(1)
                        .build();

                memberPlayInclinationRepository.save(newPlayInclination);
            }
        });
    }

    private void throwExceptionIfReviewHasBeenDeleted(Review review) {
        if (review.isDeleteYN()) {
            throw new ManipulateDeletedReviewsException();
        }
    }

    private void checkIfReviewCanAddSurvey(LocalDateTime reviewRegisterTimes,long periodForAddingSurveys) {
        LocalDateTime periodForAddingSurveysDateTime = LocalDateTime.now().minusDays(periodForAddingSurveys);
        if (reviewRegisterTimes.isBefore(periodForAddingSurveysDateTime)) {
            throw new ExpirationOfReviewSurveyAddPeriodException(reviewRegisterTimes, periodForAddingSurveysDateTime);
        }
    }

    private void addPerceivedGenresToReviewSurvey(ReviewSurvey reviewSurvey, List<String> genreCodes) {
        checkIfGenreCodeExists(genreCodes);
        genreCodes.forEach(genreCode -> {
            Genre genre = genreRepository.findByCode(genreCode).orElseThrow(() -> new GenreNotFoundException(genreCode));
            reviewSurvey.addPerceivedThemeGenre(genre);
        });
    }

    private void checkIfGenreCodeExists(List<String> genreCodes) {
        if (!existsList(genreCodes)) {
            throw new NoGenreToRegisterForReviewSurveyException();
        }
    }

    private void updatePerceivedGenresFromReviewSurvey(ReviewSurvey reviewSurvey, List<String> genreCodes) {
        reviewPerceivedThemeGenreRepository.deleteInBatch(reviewSurvey.getPerceivedThemeGenreEntities());
        addPerceivedGenresToReviewSurvey(reviewSurvey, genreCodes);
    }

    private void throwExceptionIfThemeHasBeenDeleted(Theme theme) {
        if (theme.isDeleteYN()) {
            throw new ManipulateDeletedThemeException();
        }
    }

    /**
     * 기능 테스트 o
     * - 기존 리뷰 내용이 변경되지는 않았는지 확인
     * - 리뷰 타입이 detail 로 잘 바뀌었는지 확인
     * - 추가한 detail 값들이 잘 들어가 있는지 확인
     * - 추가한 survey 값들이 잘 들어가 있는지 확인
     *
     * TODO: 2021-06-13 실패 테스트 미완
     * 실패 테스트
     * * 다른 메서드들을 그대로 가져와서 사용하는 것이므로 해당 메서드들 검증이 완료되면 굳이 안해도 됨
     * - 리뷰를 찾을 수 없는 경우
     * - 장르를 찾을 수 없는 경우
     * - 이미 리뷰 상세가 등록된 설문일 경우
     * - 이미 설문이 등록된 리뷰일 경우
     * - 설문 등록 가능 기간이 지난 경우
     */
    @Transactional
    public void addDetailAndSurveyToReview(Long reviewId, ReviewDetailCreateDto reviewDetailCreateDto, ReviewSurveyCreateDto reviewSurveyCreateDto) {
        addDetailToReview(reviewId, reviewDetailCreateDto);
        addSurveyToReview(reviewId, reviewSurveyCreateDto);
    }

    /**
     * 테스트 완료
     *
     * 기능 테스트
     * - 삭제된 목록은 나오지 않는지 확인
     * - total 이면 전체 리뷰가 나오는지 확인
     * - success 이면 성공한 리뷰만 나오는지 확인
     * - fail 이면 실패한 리뷰만 나오는지 확인
     * - 레코드 번호 역순으로 나오는지 확인
     * - 특정 회원의 리뷰 목록만 나오는지 확인
     */
    public QueryResults<Review> getMemberReviewList(Long memberId, ReviewSearchDto reviewSearchDto) {
        return reviewQueryRepository.findListByMember(memberId, reviewSearchDto);
    }
}

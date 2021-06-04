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
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.dto.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewQueryRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewSearchDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰와 관련된 비즈니스 로직을 정의하기 위해 구현한 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReviewQueryRepository reviewQueryRepository;

    private final MemberRepository memberRepository;

    private final MemberPlayInclinationRepository memberPlayInclinationRepository;

    private final MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    private final MemberFriendQueryRepository memberFriendQueryRepository;

    private final ThemeRepository themeRepository;

    private final GenreRepository genreRepository;

    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Theme findTheme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        ReviewRecodesCountsDto recodesCountsDto = reviewQueryRepository.findRecodesCountsByMember(memberId).orElse(new ReviewRecodesCountsDto());

        Review review = Review.create(findMember, findTheme, recodesCountsDto.getNextRecodeNumber(), reviewCreateDto);
        addPerceivedGenresToReview(review, reviewCreateDto.getGenreCodes());
        addPlayTogetherFriendsToReview(review, memberId, reviewCreateDto.getFriendIds());
        reviewRepository.save(review);

        reflectingPropensityOfMemberToPlay(findMember, findTheme.getGenres());

        return review.getId();
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
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

    private void addPerceivedGenresToReview(Review review, List<String> genreCodes) {
        if (genreCodesExists(genreCodes)) {
            genreCodes.forEach(genreCode -> {
                Genre genre = genreRepository.findByCode(genreCode).orElseThrow(() -> new GenreNotFoundException(genreCode));
                review.addPerceivedThemeGenre(genre);
            });
        }
    }

    private boolean genreCodesExists(List<String> genreCodes) {
        return genreCodes != null&&!genreCodes.isEmpty();
    }

    public QueryResults<Review> getThemeReviewList(Long themeId, ReviewSearchDto reviewSearchDto) {
        return reviewQueryRepository.findListByTheme(themeId, reviewSearchDto);
    }
}

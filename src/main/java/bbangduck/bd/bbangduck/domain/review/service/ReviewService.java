package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// TODO: 2021-05-23 주석 달기
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MemberRepository memberRepository;

    private final MemberPlayInclinationRepository memberPlayInclinationRepository;

    private final MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    private final ThemeRepository themeRepository;

    private final GenreRepository genreRepository;

    // TODO: 2021-05-25 Test
    /**
     * 회원을 찾을 수 없는 경우
     * 테마를 찾을 수 없는 경우
     * 테마에 장르가 등록되어 있지 않은 경우
     * 기존에 회원 성향에 테마의 장르가 있었던 경우 잘 증가하는지
     * 기존에 회원 성향에 테마의 장르가 없었떤 경우 잘 생성되는지
     * 리뷰 체감 장르가 잘 등록되는지
     * 장르 코드로 장르를 찾을 수 없는 경우
     * 리뷰 체감 장르가 잘 저장되는지
     */
    @Transactional
    public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Theme findTheme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        List<Genre> themeGenres = findTheme.getGenres();

        Review review = Review.create(findMember, findTheme, reviewCreateDto);
        reviewRepository.save(review);

        List<String> genreCodes = reviewCreateDto.getGenreCodes();
        genreCodes.forEach(genreCode -> {
            Genre genre = genreRepository.findByCode(genreCode).orElseThrow(() -> new GenreNotFoundException(genreCode));
            review.addPerceivedThemeGenre(genre);
        });

        themeGenres.forEach(genre -> {
            Optional<MemberPlayInclination> optionalMemberPlayInclination = memberPlayInclinationQueryRepository.findOneByMemberAndGenre(memberId, genre.getId());
            if (optionalMemberPlayInclination.isPresent()) {
                MemberPlayInclination memberPlayInclination = optionalMemberPlayInclination.get();
                memberPlayInclination.increasePlayCount();
            } else {
                MemberPlayInclination newPlayInclination = MemberPlayInclination.builder()
                        .member(findMember)
                        .genre(genre)
                        .playCount(1)
                        .build();

                memberPlayInclinationRepository.save(newPlayInclination);
            }
        });

        return review.getId();
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
    }
}

package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.auth.dto.service.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewNotFoundException;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ReviewService 통합 테스트")
class ReviewServiceIntegrationTest extends BaseTest {

    @Autowired
    ReviewService reviewService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 저장")
    public void saveReview() {
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("sritmember321321@email.com")
                .nickname("sritmember3217893")
                .build();
        Member member = Member.signUp(memberSignUpDto, 14);

        Member savedMember = memberRepository.save(member);


        Genre genre = Genre.builder()
                .code("SRITGR1")
                .name("장르")
                .build();
        Genre savedGenre = genreRepository.save(genre);

        Theme theme = Theme.builder()
                .name("theme")
                .build();
        theme.addGenre(savedGenre);

        Theme savedTheme = themeRepository.save(theme);

        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(true, LocalTime.of(0, 40), ReviewHintUsageCount.TWO, 4, null);


        //when
        Long savedReviewId = reviewService.saveReview(savedMember, savedTheme, reviewCreateDto);

        //then
        Review savedReview = reviewRepository.findById(savedReviewId).orElseThrow(ReviewNotFoundException::new);

        assertEquals(1, savedReview.getRecodeNumber());
        Member savedReviewMember = savedReview.getMember();
        assertEquals(savedMember.getId(), savedReviewMember.getId());
        Theme savedReviewTheme = savedReview.getTheme();
        assertEquals(savedTheme.getId(), savedReviewTheme.getId());
        assertEquals(reviewCreateDto.isClearYN(), savedReview.isClearYN());
        assertEquals(reviewCreateDto.getClearTime(), savedReview.getClearTime());
        assertEquals(reviewCreateDto.getHintUsageCount(), savedReview.getHintUsageCount());

        //final
        reviewRepository.delete(savedReview);
        themeRepository.delete(savedTheme);
        genreRepository.delete(genre);
        memberRepository.delete(savedMember);
    }

}
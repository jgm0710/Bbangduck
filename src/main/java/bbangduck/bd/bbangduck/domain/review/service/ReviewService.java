package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// TODO: 2021-05-23 주석 달기
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MemberRepository memberRepository;

    private final MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    private final ThemeRepository themeRepository;

    // TODO: 2021-05-23 회원 성향 반영
    @Transactional
    public void createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Theme findTheme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);
        Optional<MemberPlayInclination> optionalMemberPlayInclination = memberPlayInclinationQueryRepository.findOneByMemberAndGenre(memberId, findTheme.getGenreCode());
        if (optionalMemberPlayInclination.isPresent()) {
            MemberPlayInclination memberPlayInclination = optionalMemberPlayInclination.get();
            memberPlayInclination.increasePlayCount();
        }

        Review review = Review.create(findMember, findTheme, reviewCreateDto);
        reviewRepository.save(review);
    }
}

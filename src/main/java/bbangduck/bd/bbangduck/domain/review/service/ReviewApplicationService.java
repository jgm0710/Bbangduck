package bbangduck.bd.bbangduck.domain.review.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewDetailCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.entity.ReviewRecodesCountsDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.exception.ReviewCreatedByOtherMembersException;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.exception.ThemeNotFoundException;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewApplicationService {

  private final ReviewService reviewService;
  private final MemberService memberService;
  private final ThemeService themeService;


  @Transactional
  public void addDetailToReview(Long reviewId, Long memberId, ReviewDetailCreateRequestDto requestDto) {
    Review findReview = reviewService.getReview(reviewId);
    if (memberService.isEqualsMember(memberId, findReview.getMember())) {
      throw new ReviewCreatedByOtherMembersException(ResponseStatus.ADD_DETAIL_TO_REVIEW_CREATED_BY_OTHER_MEMBERS);
    }
    reviewService.addDetailToReview(reviewId, requestDto.toServiceDto());
  }

  public Long createReview(Long memberId, Long themeId, ReviewCreateDto reviewCreateDto) {
    Member member = memberService.getMember(memberId);
    Theme theme = themeService.getTheme(themeId);
    int recodeNumber = reviewService.getRecordNumberByMemberId(memberId);

    Review review = reviewService.createReview(member, theme, recodeNumber, reviewCreateDto);
    reviewService.addPlayTogetherFriendsToReview(review,member,reviewCreateDto.getFriendIds());
    reviewService.reflectingPropensityOfMemberToPlay(member,theme.getGenres());
    return review.getId();
  }
}

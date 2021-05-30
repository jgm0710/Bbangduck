package bbangduck.bd.bbangduck.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원과 관련된 리뷰 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/reviews")
public class MemberReviewApiController {

    // TODO: 2021-05-22 특정 회원이 작성한 리뷰 목록 기능 구현

}

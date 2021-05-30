package bbangduck.bd.bbangduck.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 자체에 대한 요청 API 를 구현하기 위한 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewApiController {

    // TODO: 2021-05-29 리뷰 한 건 조회 로직 만들기
    @GetMapping("/{reviewId}")
    public ResponseEntity getReview(
            @PathVariable Long reviewId
    ) {
        return null;
    }

    // TODO: 2021-05-22 리뷰 수정 기능 구현

    // TODO: 2021-05-22 리뷰 삭제 기능 구현
}

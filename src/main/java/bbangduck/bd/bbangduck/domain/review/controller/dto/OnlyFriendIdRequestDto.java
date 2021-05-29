package bbangduck.bd.bbangduck.domain.review.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 작성, 수정 요청 등에서 함께 플레이한 친구 추가 등의 조작을 하기 위해 친구 ID 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnlyFriendIdRequestDto {

    private Long friendId;

}

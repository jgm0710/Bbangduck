package bbangduck.bd.bbangduck.domain.member.dto.controller;

import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 *
 * 회원의 방탈출 기록 공개 상태 수정 요청 Body Data 를 담을 Dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRoomEscapeRecodesOpenStatusUpdateRequestDto {

    @NotNull(message = "변경할 방탈출 공개 상태를 기입해 주세요.")
    private MemberRoomEscapeRecodesOpenStatus roomEscapeRecodesOpenStatus;

}

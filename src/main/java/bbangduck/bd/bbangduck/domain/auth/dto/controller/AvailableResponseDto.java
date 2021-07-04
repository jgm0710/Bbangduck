package bbangduck.bd.bbangduck.domain.auth.dto.controller;

import bbangduck.bd.bbangduck.domain.member.dto.controller.request.CheckIfEmailIsAvailableRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberCheckIfNicknameIsAvailableRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

/**
 * 이메일, 닉테임 사용가능 여부 체크 응답 Body Data 를 담을 Dto
 * <p>
 * {@link bbangduck.bd.bbangduck.domain.auth.controller.AuthApiController#checkIfEmailIsAvailable(CheckIfEmailIsAvailableRequestDto, Errors)},
 * <p>
 * {@link bbangduck.bd.bbangduck.domain.auth.controller.AuthApiController#checkIfNicknameIsAvailable(MemberCheckIfNicknameIsAvailableRequestDto, Errors)}
 *
 * @author jgm
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableResponseDto {

    private Boolean isAvailable;
}

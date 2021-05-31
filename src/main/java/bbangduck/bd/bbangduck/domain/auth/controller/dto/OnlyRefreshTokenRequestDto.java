package bbangduck.bd.bbangduck.domain.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Refresh 요청 시 Refresh Token 만을 요청 Body 를 통해 받기 위해 구현한 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnlyRefreshTokenRequestDto {

    @NotBlank(message = "Refresh Token 을 기입해 주세요.")
    private String refreshToken;
}

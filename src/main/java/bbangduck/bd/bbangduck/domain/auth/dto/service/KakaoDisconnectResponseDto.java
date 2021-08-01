package bbangduck.bd.bbangduck.domain.auth.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오 계정 연동 해제 요청 시 응답 값을 받을 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoDisconnectResponseDto {
    @JsonProperty("id")
    private String kakaoUserId;
}

package bbangduck.bd.bbangduck.domain.auth.dto.service;

import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 네이버 API 를 통한 로그인 시
 * 인증 토큰을 통한 네이버 회원 정보를 조회했을 경우
 * 네이버 회원 정보를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverUserInfoDto implements SocialUserInfoInterface {

    @JsonProperty("resultcode")
    private String resultCode;
    private String message;
    @JsonProperty("response")
    private NaverUserInfoResponseDto response;

    @Override
    public String getSocialId() {
        return response.getId();
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.NAVER;
    }

    @Override
    public String getEmail() {
        return response.getEmail();
    }

    @Override
    public String getNickname() {
        return response.getNickname();
    }
}
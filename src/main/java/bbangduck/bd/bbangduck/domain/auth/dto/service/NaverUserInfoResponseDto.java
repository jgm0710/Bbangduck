package bbangduck.bd.bbangduck.domain.auth.dto.service;

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
public class NaverUserInfoResponseDto {
    private String id;
    private String nickname;
    private String name;
    private String email;
    private String gender;
    private String age;
    private String birthday;
    @JsonProperty("profile_image")
    private String profileImage;
    @JsonProperty("birthyear")
    private String birthYear;
    private String mobile;
}
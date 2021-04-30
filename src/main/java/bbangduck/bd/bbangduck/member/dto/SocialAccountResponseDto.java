package bbangduck.bd.bbangduck.member.dto;

import bbangduck.bd.bbangduck.member.social.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccountResponseDto {

    private Long socialAccountId;

    private String socialId;

    private SocialType socialType;

}

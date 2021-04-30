package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.model.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccountResponseDto {

    private Long socialAccountId;

    private String socialId;

    private SocialType socialType;

}

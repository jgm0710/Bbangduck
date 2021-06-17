package bbangduck.bd.bbangduck.domain.auth.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoAccountDto {
    @JsonProperty("profile_needs_agreement")
    private boolean profileNeedsAgreement;
    @JsonProperty("profile")
    private KakaoProfileDto profile;
    @JsonProperty("email_needs_agreement")
    private boolean emailNeedsAgreement;
    @JsonProperty("is_email_valid")
    private boolean isEmailValid;
    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;
    private String email;

    @Builder
    public KakaoAccountDto(boolean profileNeedsAgreement, String nickname, String thumbnailImageUrl, String profileImageUrl, boolean emailNeedsAgreement, boolean isEmailValid, boolean isEmailVerified, String email) {
        this.profileNeedsAgreement = profileNeedsAgreement;
        this.profile = KakaoProfileDto.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .thumbnailImageUrl(thumbnailImageUrl)
                .build();
        this.emailNeedsAgreement = emailNeedsAgreement;
        this.isEmailValid = isEmailValid;
        this.isEmailVerified = isEmailVerified;
        this.email = email;
    }

    public String getNickname() {
        return profile.getNickname();
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "KakaoAccountDto{" +
                "profileNeedsAgreement='" + profileNeedsAgreement + '\'' +
                ", profile=" + profile +
                ", emailNeedsAgreement=" + emailNeedsAgreement +
                ", isEmailValid=" + isEmailValid +
                ", isEmailVerified=" + isEmailVerified +
                ", email='" + email + '\'' +
                '}';
    }
}

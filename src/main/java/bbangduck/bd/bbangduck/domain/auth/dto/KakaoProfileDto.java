package bbangduck.bd.bbangduck.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoProfileDto {
    private String nickname;
    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;
    @JsonProperty("profile_image_url")
    private String profileImageUrl;


    @Builder
    public KakaoProfileDto(String nickname, String thumbnailImageUrl, String profileImageUrl) {
        this.nickname = nickname;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

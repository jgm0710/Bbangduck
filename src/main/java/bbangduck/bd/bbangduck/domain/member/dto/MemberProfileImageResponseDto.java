package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 이미지 파일에 대한 정보를 응답 Body 에 담기 위해 사용하는 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileImageResponseDto {

    private Long profileImageId;

    private String profileImageUrl;

    private String profileImageThumbnailUrl;

    public static MemberProfileImageResponseDto convert(MemberProfileImage memberProfileImage) {
        if (memberProfileImage == null) {
            return null;
        }

        return MemberProfileImageResponseDto.builder()
                .profileImageId(memberProfileImage.getId())
                .profileImageUrl(memberProfileImage.getFileDownloadUrl())
                .profileImageThumbnailUrl(memberProfileImage.getFileThumbnailDownloadUrl())
                .build();
    }


}

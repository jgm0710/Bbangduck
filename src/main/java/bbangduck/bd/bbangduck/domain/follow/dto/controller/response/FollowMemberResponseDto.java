package bbangduck.bd.bbangduck.domain.follow.dto.controller.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.global.common.NullCheckUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToFileDownload;
import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToImageFileThumbnailDownload;

/**
 * 팔로우 회원에 대한 데이터를 담을 응답 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowMemberResponseDto {

    private Long memberId;

    private String nickname;

    private String description;

    private String profileImageUrl;

    private String profileImageThumbnailUrl;

    public static FollowMemberResponseDto convert(Member member) {
        return FollowMemberResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .description(member.getDescription())
                .profileImageUrl(getProfileImageUrl(member.getProfileImage()))
                .profileImageThumbnailUrl(getProfileImageThumbnailUrl(member.getProfileImage()))
                .build();
    }

    private static String getProfileImageThumbnailUrl(MemberProfileImage profileImage) {
        if (NullCheckUtils.isNotNull(profileImage)) {
            return linkToImageFileThumbnailDownload(profileImage.getFileName());
        }
        return null;
    }

    private static String getProfileImageUrl(MemberProfileImage profileImage) {
        if (NullCheckUtils.isNotNull(profileImage)) {
            return linkToFileDownload(profileImage.getFileName());
        }
        return null;
    }
}

package bbangduck.bd.bbangduck.domain.review.controller.dto.response;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToFileDownload;
import static bbangduck.bd.bbangduck.global.common.LinkToUtils.linkToImageFileThumbnailDownload;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 조회 시 회원에 대한 간단한 정보들을 나타내야 할 경우
 * 회원에 대한 응답 Data 를 담을 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewMemberSimpleInfoResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String profileImageThumbnailUrl;

    public static ReviewMemberSimpleInfoResponseDto convert(Member member) {
        if (member == null) {
            return null;
        }

        String fileName = member.getProfileImageFileName();

        String profileImageUrl = linkToFileDownload(fileName);
        String profileImageThumbnailDownloadUrl = linkToImageFileThumbnailDownload(fileName);

        return ReviewMemberSimpleInfoResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(profileImageUrl)
                .profileImageThumbnailUrl(profileImageThumbnailDownloadUrl)
                .build();
    }
}

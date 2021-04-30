package bbangduck.bd.bbangduck.domain.member.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import bbangduck.bd.bbangduck.global.common.dto.FileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailDto {

    private Long memberId;

    private String email;

    private FileResponseDto profileImage;

    private List<SocialAccountResponseDto> socialAccountList;

    private String nickname;

    private String simpleIntroduction;

    private int reviewCount;

    private LocalDateTime registerDate;

    private LocalDateTime updateDate;

    public static MemberDetailDto memberToDetail(Member member) {

        FileResponseDto profileImageResponseDto = null;
        if (member.getProfileImage() != null) {
            profileImageResponseDto = FileResponseDto.builder()
                    .fileId(member.getProfileImage().getId())
                    .fileName(member.getProfileImage().getFileName())
                    .fileStoragePath(member.getProfileImage().getFileStoragePath())
                    .fileDownloadUrl(member.getProfileImage().getFileDownloadUrl())
                    .fileThumbnailDownloadUrl(member.getProfileImage().getFileThumbnailDownloadUrl())
                    .fileType(member.getProfileImage().getFileType())
                    .fileSize(member.getProfileImage().getFileSize())
                    .build();
        }

        List<SocialAccountResponseDto> socialAccountResponseDtos = new ArrayList<>();
        for (SocialAccount socialAccount : member.getSocialAccountList()) {
            socialAccountResponseDtos.add(SocialAccountResponseDto.builder()
                    .socialAccountId(socialAccount.getId())
                    .socialId(socialAccount.getSocialId())
                    .socialType(socialAccount.getSocialType())
                    .build());
        }

        return MemberDetailDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .profileImage(profileImageResponseDto)
                .socialAccountList(socialAccountResponseDtos)
                .nickname(member.getNickname())
                .simpleIntroduction(member.getSimpleIntroduction())
                .reviewCount(member.getReviewCount())
                .registerDate(member.getRegisterDate())
                .updateDate(member.getUpdateDate())
                .build();
    }

}

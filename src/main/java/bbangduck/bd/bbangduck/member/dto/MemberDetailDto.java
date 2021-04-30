package bbangduck.bd.bbangduck.member.dto;

import bbangduck.bd.bbangduck.common.dto.FileResponseDto;
import bbangduck.bd.bbangduck.member.Member;
import bbangduck.bd.bbangduck.member.MemberProfileImage;
import bbangduck.bd.bbangduck.member.MemberRole;
import bbangduck.bd.bbangduck.member.RefreshInfo;
import bbangduck.bd.bbangduck.member.social.SocialAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

package bbangduck.bd.bbangduck.domain.member.controller.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.SocialAccount;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 상세 정보에 대한 응답 Body 를 담는 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyProfileResponseDto {

    private Long memberId;

    private String email;

    private String nickname;

    private MemberProfileImageResponseDto profileImage;

    private List<SocialAccountResponseDto> socialAccounts;

    private String description;

    private int reviewCount;

    private boolean roomEscapeRecordVisible;

    private LocalDateTime registerDate;

    private LocalDateTime updateDate;

    // TODO: 2021-05-15 자기 프로필 응답 손보기
    // TODO: 2021-05-15 다른 회원의 프로필 조회 응답 Dto 구현
    public static MyProfileResponseDto convert(Member member) {
        return MyProfileResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .profileImage(MemberProfileImageResponseDto.convert(member.getProfileImage()))
                .socialAccounts(convertSocialAccounts(member.getSocialAccounts()))
                .nickname(member.getNickname())
                .description(member.getDescription())
                .roomEscapeRecordVisible(member.isRoomEscapeRecordsOpenYN())
                .registerDate(member.getRegisterDate())
                .updateDate(member.getUpdateDate())
                .build();
    }

    private static List<SocialAccountResponseDto> convertSocialAccounts(List<SocialAccount> socialAccounts) {
        List<SocialAccountResponseDto> socialAccountResponseDtos = new ArrayList<>();
        for (SocialAccount socialAccount : socialAccounts) {
            socialAccountResponseDtos.add(SocialAccountResponseDto.convert(socialAccount));
        }
        return socialAccountResponseDtos;
    }
}
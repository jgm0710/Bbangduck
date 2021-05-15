package bbangduck.bd.bbangduck.domain.member.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 수정 Service 요청 시 필요한 Parameter 를 담을 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateDto {

    private String nickname;

    private String description;

    private boolean roomEscapeRecordsOpenYN;

    private MemberProfileImageDto profileImageDto;


    @Builder
    public MemberUpdateDto(String nickname, String description,boolean roomEscapeRecordsOpenYN, MemberProfileImageDto profileImageDto) {
        this.nickname = nickname;
        this.description = description;
        this.roomEscapeRecordsOpenYN = roomEscapeRecordsOpenYN;
        this.profileImageDto = profileImageDto;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    public MemberProfileImageDto getProfileImageDto() {
        return profileImageDto;
    }

    public boolean isRoomEscapeRecordsOpenYN() {
        return roomEscapeRecordsOpenYN;
    }
}

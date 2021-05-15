package bbangduck.bd.bbangduck.domain.member.controller.dto;

import bbangduck.bd.bbangduck.domain.member.service.dto.MemberUpdateDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 수정 요청에 필요한 정보들을 받을
 * Controller Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateProfileRequestDto {

    @NotBlank(message = "수정할 Nickname 을 입력해 주세요.")
    private String nickname;

    @Length(max = 1000, message = "자기소개는 1000자 이상 작성할 수 없습니다.")
    private String description;

    @NotNull(message = "방탈출 공개 여부를 지정해 주세요.")
    private boolean roomEscapeRecordVisible;

    private MemberProfileImageRequestDto profileImage;

    public Long getProfileImageId() {
        return profileImage.getFileId();
    }

    public String getProfileImageName() {
        return profileImage.getFileName();
    }

    public MemberUpdateDto toServiceDto() {
        return MemberUpdateDto.builder()
                .nickname(this.nickname)
                .description(this.description)
                .profileImageDto(profileImage.toServiceDto())
                .build();
    }
}

package bbangduck.bd.bbangduck.domain.member.controller.dto;

import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 이미지 수정 등의 요청에서 필요한 프로필 이미지 파일의 정보를
 * 담을 Controller Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileImageRequestDto {

    private Long fileStorageId;

    private String fileName;

    public MemberProfileImageDto toServiceDto() {
        return MemberProfileImageDto.builder()
                .fileStorageId(fileStorageId)
                .fileName(fileName)
                .build();
    }

}

package bbangduck.bd.bbangduck.domain.member.controller.dto;

import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// TODO: 21. 5. 17. 주석
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateProfileImageRequestDto {

    @NotNull(message = "파일 저장소 ID 를 기입해 주세요.")
    private Long fileStorageId;

    @NotBlank(message = "파일 이름을 기입해 주세요.")
    private String fileName;

    public MemberProfileImageDto toServiceDto() {
        return MemberProfileImageDto.builder()
                .fileStorageId(fileStorageId)
                .fileName(fileName)
                .build();
    }
}

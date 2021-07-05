package bbangduck.bd.bbangduck.domain.theme.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 테마 이미지와 관련된 요청 Body Data 를 담을 Dto
 *
 * @author jgm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeImageRequestDto {

    @NotNull(message = "파일 저장소 ID 를 기입해 주세요.")
    private Long fileStorageId;

    @NotBlank(message = "파일 이름을 기입해 주세요.")
    private String fileName;

}

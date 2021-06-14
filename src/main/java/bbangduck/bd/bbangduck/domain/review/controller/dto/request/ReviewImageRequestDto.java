package bbangduck.bd.bbangduck.domain.review.controller.dto.request;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성, 수정 요청 등에서 리뷰에 등록할 이미지 파일에 대한 정보를 받을 때 사용할 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageRequestDto {

    @NotNull(message = "파일 저장소 ID 를 기입해 주세요.")
    private Long fileStorageId;

    @NotBlank(message = "파일 이름을 기입해 주세요.")
    private String fileName;

    public boolean fileStorageIdExists() {
        return fileStorageId != null;
    }

    public boolean fileNameExists() {
        if (fileName == null) {
            return false;
        } else {
            return !fileName.isBlank();
        }
    }

    public ReviewImageDto toServiceDto() {
        return ReviewImageDto.builder()
                .fileStorageId(fileStorageId)
                .fileName(fileName)
                .build();
    }

}

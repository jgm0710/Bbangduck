package bbangduck.bd.bbangduck.domain.review.controller.dto;

import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// TODO: 2021-05-23 주석 달기
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
        return fileName != null && !fileName.isBlank();
    }

    public ReviewImageDto toServiceDto() {
        return ReviewImageDto.builder()
                .fileStorageId(fileStorageId)
                .fileName(fileName)
                .build();
    }

}

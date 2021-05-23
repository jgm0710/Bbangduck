package bbangduck.bd.bbangduck.domain.review.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

// TODO: 2021-05-23 주석 달기
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImageDto {

    private Long fileStorageId;

    private String fileName;

    @Builder
    public ReviewImageDto(Long fileStorageId, String fileName) {
        this.fileStorageId = fileStorageId;
        this.fileName = fileName;
    }

    public Long getFileStorageId() {
        return fileStorageId;
    }

    public String getFileName() {
        return fileName;
    }
}

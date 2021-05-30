package bbangduck.bd.bbangduck.domain.review.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 생성, 수정 등의 서비스 로직 구현 시 리뷰 이미지에 대한 정보를 Dto 단위로 이동시키기 위해 구현한 Service Dto
 * Controller 단과의 의존관계를 최소화하기 위해 구현
 */
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

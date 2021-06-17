package bbangduck.bd.bbangduck.domain.member.dto.service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 이미지 생성에 사용될 Service Dto
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileImageDto {

    private Long fileStorageId;

    private String fileName;

    @Builder
    public MemberProfileImageDto(Long fileStorageId, String fileName) {
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

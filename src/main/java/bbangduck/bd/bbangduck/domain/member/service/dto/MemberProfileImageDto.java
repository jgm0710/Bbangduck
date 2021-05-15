package bbangduck.bd.bbangduck.domain.member.service.dto;

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

    private Long fileId;

    private String fileName;

    @Builder
    public MemberProfileImageDto(Long fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }
}

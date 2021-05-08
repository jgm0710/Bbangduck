package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileStorage extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(length = 1000)
    private String uploadPath;

    private String fileType;

    private long size;

    @Builder
    protected FileStorage(Long id, String fileName, String uploadPath, String fileType, long size) {
        this.id = id;
        this.fileName = fileName;
        this.uploadPath = uploadPath;
        this.fileType = fileType;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public String getFileType() {
        return fileType;
    }

    public long getSize() {
        return size;
    }
}

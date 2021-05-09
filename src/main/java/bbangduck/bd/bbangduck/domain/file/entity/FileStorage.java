package bbangduck.bd.bbangduck.domain.file.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.nio.file.Path;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 저장과 관련된 정보를 담을 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileStorage extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(length = 1000)
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

    public String getUploadPathString() {
        return uploadPath;
    }

    public Path getUploadPath() {
        return Path.of(this.uploadPath);
    }

    public Path getFileStoredPath() {
        Path uploadPath = this.getUploadPath();
        return uploadPath.resolve(fileName);
    }

    public Path getThumbnailStoredPath(String thumbnailPrefix) {
        Path uploadPath = this.getUploadPath();
        return uploadPath.resolve(getThumbnailImageFileName(thumbnailPrefix));
    }

    public String getThumbnailImageFileName(String thumbnailPrefix) {
        return thumbnailPrefix + fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "FileStorage{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", uploadPath='" + uploadPath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", size=" + size +
                '}';
    }
}

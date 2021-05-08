package bbangduck.bd.bbangduck.domain.file;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredFileDto {

    private String fileName;

    private Path uploadPath;

    @Builder
    protected StoredFileDto(String fileName, Path uploadPath) {
        this.fileName = fileName;
        this.uploadPath = uploadPath;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getUploadPath() {
        return uploadPath;
    }

    public String getUploadPathString() {
        return this.uploadPath.toAbsolutePath().toString();
    }
}

package bbangduck.bd.bbangduck.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("file.storage")
public class FileStorageProperties {

    private String uploadPath;

    private String thumbnailPrefix;

    private int originalImageWidth;

    private int originalImageHeight;

    private int thumbnailImageWidth;

    private int thumbnailImageHeight;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getThumbnailPrefix() {
        return thumbnailPrefix;
    }

    public void setThumbnailPrefix(String thumbnailPrefix) {
        this.thumbnailPrefix = thumbnailPrefix;
    }

    public int getOriginalImageWidth() {
        return originalImageWidth;
    }

    public void setOriginalImageWidth(int originalImageWidth) {
        this.originalImageWidth = originalImageWidth;
    }

    public int getOriginalImageHeight() {
        return originalImageHeight;
    }

    public void setOriginalImageHeight(int originalImageHeight) {
        this.originalImageHeight = originalImageHeight;
    }

    public int getThumbnailImageWidth() {
        return thumbnailImageWidth;
    }

    public void setThumbnailImageWidth(int thumbnailImageWidth) {
        this.thumbnailImageWidth = thumbnailImageWidth;
    }

    public int getThumbnailImageHeight() {
        return thumbnailImageHeight;
    }

    public void setThumbnailImageHeight(int thumbnailImageHeight) {
        this.thumbnailImageHeight = thumbnailImageHeight;
    }
}

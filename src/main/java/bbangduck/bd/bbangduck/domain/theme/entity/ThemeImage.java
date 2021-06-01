package bbangduck.bd.bbangduck.domain.theme.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_image_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    private Long fileStorageId;

    private String  fileName;

    @Builder
    public ThemeImage(Long id, Theme theme, Long fileStorageId, String fileName) {
        this.id = id;
        this.theme = theme;
        this.fileStorageId = fileStorageId;
        this.fileName = fileName;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Long getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    public Long getFileStorageId() {
        return fileStorageId;
    }

    public String getFileName() {
        return fileName;
    }
}

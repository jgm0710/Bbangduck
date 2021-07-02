package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 테마 분석 정보를 표현할 Entity
 *
 * @author jgm
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_analysis_id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    private Long evaluatedCount;

    @Builder
    public ThemeAnalysis(Long id, Theme theme, Genre genre, Long evaluatedCount) {
        Id = id;
        this.theme = theme;
        this.genre = genre;
        this.evaluatedCount = evaluatedCount;
    }

    public Long getId() {
        return Id;
    }

    public Theme getTheme() {
        return theme;
    }

    public Genre getGenre() {
        return genre;
    }

    public Long getEvaluatedCount() {
        return evaluatedCount;
    }
}

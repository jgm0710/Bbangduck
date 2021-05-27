package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_genre_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @CreationTimestamp
    private LocalDateTime registerTimes;

    @Builder
    public ThemeGenre(Long id, Theme theme, Genre genre) {
        this.id = id;
        this.theme = theme;
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public Theme getTheme() {
        return theme;
    }

    public Genre getGenre() {
        return genre;
    }

    public LocalDateTime getRegisterTimes() {
        return registerTimes;
    }
}

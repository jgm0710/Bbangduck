package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.theme.entity.enumerate.ThemeRatingType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Column(name = "theme_rating_type")
    @Enumerated(EnumType.STRING)
    private ThemeRatingType ratingType;

    @Column(name = "theme_rating")
    private float rating;

    @Column(name = "open_yn")
    private boolean openYN;

    @CreationTimestamp
    private LocalDateTime registerTimes;
}

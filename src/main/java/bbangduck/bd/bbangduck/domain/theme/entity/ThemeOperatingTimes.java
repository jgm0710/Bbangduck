package bbangduck.bd.bbangduck.domain.theme.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeOperatingTimes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_operating_times_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    Theme theme;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate runDate;

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}

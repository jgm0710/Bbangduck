package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 관계 매핑을 위해 임시 구현
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    public Long id;

    // TODO: 2021-05-24 샵 정보 추가

    @OneToOne(mappedBy = "theme", cascade = CascadeType.ALL)
    private ThemeImage themeImage;

    @Column(name = "theme_name")
    private String name;


    private NumberOfPeople numberOfPeople;

    private Difficulty difficulty;

    private Activity activity;

    private LocalTime playTime;

}

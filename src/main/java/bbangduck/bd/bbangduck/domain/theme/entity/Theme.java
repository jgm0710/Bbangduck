package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(mappedBy = "theme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ThemeImage themeImage;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<ThemeOperatingTimes> themeOperatingTimes = new ArrayList<>();

    @Column(name = "theme_name")
    private String name;

    @Column(length = 3000)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private NumberOfPeople numberOfPeople;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private Activity activity;

    private LocalTime playTime;

    @Column(name = "delete_yn")
    private boolean deleteYN;

}

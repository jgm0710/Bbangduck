package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ThemeDetail> themeDetails = new ArrayList<>();

    @Column(name = "theme_name")
    private String name;

    @Column(length = 3000)
    private String introduction;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<ThemeGenre> themeGenres = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private NumberOfPeople numberOfPeople;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private Activity activity;

    private LocalTime playTime;

    @Column(name = "delete_yn")
    private boolean deleteYN;

    @Builder
    public Theme(Long id, Shop shop, String name, String introduction, NumberOfPeople numberOfPeople, Difficulty difficulty, Activity activity, LocalTime playTime, boolean deleteYN) {
        this.id = id;
        this.shop = shop;
        this.name = name;
        this.introduction = introduction;
        this.numberOfPeople = numberOfPeople;
        this.difficulty = difficulty;
        this.activity = activity;
        this.playTime = playTime;
        this.deleteYN = deleteYN;
    }

    public void addThemeDetail(ThemeDetail themeDetail) {
        this.themeDetails.add(themeDetail);
        themeDetail.setTheme(this);
    }

    public void setThemeImage(ThemeImage themeImage) {
        this.themeImage = themeImage;
        themeImage.setTheme(this);
    }

    public void addGenre(Genre genre) {
        ThemeGenre themeGenre = ThemeGenre.builder()
                .theme(this)
                .genre(genre)
                .build();

        this.themeGenres.add(themeGenre);
    }

    public Long getId() {
        return id;
    }

    public Shop getShop() {
        return shop;
    }

    public ThemeImage getThemeImage() {
        return themeImage;
    }

    public List<ThemeDetail> getThemeDetails() {
        return themeDetails;
    }

    public String getName() {
        return name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public NumberOfPeople getNumberOfPeople() {
        return numberOfPeople;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Activity getActivity() {
        return activity;
    }

    public LocalTime getPlayTime() {
        return playTime;
    }

    public boolean isDeleteYN() {
        return deleteYN;
    }

    public List<Genre> getGenres() {
        return themeGenres.stream().map(ThemeGenre::getGenre).collect(Collectors.toList());
    }
}

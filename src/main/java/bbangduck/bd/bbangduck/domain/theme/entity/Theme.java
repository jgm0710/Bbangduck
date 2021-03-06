package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNull;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 관계 매핑을 위해 임시 구현
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(mappedBy = "theme", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ThemeImage themeImage;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<ThemeOperatingTimes> themeOperatingTimes = new ArrayList<>();

    @Column(name = "theme_name")
    private String name;

    @Column(length = 3000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private ThemeType type;

    @ElementCollection(targetClass = NumberOfPeople.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "theme_number_of_people", joinColumns = @JoinColumn(name = "theme_id"))
    @Enumerated(EnumType.STRING)
    private List<NumberOfPeople> numberOfPeoples;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private Activity activity;

    @Enumerated(EnumType.STRING)
    private HorrorGrade horrorGrade;

    private LocalTime playTime;

    private Long totalRating;

    private Long totalEvaluatedCount;

    @Column(name = "delete_yn")
    private boolean deleteYN;

    @Builder
    public Theme(Long id, Shop shop, String name, String description, Genre genre, ThemeType type, List<NumberOfPeople> numberOfPeoples, Difficulty difficulty, Activity activity, HorrorGrade horrorGrade, LocalTime playTime, Long totalRating, Long totalEvaluatedCount, boolean deleteYN) {
        this.id = id;
        this.shop = shop;
        this.name = name;
        this.description = description;
        this.genre = genre;
        this.type = type;
        this.numberOfPeoples = numberOfPeoples;
        this.difficulty = difficulty;
        this.activity = activity;
        this.horrorGrade = horrorGrade;
        this.playTime = playTime;
        this.totalRating = totalRating;
        this.totalEvaluatedCount = totalEvaluatedCount;
        this.deleteYN = deleteYN;
    }

    public void addThemeOperatingTime(ThemeOperatingTimes themeDetail) {
        this.themeOperatingTimes.add(themeDetail);
        themeDetail.setTheme(this);
    }

    public void setThemeImage(ThemeImage themeImage) {
        this.themeImage = themeImage;
        themeImage.setTheme(this);
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

    public List<ThemeOperatingTimes> getThemeOperatingTimes() {
        return themeOperatingTimes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<NumberOfPeople> getNumberOfPeoples() {
        return numberOfPeoples;
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

    public ThemeType getType() {
        return type;
    }

    public String getThemeImageFileName() {
        return themeImage == null ? null : themeImage.getFileName();
    }

    public Long getTotalRating() {
        return totalRating;
    }

    public Long getTotalEvaluatedCount() {
        return totalEvaluatedCount;
    }

    public HorrorGrade getHorrorGrade() {
        return horrorGrade;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
//                ", shop=" + shop +
//                ", themeImage=" + themeImage +
//                ", themeOperatingTimes=" + themeOperatingTimes +
                ", name='" + name + '\'' +
                ", introduction='" + description + '\'' +
//                ", themeGenres=" + themeGenres +
                ", type=" + type +
                ", numberOfPeoples=" + numberOfPeoples +
                ", difficulty=" + difficulty +
                ", activity=" + activity +
                ", horrorGrade=" + horrorGrade +
                ", playTime=" + playTime +
                ", totalRating=" + totalRating +
                ", totalEvaluatedCount=" + totalEvaluatedCount +
                ", deleteYN=" + deleteYN +
                '}';
    }

    public void increaseTotalRating(int rating) {
        this.totalRating += (long) rating;
    }

    public void increaseTotalEvaluatedCount() {
        this.totalEvaluatedCount++;
    }

    public void decreaseTotalRating(int rating) {
        this.totalRating -= rating;
    }

    public void decreaseTotalEvaluatedCount() {
        this.totalEvaluatedCount--;
    }

    public float getRating() {
        return isNull(this.totalRating) || isNull(this.totalEvaluatedCount) ? 0 : (float) this.totalRating / (float) this.totalEvaluatedCount;
    }

    public Genre getGenre() {
        return genre;
    }
}

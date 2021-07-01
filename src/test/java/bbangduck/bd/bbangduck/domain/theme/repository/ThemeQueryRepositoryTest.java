package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeGenre;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

class ThemeQueryRepositoryTest extends BaseTest {

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ThemeGenreRepository themeGenreRepository;

    @Autowired
    ThemeQueryRepository themeQueryRepository;

    /**
     * 쿼리 나가는 부분이랑, 결과 눈으로 확인함
     * 별도로 각 조회 테스트는 한가할 때 구현
     * todo: 한가할 때 테스트 마저 구현
     */
    @Test
    @DisplayName("테마 리스트 조회")
    public void findList() {
        //given
        List<Genre> genres = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Genre genre = Genre.builder()
                    .code("GenreCode" + i)
                    .name("GenreName" + i)
                    .build();
            Genre savedGenre = genreRepository.save(genre);
            genres.add(savedGenre);
        }

        for (int i = 0; i < 30; i++) {
            ThemeType randomThemeType = getRandomThemeType();
            Activity randomActivity = getRandomActivity();
            Difficulty randomDifficulty = getRandomDifficulty();
            HorrorGrade randomHorrorGrade = getRandomHorrorGrade();
            Set<NumberOfPeople> randomNumberOfPeoples = getRandomNumberOfPeoples();
            Genre randomGenre = getRandomGenre(genres);
            long randomTotalEvaluatedCount = new Random().nextInt(50) + 20;
            long randomTotalRating = randomTotalEvaluatedCount * new Random().nextInt(5);


            Theme theme = Theme.builder()
                    .totalEvaluatedCount(randomTotalEvaluatedCount)
                    .totalRating(randomTotalRating)
                    .difficulty(randomDifficulty)
                    .activity(randomActivity)
                    .horrorGrade(randomHorrorGrade)
                    .type(randomThemeType)
                    .numberOfPeoples(randomNumberOfPeoples)
                    .build();
            Theme savedTheme = themeRepository.save(theme);

            ThemeGenre themeGenre = ThemeGenre.builder()
                    .theme(savedTheme)
                    .genre(randomGenre)
                    .build();
            themeGenreRepository.save(themeGenre);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        ThemeGetListDto themeGetListDto = ThemeGetListDto.builder()
//                .genreCode(genres.get(1).getCode())
//                .themeType(ThemeType.HALF)
//                .rating(ThemeRatingFilteringType.TWO_OR_MORE)
//                .numberOfPeople(NumberOfPeople.TWO)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.VERY_ACTIVITY)
//                .horrorGrade(HorrorGrade.LITTLE_HORROR)
//                .sortCondition(ThemeSortCondition.LATEST)
                .build();

        //when
        System.out.println("====================================================================================================");
        QueryResults<Theme> queryResults = themeQueryRepository.findList(criteriaDto, themeGetListDto);

        //then
        List<Theme> themes = queryResults.getResults();
        System.out.println("====================================================================================================");
        themes.forEach(theme -> System.out.println("theme = " + theme));

    }

    private Genre getRandomGenre(List<Genre> genres) {
        return genres.get(new Random().nextInt(5));
    }

    private ThemeType getRandomThemeType() {
        switch (new Random().nextInt(3)) {
            case 1:
                return ThemeType.DEVICE;
            case 2:
                return ThemeType.HALF;
            default:
                return ThemeType.PROBLEM;
        }
    }

    private Set<NumberOfPeople> getRandomNumberOfPeoples() {
        switch (new Random().nextInt(7)) {
            case 1:
                return Set.of(NumberOfPeople.ONE);
            case 2:
                return Set.of(NumberOfPeople.TWO);
            case 3:
                return Set.of(NumberOfPeople.THREE);
            case 4:
                return Set.of(NumberOfPeople.FOUR);
            case 5:
                return Set.of(NumberOfPeople.FIVE);
            case 6:
                return Set.of(NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR);
            default:
                return Set.of(NumberOfPeople.ONE, NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR, NumberOfPeople.FIVE);
        }
    }

    private HorrorGrade getRandomHorrorGrade() {
        switch (new Random().nextInt(3)) {
            case 1:
                return HorrorGrade.LITTLE_HORROR;
            case 2:
                return HorrorGrade.NORMAL;
            default:
                return HorrorGrade.VERY_HORROR;
        }
    }

    private Difficulty getRandomDifficulty() {
        switch (new Random().nextInt(5)) {
            case 1:
                return Difficulty.VERY_EASY;
            case 2:
                return Difficulty.EASY;
            case 3:
                return Difficulty.NORMAL;
            case 4:
                return Difficulty.DIFFICULT;
            default:
                return Difficulty.VERY_DIFFICULT;
        }
    }

    private Activity getRandomActivity() {
        switch (new Random().nextInt(3)) {
            case 1:
                return Activity.LITTLE_ACTIVITY;
            case 2:
                return Activity.NORMAL;
            default:
                return Activity.VERY_ACTIVITY;
        }
    }

}
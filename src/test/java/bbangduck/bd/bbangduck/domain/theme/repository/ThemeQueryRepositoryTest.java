package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class ThemeQueryRepositoryTest extends BaseTest {

    @Autowired
    ThemeRepository themeRepository;

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
        for (int i = 0; i < 30; i++) {
            ThemeType randomThemeType = getRandomThemeType();
            Activity randomActivity = getRandomActivity();
            Difficulty randomDifficulty = getRandomDifficulty();
            HorrorGrade randomHorrorGrade = getRandomHorrorGrade();
            List<NumberOfPeople> randomNumberOfPeoples = getRandomNumberOfPeoples();
            Genre randomGenre = Arrays.stream(Genre.values()).findAny().orElseThrow();
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
                    .genre(randomGenre)
                    .build();
            themeRepository.save(theme);
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

    private List<NumberOfPeople> getRandomNumberOfPeoples() {
        switch (new Random().nextInt(7)) {
            case 1:
                return List.of(NumberOfPeople.ONE);
            case 2:
                return List.of(NumberOfPeople.TWO);
            case 3:
                return List.of(NumberOfPeople.THREE);
            case 4:
                return List.of(NumberOfPeople.FOUR);
            case 5:
                return List.of(NumberOfPeople.FIVE);
            case 6:
                return List.of(NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR);
            default:
                return List.of(NumberOfPeople.ONE, NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR, NumberOfPeople.FIVE);
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
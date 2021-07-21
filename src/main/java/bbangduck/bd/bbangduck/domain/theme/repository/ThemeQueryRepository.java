package bbangduck.bd.bbangduck.domain.theme.repository;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.theme.dto.service.ThemeGetListDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeRatingFilteringType;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeSortCondition;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static bbangduck.bd.bbangduck.domain.theme.entity.QTheme.theme;
import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

/**
 * Theme Entity 와 관련된 보다 복잡한 쿼리를 구현하기 위한 Repository
 *
 * @author jgm
 */
@Repository
@RequiredArgsConstructor
public class ThemeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public QueryResults<Theme> findList(CriteriaDto criteriaDto, ThemeGetListDto themeGetListDto) {
        return queryFactory
                .selectFrom(theme)
                .where(
                        genreEq(themeGetListDto.getGenre()),
                        typeEq(themeGetListDto.getThemeType()),
                        numberOfPeoplesContains(themeGetListDto.getNumberOfPeople()),
                        ratingEq(themeGetListDto.getRating()),
                        difficultyEq(themeGetListDto.getDifficulty()),
                        activityEq(themeGetListDto.getActivity()),
                        horrorGradeEq(themeGetListDto.getHorrorGrade()),
                        theme.deleteYN.eq(false)
                )
                .orderBy(
                        sortConditionEq(themeGetListDto.getSortCondition())
                )
                .offset(criteriaDto.getOffset())
                .limit(criteriaDto.getAmount())
                .fetchResults();
    }

    private OrderSpecifier<?>[] sortConditionEq(ThemeSortCondition sortCondition) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (isNotNull(sortCondition)) {
            switch (sortCondition) {
                case OLDEST:
                    orderSpecifiers.add(theme.registerTimes.asc());
                case LATEST:
                    orderSpecifiers.add(theme.registerTimes.desc());
                case RATING_ASC:
                    orderSpecifiers.add(theme.totalRating.divide(totalEvaluatedCount()).asc());
                    orderSpecifiers.add(theme.registerTimes.desc());
                default:
                    orderSpecifiers.add(theme.totalRating.divide(totalEvaluatedCount()).desc());
                    orderSpecifiers.add(theme.registerTimes.desc());
            }
        } else {
            orderSpecifiers.add(theme.totalRating.divide(totalEvaluatedCount()).desc());
            orderSpecifiers.add(theme.registerTimes.desc());
        }

        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
    }

    private BooleanExpression numberOfPeoplesContains(NumberOfPeople numberOfPeople) {
        return isNotNull(numberOfPeople) ? theme.numberOfPeoples.contains(numberOfPeople) : null;
    }

    private BooleanExpression horrorGradeEq(HorrorGrade horrorGrade) {
        if (isNotNull(horrorGrade)) {
            return theme.horrorGrade.eq(horrorGrade);
        }
        return null;
    }

    private BooleanExpression activityEq(Activity activity) {
        return isNotNull(activity) ? theme.activity.eq(activity) : null;
    }

    private BooleanExpression difficultyEq(Difficulty difficulty) {
        return isNotNull(difficulty) ? theme.difficulty.eq(difficulty) : null;
    }

    private BooleanExpression ratingEq(ThemeRatingFilteringType rating) {
        if (isNotNull(rating)) {

            switch (rating) {
                case TWO_OR_MORE:
                    return theme.totalRating.divide(totalEvaluatedCount()).gt(2);
                case THREE_OR_MORE:
                    return theme.totalRating.divide(totalEvaluatedCount()).gt(3);
                case FOUR_OR_MORE:
                    return theme.totalRating.divide(totalEvaluatedCount()).gt(4);
                default:
                    return null;
            }
        }
        return null;
    }

    private NumberExpression<Long> totalEvaluatedCount() {
        return theme.totalEvaluatedCount.when(0L).then(1L).otherwise(theme.totalEvaluatedCount);
    }

    private BooleanExpression typeEq(ThemeType themeType) {
        return isNotNull(themeType) ? theme.type.eq(themeType) : null;
    }

    private BooleanExpression genreEq(Genre genre) {
        return isNotNull(genre) ? theme.genre.eq(genre) : null;
    }
}

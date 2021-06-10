package bbangduck.bd.bbangduck.domain.search.repository;

import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.QMemberSearch;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:10:43
 */

@Repository
@RequiredArgsConstructor
public class MemberSearchRepositoryImpl implements MemberSearchRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    
    
    @Override
    public List<MemberSearchDto.MemberSearchTopMonthDto> searchTopMonthList() {
        // 유저 식별되는 키워드로 내림차순으로 한달간 많이 조회 데이터
        // select count(1) cnt , keyword, searchDate from memberSearch where member_id is not null group by keyword,
        // searchDate order by
        // cnt desc;

//        select
//        count(membersear0_.search_keyword) as col_0_0_,
//        membersear0_.search_keyword as col_1_0_,
//        membersear0_.search_type as col_2_0_,
//        membersear0_.search_date as col_3_0_
//                from
//        member_search membersear0_
//        where
//        membersear0_.search_date >?
//                and (
//                        membersear0_.member_id is not null
//        )
//        group by
//        membersear0_.search_keyword ,
//                membersear0_.search_date


        QMemberSearch qMemberSearch = QMemberSearch.memberSearch;

        return queryFactory.select(
                Projections.constructor(MemberSearchDto.MemberSearchTopMonthDto.class,
                        qMemberSearch.searchKeyword.count().as("count"),
                        qMemberSearch.searchKeyword,
                        qMemberSearch.searchType,
                        qMemberSearch.searchDate
                )
        ).from(qMemberSearch)
                .where(
                        qMemberSearch.searchDate.after(Expressions.dateTemplate(LocalDate.class, "{0}",
                                LocalDate.now().minusMonths(1))),
                        qMemberSearch.member.isNotNull()
                )
                .groupBy(qMemberSearch.searchKeyword, qMemberSearch.searchDate, qMemberSearch.searchType)
                .fetch();
    }


}

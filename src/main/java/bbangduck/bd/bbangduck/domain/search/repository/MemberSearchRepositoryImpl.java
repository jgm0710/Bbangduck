package bbangduck.bd.bbangduck.domain.search.repository;

import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.QMemberSearch;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
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
    public List<MemberSearch> searchTopMonthList() {
        QMemberSearch qMemberSearch = QMemberSearch.memberSearch;

        return null;
//        return queryFactory.select(qMemberSearch.searchKeyword, qMemberSearch.searchType).from(qMemberSearch)
//                .where(qMemberSearch.searchDate.after(Expressions.dateTemplate(LocalDate.class, "{0}",
//                        LocalDate.now().minusMonths(1))))
//                .fetch();
    }


}

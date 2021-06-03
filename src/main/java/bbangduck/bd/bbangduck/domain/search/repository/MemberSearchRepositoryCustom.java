package bbangduck.bd.bbangduck.domain.search.repository;

import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;

import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:08:01
 */
public interface MemberSearchRepositoryCustom {

    List<MemberSearch> searchTopMonthList();
}

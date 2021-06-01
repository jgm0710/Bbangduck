package bbangduck.bd.bbangduck.domain.search.repository;

import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:05:16
 */
public interface MemberSearchRepository extends JpaRepository<MemberSearch, Long>, MemberSearchRepositoryCustom {

}

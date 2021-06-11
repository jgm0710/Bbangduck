package bbangduck.bd.bbangduck.domain.shop.entity.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자 : 정구민 <br>
 * 작성 일자 : 2021-06-11 <br><br>
 *
 * Franchise Entity 의 기본적인 DB 조작을 위해 구현한 Repository
 */
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {
}

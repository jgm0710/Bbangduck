package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {
    Franchise findByName(String name);
}

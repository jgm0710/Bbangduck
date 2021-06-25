package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.QShop;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopQueryRepository {
  private final EntityManager em;
  private JPAQueryFactory jpaQueryFactory;

  @PostConstruct
  public void setUp() {
    jpaQueryFactory = new JPAQueryFactory(em);
  }

  public List<Shop> findByRangeLocation(double topLatitude, double bottomLatitude, double leftLongitude, double rightLongitude) {
    return jpaQueryFactory.selectFrom(QShop.shop)
        .where(QShop.shop.location.latitude.between(bottomLatitude, topLatitude).and(
            QShop.shop.location.longitude.between(leftLongitude, rightLongitude)
        )).fetch();
  }
}

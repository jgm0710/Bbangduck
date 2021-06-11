package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.entity.QAdminInfo;
import bbangduck.bd.bbangduck.domain.shop.entity.QShop;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Shop> search(Shop shop) {
        return queryFactory.selectFrom(QShop.shop)
                .where(

                ).fetch();
    }

    @Override
    public Page<Shop> searchPage(Shop shop, Pageable pageable) {
        QueryResults<Shop> shops = queryFactory.selectFrom(QShop.shop)
                .where(

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(shops.getResults(), pageable, shops.getTotal());
    }
}

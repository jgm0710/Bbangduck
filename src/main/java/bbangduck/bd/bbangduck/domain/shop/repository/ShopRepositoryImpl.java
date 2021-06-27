package bbangduck.bd.bbangduck.domain.shop.repository;

import bbangduck.bd.bbangduck.domain.shop.entity.QShop;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopRepositoryImpl implements ShopRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Shop> search(Shop shop) {
        return queryFactory.selectFrom(QShop.shop)
                .where(
                        eqId(shop.getId()),
                        eqFranchiseId(shop.getFranchise().getId()),
                        eqShopImageId(shop.getShopImage().getId()),
                        eqShopName(shop.getName()),
                        eqShopUrl(shop.getShopUrl()),
                        eqShopInfo(shop.getShopInfo()),
//                        eqShopPrices(shop.getShopPrices().get(??)),
                        eqLocation(shop.getLocation()), // 이렇게 하면 Embedded로 location 안에 있는 필드를 꺼내서 쿼리 작성하나?
                        eqAddress(shop.getAddress()),
                        eqArea(shop.getArea().getId()),
                        eqDeleteYN(false)
                ).fetch();
    }

    @Override
    public Shop deleteYN(Long shopId) {
        QShop qShop = QShop.shop;
        queryFactory.update(qShop).set(qShop.deleteYN, true).where(qShop.id.eq(shopId)).execute();
        return queryFactory.selectFrom(qShop).where(qShop.id.eq(shopId)).fetchFirst();
    }

    private Predicate eqDeleteYN(Boolean n) {
        if (ObjectUtils.isEmpty(n)) {
            return null;
        }
        return QShop.shop.deleteYN.eq(n);
    }

    private BooleanExpression eqArea(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShop.shop.area.id.eq(id);
    }

    private BooleanExpression eqAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        return QShop.shop.address.eq(address);
    }

    private BooleanExpression eqLocation(Location location) {
        if (ObjectUtils.isEmpty(location)) {
            return null;
        }
        return QShop.shop.location.eq(location);
    }

    private BooleanExpression eqShopInfo(String shopInfo) {
        if (StringUtils.isBlank(shopInfo)) {
            return null;
        }
        return QShop.shop.shopInfo.eq(shopInfo);
    }

    private BooleanExpression eqShopUrl(String shopUrl) {
        if (StringUtils.isBlank(shopUrl)) {
            return null;
        }
        return QShop.shop.shopUrl.eq(shopUrl);
    }

    private BooleanExpression eqShopName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return QShop.shop.name.eq(name);
    }

    private BooleanExpression eqShopImageId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShop.shop.shopImage.id.eq(id);
    }

    private BooleanExpression eqFranchiseId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShop.shop.franchise.id.eq(id);
    }

    private BooleanExpression eqId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShop.shop.id.eq(id);
    }

//    @Override
//    public Page<Shop> searchPage(Shop shop, Pageable pageable) {
//        QueryResults<Shop> shops = queryFactory.selectFrom(QShop.shop)
//                .where(
//
//                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//        return new PageImpl<>(shops.getResults(), pageable, shops.getTotal());
//    }
}

package bbangduck.bd.bbangduck.domain.shop.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopRepositoryImpl implements ShopRepositoryCustom{

    //
//    private final JPAQueryFactory queryFactory;

//    @Override
//    public List<Shop> search(Shop shop) {
//        return queryFactory.selectFrom(QShop.shop)
//                .where(
//
//                ).fetch();
//    }
//
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

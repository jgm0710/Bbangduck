package bbangduck.bd.bbangduck.domain.event.repository;

import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.event.entity.QShopEvent;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopEventRepositoryImpl implements ShopEventRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShopEvent> search(ShopEvent shopEvent) {
        return queryFactory.selectFrom(QShopEvent.shopEvent)
                .where(
                        eqId(shopEvent.getId()),
                        eqShopId(shopEvent.getShop().getId()),
                        eqBoardId(shopEvent.getBoard().getId()),
                        eqEndTimes(shopEvent.getEndTimes()),
                        eqStartTimes(shopEvent.getStartTimes())
                ).fetch();
    }

    private BooleanExpression eqStartTimes(LocalDateTime startTimes) {
        if (startTimes.toString().isEmpty()) {
            return null;
        }
        return QShopEvent.shopEvent.startTimes.after(startTimes);
    }

    private BooleanExpression eqEndTimes(LocalDateTime endTimes) {
        if (endTimes.toString().isEmpty()) {
            return null;
        }
        return QShopEvent.shopEvent.endTimes.before(endTimes);
    }

    private BooleanExpression eqBoardId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShopEvent.shopEvent.board.id.eq(id);
    }

    private BooleanExpression eqShopId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShopEvent.shopEvent.shop.id.eq(id);
    }

    private BooleanExpression eqId(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QShopEvent.shopEvent.id.eq(id);
    }


    @Override
    public Page<ShopEvent> searchPage(ShopEvent shopEvent, Pageable pageable) {
        QueryResults<ShopEvent> shopEvents = queryFactory.selectFrom(QShopEvent.shopEvent)
                .where(
                        eqId(shopEvent.getId()),
                        eqShopId(shopEvent.getShop().getId()),
                        eqBoardId(shopEvent.getBoard().getId()),
                        eqEndTimes(shopEvent.getEndTimes()),
                        eqStartTimes(shopEvent.getStartTimes())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(shopEvents.getResults(), pageable, shopEvents.getTotal());
    }




}

package bbangduck.bd.bbangduck.domain.admin.repository;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.entity.QAdminInfo;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/29/0029
 * Time: 오전 11:15:04
 */

//public class AdminInfoRepositoryImpl extends QuerydslRepositorySupport implements AdminInfoRepositoryCustom {

@Repository
@RequiredArgsConstructor
public class AdminInfoRepositoryImpl implements AdminInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<AdminInfo> search(AdminInfo adminInfo) {
        return queryFactory.selectFrom(QAdminInfo.adminInfo)
                .where(
                        eqId(adminInfo.getId()),
                        eqMemberEmail(adminInfo.getMember().getEmail()),
                        eqCompanyName(adminInfo.getCompanyName()),
                        eqOwner(adminInfo.getOwner()),
                        eqAddress(adminInfo.getAddress()),
                        eqCompanyNum(adminInfo.getCompanyNum()),
                        eqTelephone(adminInfo.getTelephone()),
                        eqDeleteYN(false)
                ).fetch();
    }

    @Override
    public Page<AdminInfo> searchPage(AdminInfo adminInfo, Pageable pageable) {
        //        QAdminInfo qAdminInfo = QAdminInfo.adminInfo;
//        QMember qMember = QMember.member;
//
//        JPQLQuery jpqlQuery = from(qAdminInfo);
//        if (!StringUtils.isEmpty(adminInfo.getAddress())) {
//            jpqlQuery.where(qAdminInfo.address.eq(adminInfo.getAddress()));
//        }
//
//        List<AdminInfo> adminInfos = getQuerydsl().applyPagination(pageable, jpqlQuery).fetchAll().fetch();
//        long totalCount = jpqlQuery.fetchCount();
//
//        return new PageImpl<>(adminInfos, pageable, totalCount);

        /// --------------------------

//        BooleanBuilder booleanBuilder = new BooleanBuilder();
//
//        if (StringUtils.isEmpty(adminInfo.getAddress())) {
//            booleanBuilder.and(QAdminInfo.adminInfo.address.eq(adminInfo.getAddress()));
//        }

        /// -----------------------------

        QueryResults<AdminInfo> adminInfos = queryFactory.selectFrom(QAdminInfo.adminInfo)
                .where(
                        eqId(adminInfo.getId()),
                        eqMemberEmail(adminInfo.getMember().getEmail()),
                        eqCompanyName(adminInfo.getCompanyName()),
                        eqOwner(adminInfo.getOwner()),
                        eqAddress(adminInfo.getAddress()),
                        eqCompanyNum(adminInfo.getCompanyNum()),
                        eqTelephone(adminInfo.getTelephone()),
                        eqDeleteYN(false)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(adminInfos.getResults(), pageable, adminInfos.getTotal());

    }

    private BooleanExpression eqDeleteYN(boolean deleteYN) {
        if (ObjectUtils.isEmpty(deleteYN)) {
            return null;
        }
        return QAdminInfo.adminInfo.deleteYN.eq(deleteYN);
    }

    private BooleanExpression eqTelephone(String telephone) {
        if (StringUtils.isBlank(telephone)) {
            return null;
        }
        return QAdminInfo.adminInfo.telephone.eq(telephone);
    }

    private BooleanExpression eqCompanyNum(String companyNum) {
        if (StringUtils.isBlank(companyNum)) {
            return null;
        }
        return QAdminInfo.adminInfo.companyName.eq(companyNum);
    }

    private BooleanExpression eqOwner(String owner) {
        if (StringUtils.isBlank(owner)) {
            return null;
        }
        return QAdminInfo.adminInfo.owner.eq(owner);
    }

    private BooleanExpression eqCompanyName(String companyName) {
        if (StringUtils.isBlank(companyName)) {
            return null;
        }
        return QAdminInfo.adminInfo.companyName.eq(companyName);
    }

    private BooleanExpression eqMemberEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        return QAdminInfo.adminInfo.member.email.eq(email);
    }

    private BooleanExpression eqId(Long id) {
//        if (Optional.of(id).isPresent()) {
//        if (null != id) {
        if (ObjectUtils.isEmpty(id)) {
            return null;
        }
        return QAdminInfo.adminInfo.id.eq(id);
    }

    private BooleanExpression eqAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        return QAdminInfo.adminInfo.address.eq(address);
    }


}

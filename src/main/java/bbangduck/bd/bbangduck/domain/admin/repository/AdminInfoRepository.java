package bbangduck.bd.bbangduck.domain.admin.repository;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/29/0029
 * Time: 오전 10:42:36
 */

public interface AdminInfoRepository extends JpaRepository<AdminInfo, Long>, AdminInfoRepositoryCustom {

}

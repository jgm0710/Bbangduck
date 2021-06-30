package bbangduck.bd.bbangduck.domain.admin.repository;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/29/0029
 * Time: 오전 11:12:58
 */
public interface AdminInfoRepositoryCustom {
    List<AdminInfo> search(AdminInfoDto.Search adminInfoDtoSearch);

    Page<AdminInfo> searchPage(Pageable pageable, AdminInfoDto.Search adminInfoSearchDto);
}

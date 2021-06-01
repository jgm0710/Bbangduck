package bbangduck.bd.bbangduck.domain.admin.service;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/30/0030
 * Time: 오후 3:49:56
 */

public interface AdminInfoService {
    List<AdminInfoDto> getAdminInfoList(AdminInfoDto adminInfo);
}

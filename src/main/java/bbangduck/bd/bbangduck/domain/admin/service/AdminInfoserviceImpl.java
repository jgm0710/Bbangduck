package bbangduck.bd.bbangduck.domain.admin.service;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/30/0030
 * Time: 오후 3:50:50
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class AdminInfoserviceImpl implements AdminInfoService{

    private final AdminInfoRepository adminInfoRepository;

    @Override
    public List<AdminInfo> getAdminInfoList(AdminInfo adminInfo) {
        System.out.println(adminInfo.toString());
        return adminInfoRepository.search(adminInfo);

    }
}

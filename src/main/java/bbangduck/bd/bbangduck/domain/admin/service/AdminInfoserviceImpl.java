package bbangduck.bd.bbangduck.domain.admin.service;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
public class AdminInfoserviceImpl implements AdminInfoService {

    private final AdminInfoRepository adminInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<AdminInfoDto> getAdminInfoList(AdminInfoDto.Search adminInfoDtoSearch) {

        List<AdminInfo> adminInfos = adminInfoRepository.search(adminInfoDtoSearch);

        return adminInfos.stream().map(AdminInfoDto::of).collect(Collectors.toList());

    }
}

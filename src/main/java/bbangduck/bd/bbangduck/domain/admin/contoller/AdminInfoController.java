package bbangduck.bd.bbangduck.domain.admin.contoller;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.service.AdminInfoService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/30/0030
 * Time: 오전 12:09:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/info")
@Slf4j
public class AdminInfoController {

    private final AdminInfoService adminInfoService;


    @GetMapping("/searcg")
    public ResponseEntity<List<AdminInfoDto>> adminInfoSearch(@RequestBody @Valid AdminInfoDto.Search adminInfoSearch) {


        List<AdminInfoDto> adminInfoDtos = adminInfoService.getAdminInfoList(adminInfoSearch);

        return ResponseEntity.ok().body(adminInfoDtos);
    }
}

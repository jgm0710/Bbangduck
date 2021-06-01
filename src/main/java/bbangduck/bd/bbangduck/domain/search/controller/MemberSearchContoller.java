package bbangduck.bd.bbangduck.domain.search.controller;

import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.service.MemberSearchService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:55:24
 */
@RequestMapping("/member/search")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberSearchContoller {

    private final MemberSearchService memberSearchService;

    @PostMapping(value = "/")
    public ResponseEntity<String> membersearchPost(@RequestBody @Valid MemberSearchDto memberSearchDto) {

        try {
            this.memberSearchService.save(memberSearchDto);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("키워드 저장 완료 : " + memberSearchDto.toString());
    }

}

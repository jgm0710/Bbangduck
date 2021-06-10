package bbangduck.bd.bbangduck.domain.search.controller;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import bbangduck.bd.bbangduck.domain.search.service.MemberSearchService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    private final MemberSearch memberSearch;

    @PostMapping(value = "/")
    public ResponseEntity<String> memberSearchPost(@RequestBody @Valid MemberSearchDto memberSearchDto) {

        try {
            this.memberSearchService.save(memberSearchDto);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("키워드 저장 완료 : " + memberSearchDto.toString());
    }

    @GetMapping("/{type}/{keyword}")
    public ResponseEntity<String> memberSearchIntegratedSearch(@PathVariable("type") @Valid String type,
                                                               @PathVariable("keyword") @Valid String keyword) {
        MemberSearchDto memberSearchDto = null;
        try {
            memberSearchDto = MemberSearchDto.builder().searchType(Optional.of(MemberSearchType.valueOf(type)).orElseThrow(Exception::new)).searchKeyword(keyword).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<MemberSearchDto> searchDtos = this.memberSearchService.findByMemberSearchTypeAndKeyword(memberSearchDto);
        return ResponseEntity.ok("통합 검색 완료 : " + searchDtos);
    }

    @GetMapping("/top/month")
    public ResponseEntity<String> memberSearchTopMonthList() {
        List<MemberSearchDto.MemberSearchTopMonthDto> memberSearchTopMonthDtos = this.memberSearchService.searchTopMonthList();
        return ResponseEntity.ok("빵덕 내 인기 검색어 : " + memberSearchTopMonthDtos);
    }

    @GetMapping("/top/{email}")
    public ResponseEntity<String> memberSearchTop10MySearch(@PathVariable("email") @Valid String email) {
        List<MemberSearchDto> list = this.memberSearchService.findTop10ByMemberIdAndSearchDateLessThan(email,
                LocalDate.now().minusMonths(1));

        return ResponseEntity.ok("내 검색 이력 조회 : " + list);

    }


}

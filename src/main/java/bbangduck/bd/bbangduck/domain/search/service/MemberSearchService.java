package bbangduck.bd.bbangduck.domain.search.service;

import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;

import java.util.List;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:03:29
 */
public interface MemberSearchService {

    void save(MemberSearchDto memberSearchDto);

    List<MemberSearchDto> findAll();

    List<MemberSearchDto> findByMemberSearchTypeAndKeyword(MemberSearchDto memberSearchDto);

    List<MemberSearchDto.MemberSearchTopMonthDto> searchTopMonthList();
}

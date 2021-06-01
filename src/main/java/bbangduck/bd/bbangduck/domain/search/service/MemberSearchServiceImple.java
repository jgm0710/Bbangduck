package bbangduck.bd.bbangduck.domain.search.service;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import bbangduck.bd.bbangduck.domain.search.repository.MemberSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:03:29
 */
@Service
@RequiredArgsConstructor
public class MemberSearchServiceImple implements MemberSearchService {

    private final MemberSearchRepository memberSearchRepository;

    private final MemberRepository memberRepository;

    @Override
    public void save(MemberSearchDto memberSearchDto) {
        Member member =
                this.memberRepository.findById(memberSearchDto.getMemberId()).orElseThrow(MemberNotFoundException::new);
        this.memberSearchRepository.save(MemberSearch.toEntity(memberSearchDto, member));
    }

    @Override
    public List<MemberSearchDto> findAll() {
        return this.memberSearchRepository.findAll().stream().map(MemberSearchDto::of).collect(Collectors.toList());
    }

    @Override
    public List<MemberSearchDto> findByMemberSearchTypeAndKeyword(MemberSearchDto memberSearchDto) {
        switch (memberSearchDto.getSearchType().name()){
//                  T01("테마 검색")
//                , S01("샵 검색")
//                , M01("회원 검색")
//                , F01("친구 검색")
//                , C01("커뮤니티 제목/내용")
//                , C02("커뮤니티 제목")
//                , C03("커뮤니티 내용")
//                , L01("지역 검색");
//            case "테마 검색" :
//            case "샵 검색" :
//            case "회원 검색" :
//            case "친구 검색" :
//            case "커뮤니티 제목/내용" :
//            case "커뮤니티 제목" :
//            case "커뮤니티 내용" :
//            case "지역 검색" :

            // TODO: 2021/6/1/0001 오후 7:55:03 otrodevym : 각 기능이 개발 되면 개발 해야 함
            case "T01" :
                return null;
            case "S01" :
                return null;
            case "M01" :
                return null;
            case "F01" :
                return null;
            case "C01" :
                return null;
            case "C02" :
                return null;
            case "C03" :
                return null;
            case "L01" :
            default:
                return null;


        }
    }

}

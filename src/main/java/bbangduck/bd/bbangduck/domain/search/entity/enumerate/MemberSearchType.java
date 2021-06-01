package bbangduck.bd.bbangduck.domain.search.entity.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberSearchType {
    T01("테마 검색")
    , S01("샵 검색")
    , M01("회원 검색")
    , F01("친구 검색")
    , C01("커뮤니티 제목/내용")
    , C02("커뮤니티 제목")
    , C03("커뮤니티 내용")
    , L01("지역 검색");

    private final String description;

    public String getDescription() {
        return this.description;
    }
}

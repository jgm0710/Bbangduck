package bbangduck.bd.bbangduck.domain.member.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 회원 검색 시 이메일을 통한 검색인지, 닉네임을 통한 검색인지를
 * 지정하기 위한 Enum
 *
 * @author jgm
 */
@RequiredArgsConstructor
@Getter
public enum MemberSearchKeywordType {
    EMAIL("이메일로 회원 검색"),
    NICKNAME("닉네임으로 회원 검색");

    private final String description;

    public static List<String> getNameList() {
        return Stream.of(MemberSearchKeywordType.values()).map(Enum::name).collect(Collectors.toList());
    }
}

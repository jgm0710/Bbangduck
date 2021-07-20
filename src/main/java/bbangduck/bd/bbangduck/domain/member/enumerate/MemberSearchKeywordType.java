package bbangduck.bd.bbangduck.domain.member.enumerate;

import bbangduck.bd.bbangduck.global.common.EnumType;
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
public enum MemberSearchKeywordType implements EnumType {
    EMAIL("이메일로 회원 검색"),
    NICKNAME("닉네임으로 회원 검색");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}

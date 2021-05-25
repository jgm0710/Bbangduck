package bbangduck.bd.bbangduck.domain.member.entity.enumerate;

import lombok.RequiredArgsConstructor;


/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 권한을 담는 Enum
 */
@RequiredArgsConstructor
public enum MemberRole {
    USER("ROLE_USER", "일반 회원"),
    ADMIN("ROLE_ADMIN", "관리자"),
    WITHDRAWAL("ROLE_WITHDRAWAL", "탈퇴"),
    BAN("ROLE_BAN", "활동 정지"),
    DEVELOP("ROLE_DEVELOP", "개발자 권한"),
    ;

    private final String roleName;
    private final String description;

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
}

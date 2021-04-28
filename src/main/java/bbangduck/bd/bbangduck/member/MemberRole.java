package bbangduck.bd.bbangduck.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    USER("ROLE_USER", "일반 회원"),
    ADMIN("ROLE_ADMIN", "관리자"),
    WITHDRAWAL("ROLE_WITHDRAWAL", "탈퇴"),
    BAN("ROLE_BAN", "활동 정지");

    private final String roleName;
    private final String description;
}

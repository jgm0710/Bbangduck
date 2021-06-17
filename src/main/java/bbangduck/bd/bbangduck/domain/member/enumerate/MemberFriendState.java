package bbangduck.bd.bbangduck.domain.member.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberFriendState {
    REQUEST("A -> B 친구 요청"),
    STAY("A -> B 수락 대기"),
    ALLOW("친구 수락"),
    REFUSE("친구 거절"),
    CUT("차단");

    private final String description;

}
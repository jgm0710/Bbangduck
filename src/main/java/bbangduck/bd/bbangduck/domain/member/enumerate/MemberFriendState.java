package bbangduck.bd.bbangduck.domain.member.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberFriendState {
    REQUEST("A -> B 친구 요청"),
    STAY("A -> B 수락 대기"),
    ACCEPT("친구 수락"),
    REJECT("친구 거절"),
    CUT("차단"),
    DELETE("친구 삭제");

    private final String description;

}

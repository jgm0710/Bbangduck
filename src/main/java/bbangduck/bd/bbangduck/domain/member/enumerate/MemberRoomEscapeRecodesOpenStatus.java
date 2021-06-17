package bbangduck.bd.bbangduck.domain.member.enumerate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 작성자 : Gumin Jeong
 * <p>
 * 작성 일자 : 2021-06-16
 * <p>
 *
 * 회원의 방탈출 기록 공개 상태를 표현할 Enum
 */
@RequiredArgsConstructor
public enum MemberRoomEscapeRecodesOpenStatus {
    OPEN("방탈출 기록 공개"),
    ONLY_FRIENDS_OPEN("방탈출 기록 친구에게만 공개"),
    CLOSE("방탈출 기록 비공개 -> 본인만 확인 가능");

    public final String description;

    public static List<String> getNameList() {
        return Stream.of(MemberRoomEscapeRecodesOpenStatus.values()).map(Enum::name).collect(Collectors.toList());
    }

}

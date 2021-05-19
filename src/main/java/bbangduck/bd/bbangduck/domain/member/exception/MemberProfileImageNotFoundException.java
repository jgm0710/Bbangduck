package bbangduck.bd.bbangduck.domain.member.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 프로필 이미지 삭제 등에서 회원의 프로필 이미지를 조회할 경우
 * 회원 프로필 이미지를 찾을 수 없을 때 발생할 예외
 */
public class MemberProfileImageNotFoundException extends NotFoundException {
    public MemberProfileImageNotFoundException() {
        super(ResponseStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND);
    }
}

package bbangduck.bd.bbangduck.security.exception;


import bbangduck.bd.bbangduck.common.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionStatus implements EnumType {
    KAKAO_USER_NOT_FOUND(4101, "해당 카카오 계정으로 가입된 회원이 존재하지 않습니다.", "카카오 로그인 시점에 해당 카카오 계정으로 회원가입을 진행하지 않은 회원인 경우");

    private final int status;
    private final String message;
    private final String description;

}

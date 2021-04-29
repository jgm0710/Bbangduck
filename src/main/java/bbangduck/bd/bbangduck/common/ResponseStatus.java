package bbangduck.bd.bbangduck.common;


import bbangduck.bd.bbangduck.common.EnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus implements EnumType {

    KAKAO_SIGN_IN_SUCCESS(2101, "카카오 로그인에 성공했습니다.", "카카오 API 를 통한 로그인이 성공했을 경우"),

    MEMBER_NOT_FOUND(4001, "해당 회원이 존재하지 않습니다.", "해당 조건으로 조회된 회원이 존재하지 않는 경우"),

    KAKAO_USER_NOT_FOUND(4101, "해당 카카오 계정으로 가입된 회원이 존재하지 않습니다.", "카카오 로그인 시점에 해당 카카오 계정으로 회원가입을 진행하지 않은 회원인 경우"),

    SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR(4120, "소셜에서 받아온 인가 토큰을 통한 인증 토큰 발급에 실패하였습니다.", "소셜 API 를 통해 받아온 인가 토큰을 통한 인증 토큰 발급이 실패한 경우"),
    SOCIAL_USER_INFO_RETRIEVAL_ERROR(4121, "소셜에서 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패하였습니다.", "소셜  API 를 통해 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패한 경우")
    ;

    private final int status;
    private final String message;
    private final String description;

}

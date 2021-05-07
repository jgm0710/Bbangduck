package bbangduck.bd.bbangduck.global.common;


import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 작성자 : 정구민 <br><br>
 *
 * ResponseDto 를 통해 응답되는 응답에 대해서 HttpStatus 와 별개로 응답을 구분하기 위한 Status 값이 있으면 좋겠다 판단
 * ResponseStatus 여러 응답 상태 값들을 기입하고 ResponseDto 를 보다 편하게 사용하기 위한 상태값
 *
 * 뒤의 3자리를 제외한 앞자리는 어떤 도메인과 관련이 있는지를 명시
 * 뒤에서 3번째 자리의 숫자를 통해 성공적인 응답인지, 실패한 응답인지 명시 (2, 4)
 * 이후 순서 등에 따라 자유롭게 명시
 */
@RequiredArgsConstructor
public enum ResponseStatus {

    /**
     * 인증 관련 응답 코드
     * 시작 코드 1
     */
    KAKAO_SIGN_IN_SUCCESS(1221, "카카오 로그인에 성공했습니다.", "카카오 API 를 통한 로그인이 성공했을 경우"),

    UNAUTHORIZED(1401, "인증되지 않은 사용자가 리소스에 접근하였습니다. 인증 토큰을 다시 확인해 주세요.", "인증이 필요한 리소스 요청 시 헤더에 기입된 인증 토큰이 유효하지 않은 경우"),
    FORBIDDEN(1403, "해당 리소스에 접근할 수 없는 회원입니다.", "인증은 되었으나 해당 리소스에 대한 접근 권한을 인가받지 못한 사용자일 경우"),

    SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR(1411, "소셜에서 받아온 인가 토큰을 통한 인증 토큰 발급에 실패하였습니다.", "소셜 API 를 통해 받아온 인가 토큰을 통한 인증 토큰 발급이 실패한 경우"),
    SOCIAL_USER_INFO_RETRIEVAL_ERROR(1412, "소셜에서 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패하였습니다.", "소셜  API 를 통해 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패한 경우"),
    SOCIAL_SIGN_IN_STATE_MISMATCH(1413, "소셜 인가 토큰 요청 시 기입한 state 값이 변형되었습니다.", "소셜 API 를 통한 인가 토큰 요청 시 CSRF 방지를 위해 기입한 state 값이 변형된 경우"),

    KAKAO_USER_NOT_FOUND(1421, "해당 카카오 계정으로 가입된 회원이 존재하지 않습니다.", "카카오 로그인 시점에 해당 카카오 계정으로 회원가입을 진행하지 않은 회원인 경우"),

    /**
     * 회원 관련 응답 코드
     * 시작 코드 2
     */
    MEMBER_SIGN_UP_SUCCESS(2201, " 회원가입에 성공했습니다.", "회원가입에 성공했을 경우"),
    GET_MEMBER_PROFILE_SUCCESS(2202, "회원 조회에 성공했습니다.", "회원 프로필 조회에 성공했을 경우"),

    MEMBER_NOT_FOUND(2401, "해당 회원이 존재하지 않습니다.", "해당 조건으로 조회된 회원이 존재하지 않는 경우"),
    MEMBER_SIGN_UP_NOT_VALID(2402, "회원가입 시 기입사항을 올바르게 기입하지 않았습니다.", "회원가입 시 기입사항을 규칙에 맞게 기입하지 않은 경우"),
    MEMBER_EMAIL_DUPLICATE(2403,"해당 Email 을 사용하는 회원이 이미 존재합니다.", "회원가입, 회원 정보 수정 시 기존 사용자와 중복되는 Email 저장을 시도하는 경우"),
    MEMBER_NICKNAME_DUPLICATE(2404, "해당 Nickname 을 사용하는 회원이 이미 존재합니다.", "회원가입, 회원 정보 수정 시 기존 사용자와 중복되는 Nickname 저장을 시도하는 경우"),
    MEMBER_SOCIAL_INFO_DUPLICATE(2405, "해당 소셜 회원은 이미 가입된 회원입니다.", "소셜 회원가입 시 기입한 소셜 정보를 통해 가입한 회원이 이미 존재하는 경우"),


    /**
     * 공통 응답 코드
     * 시작 코드 99
     */
    VALIDATION_ERROR(99411, "요청 시 기입 사항이 해당 요청의 규칙에 맞게 기입되지 않았습니다.", "API 요청 시 요청 Body 에 기입해야 할 부분이 해당 요청의 Validation 규칙에 맞지 않는 경우"),

    ;

    private final int status;
    private final String message;
    private final String description;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}

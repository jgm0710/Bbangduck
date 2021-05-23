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
    REFRESH_SIGN_IN_SUCCESS(1222, "Refresh Token 을 통한 Access Token 재발급에 성공했습니다.", "Refresh Token 을 통한 Access Token 재발급에 성공했을 경우"),
    WITHDRAWAL_SUCCESS(1223, "회원 탈퇴에 성공했습니다.", "회원 탈퇴에 성공한 경우"),
    SIGN_OUT_SUCCESS(1224, "로그아웃에 성공했습니다.", "로그아웃에 성공한 경우"),

    UNAUTHORIZED(1401, "인증되지 않은 사용자가 리소스에 접근했습니다. 인증 토큰을 다시 확인해 주세요.", "인증이 필요한 리소스 요청 시 헤더에 기입된 인증 토큰이 유효하지 않은 경우"),
    FORBIDDEN(1403, "해당 리소스에 접근할 수 없는 회원입니다.", "인증은 되었으나 해당 리소스에 대한 접근 권한을 인가받지 못한 사용자일 경우"),

    SOCIAL_ACCESS_TOKEN_RETRIEVAL_ERROR(1411, "소셜에서 받아온 인가 토큰을 통한 인증 토큰 발급에 실패했습니다.", "소셜 API 를 통해 받아온 인가 토큰을 통한 인증 토큰 발급이 실패한 경우"),
    SOCIAL_USER_INFO_RETRIEVAL_ERROR(1412, "소셜에서 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패했습니다.", "소셜  API 를 통해 받아온 인증 토큰을 통한 소셜 회원 정보 조회에 실패한 경우"),
    SOCIAL_SIGN_IN_STATE_MISMATCH(1413, "소셜 인가 토큰 요청 시 기입한 state 값이 변형되었습니다.", "소셜 API 를 통한 인가 토큰 요청 시 CSRF 방지를 위해 기입한 state 값이 변형된 경우"),

    KAKAO_USER_NOT_FOUND(1421, "해당 카카오 계정으로 가입된 회원이 존재하지 않습니다.", "카카오 로그인 시점에 해당 카카오 계정으로 회원가입을 진행하지 않은 회원인 경우"),

    REFRESH_NOT_VALID(1431, "Access Token 재발급에 필요한 Refresh Token 을 기입해 주세요.", "Access Token 재발급 요청 시 필요한 Refresh Token 을 기입하지 않은 경우"),
    REFRESH_TOKEN_NOT_FOUND(1432, "해당 Refresh Token 을 통한 Access Token 재발급이 불가능합니다.", "요청을 통해 들어온 Refresh Token 을 통한 회원 조회에 실패했을 경우"),
    REFRESH_TOKEN_EXPIRED(1433, "Refresh Token 의 유효기간이 만료되었습니다.", "Access Token 재발급 요청 시 Refresh Token 의 유효기간이 만료된 경우"),

    WITHDRAWAL_DIFFERENT_MEMBER(1441, "자신의 계정만 탈퇴가 가능합니다.", "회원 탈퇴 요청 시 자신이 아닌 다른 회원의 계정을 회원 탈퇴하는 경우"),
    SIGN_OUT_DIFFERENT_MEMBER(1442, "자신의 계정만 로그아웃 할 수 있습니다.", "로그아웃 요청 시 다른 회원의 계정을 로그아웃하는 경우"),


    /**
     * 회원 관련 응답 코드
     * 시작 코드 2
     */
    MEMBER_SIGN_UP_SUCCESS(2201, " 회원가입에 성공했습니다.", "회원가입에 성공했을 경우"),
    MEMBER_GET_PROFILE_SUCCESS(2202, "회원 조회에 성공했습니다.", "회원 프로필 조회에 성공했을 경우"),
    MEMBER_UPDATE_PROFILE_IMAGE_SUCCESS(2203, "회원 프로필 이미지 변경에 성공했습니다.", "회원 프로필 이미지 변경 요청이 성공했을 경우"),
    MEMBER_DELETE_PROFILE_IMAGE_SUCCESS(2204, "회원 프로필 이미지 삭제에 성공했습니다.", "회원 프로필 이미지 삭제 요청에 성공했을 경우"),
    MEMBER_UPDATE_NICKNAME_SUCCESS(2205, "회원 Nickname 변경에 성공했습니다.", "회원 Nickname 변경에 성공한 경우"),
    MEMBER_UPDATE_DESCRIPTION_SUCCESS(2206, "회원 자기소개 변경에 성공했습니다.", "회원 자기소개 변경에 성공한 경우"),
    MEMBER_TOGGLE_ROOM_ESCAPE_RECODES_OPEN_SUCCESS(2207, "회원 방탈출 기록 공개 여부 변경에 성공했습니다.", "회원 방탈출 기록 공개 여부 변경에 성공한 경우"),


    MEMBER_NOT_FOUND(2401, "해당 회원이 존재하지 않습니다.", "해당 조건으로 조회된 회원이 존재하지 않는 경우"),
    MEMBER_SIGN_UP_NOT_VALID(2402, "회원가입 시 기입 사항을 올바르게 기입하지 않았습니다.", "회원가입 시 기입 사항을 규칙에 맞게 기입하지 않은 경우"),
    MEMBER_EMAIL_DUPLICATE(2403,"해당 Email 을 사용하는 회원이 이미 존재합니다.", "회원가입, 회원 정보 수정 시 기존 사용자와 중복되는 Email 저장을 시도하는 경우"),
    MEMBER_NICKNAME_DUPLICATE(2404, "해당 Nickname 을 사용하는 회원이 이미 존재합니다.", "회원가입, 회원 정보 수정 시 기존 사용자와 중복되는 Nickname 저장을 시도하는 경우"),
    MEMBER_SOCIAL_INFO_DUPLICATE(2405, "해당 소셜 회원은 이미 가입된 회원입니다.", "소셜 회원가입 시 기입한 소셜 정보를 통해 가입한 회원이 이미 존재하는 경우"),
    UPDATE_DIFFERENT_MEMBER_PROFILE(2406, "다른 회원의 프로필은 수정할 수 없습니다.", "프로필 수정 요청 시 다른 회원의 프로필 수정을 요청하는 경우"),
    MEMBER_UPDATE_PROFILE_IMAGE_NOT_VALID(2408, "회원 프로필 이미지 수정 시 기입 사항을 올바르게 기입하지 않았습니다.", "회원 프로필 이미지 수정 시 필요한 기입 사항을 올바르게 기입하지 않은 경우"),
    MEMBER_UPDATE_NICKNAME_NOT_VALID(2409, "회원 Nickname 변경 시 기입 사항을 올바르게 기입하지 않았습니다.", "회원 Nickname 변경 시 기입 사항을 올바르게 기입하지 않은 경우"),
    MEMBER_UPDATE_DESCRIPTION_NOT_VALID(2410, "회원 자기소개 변경 시 기입 사항을 올바르게 기입하지 않았습니다.", "회원의 자기소개 내용 변경 시 기입 사항을 올바르게 기입하지 않은 경우"),
    MEMBER_PROFILE_IMAGE_NOT_FOUND(2411, "회원의 프로필 이미지가 존재하지 않습니다.", "회원 프로필 이미지 삭제 등의 요청에서 회원 프로필 이미지 요청에 실패한 경우"),


    /**
     * 파일 관련 응답 코드
     * 시작 코드 3
     */
    UPLOAD_IMAGE_FILE_SUCCESS(3201, "이미지 파일 업로드에 성공했습니다.", "이미지 파일 업로드에 성공한 경우"),
    // FIXME: 2021-05-12 파일 삭제 기능 부분 논의 완료되면 해당 주석 삭제
//    DELETE_FILE_SUCCESS(3202, "파일 삭제에 성공했습니다.", "파일 삭제에 성공한 경우"),

    DENIED_FILE_EXTENSION(3401, "업로드를 요청한 파일의 파일 확장자는 업로드 거부 대상입니다.", "파일 업로드 시 실행 파일 등을 업로드하는 경우"),
    ORIGINAL_FILE_NAME_IS_BLANK(3402, "업로드를 요청한 파일의 파일명을 알 수 없습니다. 다시 확인해 주세요.", "업로드를 시도한 파일의 원본 파일명이 NULL 인 경우"),
    FILE_NAME_CONTAINS_WRONG_PATH(3403, "업로드를 요청한 파일의 이름에 잘 못된 경로가 포함되어 있어 업로드에 실패했습니다.", "업로드를 시도한 파일의 파일명이 .. 등을 포함한 경우"),
    COULD_NOT_STORE_FILE(3404, "업로드를 요청한 파일을 저장하는데 실패했습니다. 다시 시도해 주세요.", "알 수 없는 이유로 업로드 요청된 파일 저장에 실패한 경우"),
    COULD_NOT_CREATE_DIRECTORY(3405, "업로드 된 파일이 저장 될 디렉토리를 만들 수 없습니다.", "알 수 없는 이유로 설정된 파일 경로에 디렉토리를 생성할 수 없을 경우"),
    UPLOAD_NOT_IMAGE_FILE(3406, "이미지 파일만 업로드가 가능합니다. 업로드 파일을 다시 확인해 주시요.", "이미지 파일이 아닌 다른 종류의 파일을 업로드 할 경우"),
    STORED_FILE_NOT_FOUND_IN_DATABASE(3407, "요청한 파일은 저장되지 않은 파일입니다.", "파일을 조회할 때, 데이터베이스에 해당 파일에 대한 정보가 존재하지 않을 경우"),
    STORED_FILE_NOT_EXIST(3408, "요청한 파일이 알 수 없는 이유로 실제 존재하지 않습니다.", "요청된 파일에 대한 정보가 데이터베이스에 저장되어 있으나, 알 수 없는 이유로 실제 파일이 존재하지 않는 경우"),
    FILE_DOWNLOAD_FAIL_FOR_UNKNOWN_REASON(3409, "요청한 파일이 알 수 없는 이유로 다운로드 할 수 없습니다.", "알 수 없는 이유로 파일 다운로드를 실패한 경우"),
    DOWNLOAD_THUMBNAIL_OF_NON_IMAGE_FILE(3410, "해당 파일은 이미지 파일이 아니므로, 썸네일 이미지 다운로드 요청이 불가능합니다.", "썸네일 이미지 요청 시 해당 파일이 이미지 파일이 아닌 경우"),
    FILE_DELETE_FAIL_FOR_UNKNOWN_REASON(3411, "삭제 요청한 파일이 알 수 없는 이유로 삭제되지 않았습니다. 다시 시도해 주세요.", "알 수 없는 이유로 삭제 요청한 파일이 삭제되지 않았을 경우"),

    /**
     * 리뷰 관련 응답 코드
     * 시작 코드 4
     */

    CREATE_REVIEW_NOT_VALID(4401, "리뷰 생성 요청 시 기입 사항을 올바르게 기입하지 않았습니다.", "리뷰 생성 요청 시 기입 사항을 올바르게 기입하지 않은 경우"),

    /**
     * 테마 관련 응답 코드
     * 시작 코드 5
     */
    THEME_NOT_FOUND(5401, "해당 테마가 존재하지 않습니다.", "요청된 테마를 조회했을 때 DB에 해당 테마가 존재하지 않는 경우"),


    /**
     * 공통 응답 코드
     * 시작 코드 99
     */
    VALIDATION_ERROR(99411, "요청 시 기입 사항이 해당 요청의 규칙에 맞게 기입되지 않았습니다.", "API 요청 시 요청 Body 에 기입해야 할 부분이 해당 요청의 Validation 규칙에 맞지 않는 경우"),
    MD5_ENCODE_ERROR(99412, "알 수 없는 이유로 MD5 형식의 인코딩에 실패하였습니다.", "Etag 에 사용되는 MD5 인코더를 통한 문자열 인코딩 시 알 수 없는 이유로 예외가 발생한 경우"),
    URL_ENCODE_ERROR(99413, "알 수 없는 이유로 URL 인코딩에 실패하였습니다.", "URLEncoder 를 통한 인코딩 시 알 수 없는 이유로 예외가 발생한 경우"),

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

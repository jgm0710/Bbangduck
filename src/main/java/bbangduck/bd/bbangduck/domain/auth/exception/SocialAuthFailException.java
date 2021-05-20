package bbangduck.bd.bbangduck.domain.auth.exception;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 소셜 로그인 요청 시 로그인이 실패할 경우에 대한 최상위 Exception <br/>
 * ExceptionHandlerController 의 socialAuthFailExceptionHandling() 을 통해 예외 처리 <br/>
 * --> ModelAndView 를 통해 로그인 실패 정보를 응답.
 */
public class SocialAuthFailException extends RuntimeException {

    private int status;

    private Object body;

    private String viewName;

    public SocialAuthFailException(int status, Object body, String message) {
        super(message);
        this.status = status;
        this.body = body;
        this.viewName = "social-sign-in-result";
    }

    public SocialAuthFailException(ResponseStatus responseStatus, Object body) {
        super(responseStatus.getMessage());
        this.status = responseStatus.getStatus();
        this.body = body;
        this.viewName = "social-sign-in-result";
    }

    public int getStatus() {
        return status;
    }

    public Object getBody() {
        return body;
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public String toString() {
        return "SocialAuthFailException{" +
                "status=" + status +
                ", body=" + body +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}

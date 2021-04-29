package bbangduck.bd.bbangduck.security.exception;

import bbangduck.bd.bbangduck.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import lombok.Getter;

@Getter
public class SocialAuthFailException extends RuntimeException {

    private int status;

    private SocialUserInfoDto socialUserInfoDto;

    private String viewName;

    public SocialAuthFailException(ResponseStatus responseStatus, SocialUserInfoDto socialUserInfoDto) {
        super(responseStatus.getMessage());
        this.status = responseStatus.getStatus();
        this.socialUserInfoDto = socialUserInfoDto;
        this.viewName = "social-sign-in-result";
    }

    @Override
    public String toString() {
        return "SocialAuthFailException{" +
                "status=" + status +
                ", socialUserInfoDto=" + socialUserInfoDto +
                ", viewName='" + viewName + '\'' +
                '}';
    }
}

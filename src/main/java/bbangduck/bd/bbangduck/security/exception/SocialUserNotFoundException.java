package bbangduck.bd.bbangduck.security.exception;

import bbangduck.bd.bbangduck.member.social.SocialUserInfoDto;
import bbangduck.bd.bbangduck.member.social.SocialType;
import lombok.Getter;

@Getter
public class SocialUserNotFoundException extends RuntimeException {

    private int status;

    private SocialUserInfoDto socialUserInfoDto;

    private String viewName;

    public SocialUserNotFoundException(ExceptionStatus exceptionStatus, SocialUserInfoDto socialUserInfoDto, String viewName) {
        super(exceptionStatus.getMessage());
        this.status = exceptionStatus.getStatus();
        this.socialUserInfoDto = socialUserInfoDto;
        this.viewName = viewName;
    }
}

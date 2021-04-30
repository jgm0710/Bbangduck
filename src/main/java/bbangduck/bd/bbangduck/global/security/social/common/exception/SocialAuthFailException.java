package bbangduck.bd.bbangduck.global.security.social.common.exception;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.global.security.social.common.dto.SocialUserInfoDto;
import lombok.Getter;

@Getter
public class SocialAuthFailException extends RuntimeException {

    private int status;

    private SocialUserInfoDto socialUserInfoDto;

    private String viewName;

    public SocialAuthFailException(MemberResponseStatus memberResponseStatus, SocialUserInfoDto socialUserInfoDto) {
        super(memberResponseStatus.getMessage());
        this.status = memberResponseStatus.getStatus();
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

package bbangduck.bd.bbangduck.global.security.social.common;

import bbangduck.bd.bbangduck.domain.member.model.SocialType;

public interface SocialUserInfoInterface {
    String getId();

    SocialType getSocialType();

    String getEmail();

    String getNickname();
}

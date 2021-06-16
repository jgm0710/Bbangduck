package bbangduck.bd.bbangduck.domain.auth.dto.service;

import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;

public interface SocialUserInfoInterface {
    String getSocialId();

    SocialType getSocialType();

    String getEmail();

    String getNickname();
}

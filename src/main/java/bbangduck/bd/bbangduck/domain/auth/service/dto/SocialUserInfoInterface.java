package bbangduck.bd.bbangduck.domain.auth.service.dto;

import bbangduck.bd.bbangduck.domain.member.entity.SocialType;

public interface SocialUserInfoInterface {
    String getSocialId();

    SocialType getSocialType();

    String getEmail();

    String getNickname();
}
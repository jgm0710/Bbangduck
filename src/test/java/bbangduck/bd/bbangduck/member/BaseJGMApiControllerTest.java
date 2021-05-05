package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class BaseJGMApiControllerTest extends BaseControllerTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected SecurityJwtProperties securityJwtProperties;

    protected static int REFRESH_TOKEN_EXPIRED_DATE;

    @BeforeEach
    public void setUp() {
        REFRESH_TOKEN_EXPIRED_DATE = securityJwtProperties.getRefreshTokenExpiredDate();
        memberRepository.deleteAll();
    }

}

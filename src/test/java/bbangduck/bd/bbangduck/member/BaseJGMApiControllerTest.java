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

    protected static String STATUS_DESCRIPTION = "요청에 대해 HttpStatus 외에 별도로 응답 상태를 구분하기 위한 상태 값";

    protected static String MESSAGE_DESCRIPTION = "요청에 따른 응답에 대한 간단한 Message";

    @BeforeEach
    public void setUp() {
        REFRESH_TOKEN_EXPIRED_DATE = securityJwtProperties.getRefreshTokenExpiredDate();
        memberRepository.deleteAll();
    }

}

package bbangduck.bd.bbangduck.domain.auth.service;

import bbangduck.bd.bbangduck.common.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NaverSignServiceTest extends BaseTest {

    @Autowired
    NaverSignService naverSignService;

    @Test
    @DisplayName("네이버 아이디로 로그인 인증 요청 URL Test")
    public void getNaverAuthorizationUrl() {
        //given

        //when
        String naverAuthorizationUrl = naverSignService.getNaverAuthorizationUrl();
        //then
        System.out.println("naverAuthorizationUrl = " + naverAuthorizationUrl);

    }
}
package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.service.SocialSignInService;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialAuthApiControllerTest extends BaseJGMApiControllerTest {

    @Mock
    SocialSignInService socialSignInService;

    @Test
    @DisplayName("Kakao 콜백 테스트")
    public void Kakao_Callback_Test() throws Exception {
        //given

        //when

        //then

    }
}
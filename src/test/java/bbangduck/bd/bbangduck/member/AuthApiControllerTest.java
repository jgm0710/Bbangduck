package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiControllerTest extends BaseMemberApiControllerTest {

    @Test
    @DisplayName("회원가입 테스트")
    public void signUpTest() throws Exception{
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .password("")
                .socialType(null)
                .socialId("")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 기입 오류 테스트")
    public void signUpTest_Empty() throws Exception{
        //given
        MemberSignUpDto memberSignUpDto = MemberSignUpDto.builder()
                .email("")
                .nickname("")
                .password("")
                .socialType(null)
                .socialId("")
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest());
    }
}
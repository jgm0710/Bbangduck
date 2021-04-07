package bbangduck.bd.bbangduck.hello;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HelloControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("Rest Docs 설정 검증을 위한 Test")
    public void helloRestDocs() throws Exception {
        //given

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/hello")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "hello"
                ))
        ;

    }

}
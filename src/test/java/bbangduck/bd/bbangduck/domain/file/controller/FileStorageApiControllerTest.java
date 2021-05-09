package bbangduck.bd.bbangduck.domain.file.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileStorageApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("파일 업로드")
    public void uploadFile() throws Exception {
        //given
        MockMultipartFile multipartFile1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        MockMultipartFile multipartFile2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);

        MemberSignUpDto signUpDto = MemberSignUpDto.builder()
                .email("test@email.com")
                .nickname("test")
                .password("test")
                .socialType(null)
                .socialId(null)
                .build();

        Long signUpMemberId = authenticationService.signUp(signUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));
        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);


        //when
        ResultActions perform = mockMvc.perform(
                multipart("/api/files/images")
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getTotalAccessToken())
        ).andDo(print());

        //then
        perform.andExpect(status().isOk());

    }

}
package bbangduck.bd.bbangduck.domain.theme.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberDevelopService;
import bbangduck.bd.bbangduck.domain.theme.dto.controller.request.ThemeImageRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ThemeDeveloperApiControllerTest extends BaseControllerTest {

    @MockBean
    ThemeRepository themeRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MemberDevelopService memberDevelopService;

    @Autowired
    SecurityJwtProperties securityJwtProperties;

    @Test
    @DisplayName("관리자 권한을 사용한 테마 이미지 추가")
    public void addImageToThemeByDeveloper() throws Exception {
        //given
        Theme theme = Theme.builder()
                .id(1L)
                .name("theme")
                .build();

        given(themeRepository.findById(theme.getId())).willReturn(Optional.of(theme));

        ThemeImageRequestDto themeImageRequestDto = new ThemeImageRequestDto(1L, "fileName");

        Member developMember = memberDevelopService.createDevelopMember();
        System.out.println("developMember.getRoles() = " + developMember.getRoles());
        TokenDto tokenDto = authenticationService.signIn(developMember.getId());

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/develop/themes/{themeId}/images", theme.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themeImageRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "add-image-to-theme-by-developer-success"
                ))
        ;

    }

}
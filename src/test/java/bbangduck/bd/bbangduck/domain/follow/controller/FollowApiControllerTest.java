package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Follow API 테스트")
class FollowApiControllerTest extends BaseControllerTest {

    @MockBean
    FollowRepository followRepository;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SecurityJwtProperties securityJwtProperties;

    @Test
    @DisplayName("팔로우 요청")
    public void requestFollow() throws Exception {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .email("followingMember@emai.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        Member followedMember = Member.builder()
                .id(2L)
                .email("followedMember@emai.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        given(memberRepository.findById(followingMember.getId())).willReturn(Optional.of(followingMember));
        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.of(followedMember));
        given(followRepository.findByFollowingMemberAndFollowedMember(followingMember, followedMember)).willReturn(Optional.empty());
        given(followRepository.findByFollowingMemberAndFollowedMember(followedMember, followingMember)).willReturn(Optional.empty());

        String accessToken = jwtTokenProvider.createToken(followingMember.getEmail(), followedMember.getRoleNameList());

        given(memberRepository.findByEmail(followingMember.getEmail())).willReturn(Optional.of(followingMember));

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/follows/{memberId}/request-follow", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "request-follow-success"
                ))
        ;

    }

}
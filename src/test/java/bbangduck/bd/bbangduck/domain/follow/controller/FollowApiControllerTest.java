package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Follow API 테스트")
class FollowApiControllerTest extends BaseControllerTest {

    @MockBean
    FollowRepository followRepository;

    @MockBean
    FollowQueryRepository followQueryRepository;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SecurityJwtProperties securityJwtProperties;

    private final String TOKEN_DESCRIPTION = "인증을 위한 Access Token 기입";

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
                        "request-follow-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description("인증에 필요한 Access Token 기입")
                        )
                ))
        ;
    }

    // TODO: 2021-07-15 팔로우 요청 - 인증되지 않은 경우
    // TODO: 2021-07-15 팔로우 요청 - 탈퇴한 회원인 경우
    // TODO: 2021-07-15 팔로우 대상이 탈퇴하거나 활동이 정지된 경우
    // TODO: 2021-07-15 팔로우 대상을 찾을 수 없는 경우

    @Test
    @DisplayName("팔로우 해제")
    public void unfollow() throws Exception {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .email("member1@email.com")
                .nickname("member1")
                .roles(Set.of(MemberRole.USER))
                .build();

        Member followedMember = Member.builder()
                .id(2L)
                .email("member2@email.com")
                .nickname("member2")
                .roles(Set.of(MemberRole.USER))
                .build();

        Follow follow = Follow.builder()
                .followingMember(followingMember)
                .followedMember(followedMember)
                .build();

        given(followQueryRepository.findByFollowingMemberIdAndFollowedMemberId(followingMember.getId(), followedMember.getId())).willReturn(Optional.of(follow));

        String accessToken = jwtTokenProvider.createToken(followingMember.getEmail(), followingMember.getRoleNameList());
        given(memberRepository.findByEmail(followingMember.getEmail())).willReturn(Optional.of(followingMember));

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/follows/{memberId}/unfollow", followingMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "unfollow-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(TOKEN_DESCRIPTION)
                        )
                ))
        ;

    }

    @SneakyThrows
    @Test
    @DisplayName("팔로우 해제 - 인증되지 않은 경우")
    public void unfollow_Unauthorized() throws Exception {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .email("member1@email.com")
                .nickname("member1")
                .roles(Set.of(MemberRole.USER))
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/follows/{memberId}/unfollow", followingMember.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("팔로우 해제 - 탈퇴한 회원이 리소스 접근")
    public void unfollow_Forbidden() throws Exception {
        //given
        Member followingMember = Member.builder()
                .id(1L)
                .email("member1@email.com")
                .nickname("member1")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        String accessToken = jwtTokenProvider.createToken(followingMember.getEmail(), followingMember.getRoleNameList());
        given(memberRepository.findByEmail(followingMember.getEmail())).willReturn(Optional.of(followingMember));

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/follows/{memberId}/unfollow", followingMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

}
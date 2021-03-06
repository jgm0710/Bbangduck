package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.FollowStatus;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.exception.FoundMemberIsWithdrawalOrBanException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import com.querydsl.core.QueryResults;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Follow API ?????????")
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

    private final String TOKEN_DESCRIPTION = "????????? ?????? Access Token ??????";

    @Test
    @DisplayName("????????? ??????")
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
                post("/api/members/{memberId}/follows", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "request-follow-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description("????????? ????????? Access Token ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("????????? ??????")
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
                delete("/api/members/{memberId}/follows", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
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
    @DisplayName("????????? ?????? - ???????????? ?????? ??????")
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
                delete("/api/members/{memberId}/follows", followingMember.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("????????? ?????? - ????????? ????????? ????????? ??????")
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
                delete("/api/members/{memberId}/follows", followingMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ??????")
    public void getFollowingMemberList() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        List<Member> followedMembers = new ArrayList<>();
        List<Follow> follows = new ArrayList<>();
        for (long i = 2; i < 6; i++) {
            Member followedMember = Member.builder()
                    .id(i)
                    .nickname("member"+i)
                    .description("description"+i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName(UUID.randomUUID() + "fileName" + nextInt)
                    .build();

            followedMember.setProfileImage(memberProfileImage);

            followedMembers.add(followedMember);

            Follow follow = Follow.builder()
                    .followingMember(member)
                    .followedMember(followedMember)
                    .build();

            follows.add(follow);
        }

        CriteriaDto criteriaDto = new CriteriaDto(1,4);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        QueryResults<Follow> followQueryResults = new QueryResults<>(follows, (long) criteriaDto.getAmount(), (long) criteriaDto.getOffset(), 132);
        given(followQueryRepository.findListByFollowingMemberId(any(Long.class), any(CriteriaDto.class))).willReturn(followQueryResults);

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());


        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-following-member-list-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(TOKEN_DESCRIPTION)
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("????????? ????????? ??????"),
                                parameterWithName("amount").description("????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("contents").description("?????? ?????? ??????"),
                                fieldWithPath("contents[].memberId").description("???????????? ????????? ?????? ID"),
                                fieldWithPath("contents[].nickname").description("???????????? ????????? ?????????"),
                                fieldWithPath("contents[].description").description("???????????? ????????? ????????????"),
                                fieldWithPath("contents[].profileImageUrl").description("???????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("contents[].profileImageThumbnailUrl").description("???????????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nowPageNum").description("?????? ????????? ?????????"),
                                fieldWithPath("requestAmount").description("?????? ????????? ??????"),
                                fieldWithPath("totalResultsCount").description("?????? ?????? ??? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ?????? - ???????????? ????????? ?????? ??? ????????????")
    public void getFollowingMemberList_MemberNotFound() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        CriteriaDto criteriaDto = new CriteriaDto(1,4);

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NOT_FOUND.getMessage()))
        ;

    }

    @ParameterizedTest
    @MethodSource("parametersFor_getFollowingMemberList_NotValid")
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ?????? - ????????? ?????? ?????? ????????? ??????")
    public void getFollowingMemberList_NotValid(CriteriaDto criteriaDto) throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_FOLLOWING_MEMBER_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.GET_FOLLOWING_MEMBER_LIST_NOT_VALID.getMessage()));
    }

    private static Stream<Arguments> parametersFor_getFollowingMemberList_NotValid() {
        return Stream.of(
                Arguments.of(new CriteriaDto(-1, 10)),
                Arguments.of(new CriteriaDto(0, 10)),
                Arguments.of(new CriteriaDto(1, -1)),
                Arguments.of(new CriteriaDto(1, 0)),
                Arguments.of(new CriteriaDto(1, 501))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersFor_getFollowingMemberList_MemberIsWithdrawalOrBan")
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ?????? - ???????????? ????????? ??????????????? ????????? ????????? ????????? ??????")
    public void getFollowingMemberList_MemberIsWithdrawalOrBan(Set<MemberRole> selectedMemberRoles) throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .roles(selectedMemberRoles)
                .build();

        CriteriaDto criteriaDto = new CriteriaDto(1,4);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        Member member2 = Member.builder()
                .email("member2@email.com")
                .roles(Set.of(MemberRole.USER))
                .build();

        String accessToken = jwtTokenProvider.createToken(member2.getEmail(), member2.getRoleNameList());
        given(memberRepository.findByEmail(member2.getEmail())).willReturn(Optional.of(member2));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.FOUND_MEMBER_IS_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("message").value(new FoundMemberIsWithdrawalOrBanException(member.getId()).getMessage()))
        ;

    }

    private static Stream<Arguments> parametersFor_getFollowingMemberList_MemberIsWithdrawalOrBan() {
        return Stream.of(
                Arguments.of(Set.of(MemberRole.WITHDRAWAL)),
                Arguments.of(Set.of(MemberRole.BAN))
        );
    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ?????? - ???????????? ?????? ???????????? ??????")
    public void getFollowingMemberList_Unauthorized() throws Exception {
        //given
        CriteriaDto criteriaDto = new CriteriaDto(1,4);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", 1L)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("?????? ????????? ???????????? ?????? ?????? ?????? - ????????? ????????? ??????")
    public void getFollowingMemberList_Forbidden() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        CriteriaDto criteriaDto = new CriteriaDto(1,4);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followings", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", criteriaDto.getPageNum().toString())
                        .param("amount", criteriaDto.getAmount().toString())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void getFollowerMemberList() throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.USER))
                .build();

        List<Follow> followedList = new ArrayList<>();
        for (long i = 1; i < 6; i++) {
            Member followingMember = Member.builder()
                    .id(i)
                    .nickname("member" + i)
                    .description("description" + i)
                    .build();

            int randomInt = new Random().nextInt(100);
            MemberProfileImage profileImage = MemberProfileImage.builder()
                    .fileStorageId((long) randomInt)
                    .fileName(UUID.randomUUID() + "fileName" + randomInt)
                    .build();

            followingMember.setProfileImage(profileImage);

            Follow follow = Follow.builder()
                    .followingMember(followingMember)
                    .followedMember(followedMember)
                    .build();
            followedList.add(follow);
        }

        QueryResults<Follow> followQueryResults = new QueryResults<>(followedList, 1L, 1L, 1);

        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.of(followedMember));
        given(followQueryRepository.findListByFollowedMemberId(any(), any())).willReturn(followQueryResults);

        String accessToken = jwtTokenProvider.createToken(followedMember.getEmail(), followedMember.getRoleNameList());
        given(memberRepository.findByEmail(followedMember.getEmail())).willReturn(Optional.of(followedMember));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", "1")
                        .param("amount","20")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-follower-member-list-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader())   .description("????????? ????????? Access Token ??????")
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("????????? ????????? ??????"),
                                parameterWithName("amount").description("????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("contents").description("?????? ?????? ??????"),
                                fieldWithPath("contents[].memberId").description("?????? ????????? ???????????? ????????? ?????? ID"),
                                fieldWithPath("contents[].nickname").description("?????? ????????? ???????????? ????????? ?????????"),
                                fieldWithPath("contents[].description").description("?????? ????????? ???????????? ????????? ????????????"),
                                fieldWithPath("contents[].profileImageUrl").description("?????? ????????? ???????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("contents[].profileImageThumbnailUrl").description("?????? ????????? ???????????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nowPageNum").description("?????? ????????? ?????????"),
                                fieldWithPath("requestAmount").description("?????? ????????? ??????"),
                                fieldWithPath("totalResultsCount").description("?????? ?????? ??? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ????????? ?????? ??? ?????? ??????")
    public void getFollowerMemberList_MemberNotFound() throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.USER))
                .build();


        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.empty());

        String accessToken = jwtTokenProvider.createToken(followedMember.getEmail(), followedMember.getRoleNameList());
        given(memberRepository.findByEmail(followedMember.getEmail())).willReturn(Optional.of(followedMember));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NOT_FOUND.getMessage()))
        ;
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ????????? ????????? ????????? ??????")
    public void getFollowerMemberList_FindMemberIsWithdrawalMember() throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.of(followedMember));

        Member member2 = Member.builder()
                .id(2000L)
                .email("member2@email.com")
                .nickname("member2")
                .roles(Set.of(MemberRole.USER))
                .build();

        String accessToken = jwtTokenProvider.createToken(member2.getEmail(), member2.getRoleNameList());
        given(memberRepository.findByEmail(member2.getEmail())).willReturn(Optional.of(member2));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.FOUND_MEMBER_IS_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("message").value(new FoundMemberIsWithdrawalOrBanException(followedMember.getId()).getMessage()));
    }

    @ParameterizedTest
    @MethodSource("parametersFor_getFollowingMemberList_NotValid")
    @DisplayName("????????? ?????? ?????? - ????????? ?????? ?????? ?????? ????????? ??????")
    public void getFollowerMemberList_NotValid(CriteriaDto criteriaDto) throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.USER))
                .build();

        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.of(followedMember));

        String accessToken = jwtTokenProvider.createToken(followedMember.getEmail(), followedMember.getRoleNameList());
        given(memberRepository.findByEmail(followedMember.getEmail())).willReturn(Optional.of(followedMember));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_FOLLOWER_MEMBER_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.GET_FOLLOWER_MEMBER_LIST_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ???????????? ?????? ??????")
    public void getFollowerMemberList_Unauthorized() throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.USER))
                .build();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("????????? ?????? ?????? - ????????? ????????? ????????? ??????")
    public void getFollowerMemberList_Forbidden() throws Exception {
        //given
        Member followedMember = Member.builder()
                .id(1000L)
                .email("followedMember@emai.com")
                .nickname("followedMember")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        String accessToken = jwtTokenProvider.createToken(followedMember.getEmail(), followedMember.getRoleNameList());
        given(memberRepository.findByEmail(followedMember.getEmail())).willReturn(Optional.of(followedMember));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/followers", followedMember.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("???????????? ?????? ?????? ??????")
    public void getTwoWayFollowMemberList() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.USER))
                .build();


        List<Member> followedMembers = new ArrayList<>();
        List<Follow> twoWayFollows = new ArrayList<>();
        for (long i = 4; i < 4 + 5; i++) {
            Member followedMember = Member.builder()
                    .id(i)
                    .nickname("member" + i)
                    .description("description" + i)
                    .build();

            int nextInt = new Random().nextInt(100);
            MemberProfileImage memberProfileImage = MemberProfileImage.builder()
                    .id(i)
                    .fileStorageId((long) nextInt)
                    .fileName(UUID.randomUUID()+"fileName" + nextInt)
                    .build();

            followedMember.setProfileImage(memberProfileImage);

            followedMembers.add(followedMember);

            Follow following = Follow.builder()
                    .id(i + 100)
                    .followingMember(member)
                    .followedMember(followedMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            twoWayFollows.add(following);
        }

        CriteriaDto criteriaDto = new CriteriaDto();

        QueryResults<Follow> followQueryResults = new QueryResults<>(twoWayFollows, (long) criteriaDto.getAmount(), (long) criteriaDto.getOffset(), twoWayFollows.size());

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(followQueryRepository.findTwoWayFollowListByFollowingMemberId(any(), any())).willReturn(followQueryResults);

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-two-way-followers-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(TOKEN_DESCRIPTION)
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("????????? ????????? ??????"),
                                parameterWithName("amount").description("????????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("contents").description("?????? ?????? ??????"),
                                fieldWithPath("contents[].memberId").description("??????????????? ????????? ?????? ID"),
                                fieldWithPath("contents[].nickname").description("??????????????? ????????? ?????????"),
                                fieldWithPath("contents[].description").description("??????????????? ????????? ????????????"),
                                fieldWithPath("contents[].profileImageUrl").description("??????????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("contents[].profileImageThumbnailUrl").description("??????????????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("nowPageNum").description("?????? ????????? ?????????"),
                                fieldWithPath("requestAmount").description("?????? ????????? ??????"),
                                fieldWithPath("totalResultsCount").description("?????? ?????? ??? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? - ????????? ????????? ?????? ??? ?????? ??????")
    public void getTwoWayFollowMemberList_MemberNotFound() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.USER))
                .build();

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? - ????????? ????????? ????????? ????????? ?????? ")
    public void getTwoWayFollowMemberList_FindMemberIsWithdrawal() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        CriteriaDto criteriaDto = new CriteriaDto();


        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        Member member2 = Member.builder()
                .email("member2@email.com")
                .roles(Set.of(MemberRole.USER))
                .build();
        String accessToken = jwtTokenProvider.createToken(member2.getEmail(), member2.getRoleNameList());
        given(memberRepository.findByEmail(member2.getEmail())).willReturn(Optional.of(member2));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.FOUND_MEMBER_IS_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("message").value(new FoundMemberIsWithdrawalOrBanException(member.getId()).getMessage()));
    }


    @ParameterizedTest
    @MethodSource("parametersFor_getFollowingMemberList_NotValid")
    @DisplayName("???????????? ?????? ?????? ?????? - ????????? ?????? ?????? ????????? ??????")
    public void getTwoWayFollowMemberList_NotValid(CriteriaDto criteriaDto) throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.USER))
                .build();

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_TWO_WAY_FOLLOW_MEMBER_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.GET_TWO_WAY_FOLLOW_MEMBER_LIST_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? - ???????????? ?????? ")
    public void getTwoWayFollowMemberList_Unauthorized() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.USER))
                .build();


        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("???????????? ?????? ?????? ?????? - ????????? ????????? ????????? ??????")
    public void getTwoWayFollowMemberList_Forbidden() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("member@email.com")
                .nickname("member")
                .roles(Set.of(MemberRole.WITHDRAWAL))
                .build();

        CriteriaDto criteriaDto = new CriteriaDto();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoleNameList());
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/two-way-followers", member.getId())
                        .header(securityJwtProperties.getJwtTokenHeader(), accessToken)
                        .param("pageNum", String.valueOf(criteriaDto.getPageNum()))
                        .param("amount", String.valueOf(criteriaDto.getAmount()))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));
    }

}
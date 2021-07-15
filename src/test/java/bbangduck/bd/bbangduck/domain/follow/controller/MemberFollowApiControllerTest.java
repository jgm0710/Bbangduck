package bbangduck.bd.bbangduck.domain.follow.controller;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.JwtTokenProvider;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.exception.FindMemberIsWithdrawalOrBanException;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원과 관련된 팔로우 EndPoint Api 테스트")
class MemberFollowApiControllerTest extends BaseControllerTest {

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    FollowQueryRepository followQueryRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SecurityJwtProperties securityJwtProperties;


    @Test
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회")
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
        given(followQueryRepository.findListByFollowingMemberId(any(Long.class), any(CriteriaDto.class))).willReturn(follows);

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
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description("인증에 필요한 Access Token 기입")
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("조회할 페이지 기입"),
                                parameterWithName("amount").description("조회할 수량 기입")
                        ),
                        responseFields(
                                fieldWithPath("[].memberId").description("팔로우한 회원의 식별 ID"),
                                fieldWithPath("[].nickname").description("팔로우한 회원의 닉네임"),
                                fieldWithPath("[].description").description("팔로우한 회원의 자기소개"),
                                fieldWithPath("[].profileImageUrl").description("팔로우한 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("[].profileImageThumbnailUrl").description("팔로우한 회원의 프로필 이미지 썸네일 이미지 다운로드 URL")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회 - 조회되는 회원을 찾을 수 없는경우")
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
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회 - 페이징 값을 잘못 기입한 경우")
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
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회 - 조회되는 회원이 탈퇴되거나 계정이 정지된 회원일 경우")
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
                .andExpect(jsonPath("status").value(ResponseStatus.FIND_MEMBER_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("message").value(new FindMemberIsWithdrawalOrBanException(member.getId()).getMessage()))
        ;

    }

    private static Stream<Arguments> parametersFor_getFollowingMemberList_MemberIsWithdrawalOrBan() {
        return Stream.of(
                Arguments.of(Set.of(MemberRole.WITHDRAWAL)),
                Arguments.of(Set.of(MemberRole.BAN))
        );
    }

    @Test
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회 - 인증되지 않은 사용자가 조회")
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
    @DisplayName("특정 회원이 팔로우한 회원 목록 조회 - 탈퇴한 회원이 조회")
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
    @DisplayName("팔로워 목록 조회")
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


        given(memberRepository.findById(followedMember.getId())).willReturn(Optional.of(followedMember));
        given(followQueryRepository.findListByFollowedMemberId(any(), any())).willReturn(followedList);

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
                                headerWithName(securityJwtProperties.getJwtTokenHeader())   .description("인증에 필요한 Access Token 기입")
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("조회할 페이지 기입"),
                                parameterWithName("amount").description("조회할 수량 기입")
                        ),
                        responseFields(
                                fieldWithPath("[].memberId").description("해당 회원을 팔로우한 회원의 식별 ID"),
                                fieldWithPath("[].nickname").description("해당 회원을 팔로우한 회원의 닉네임"),
                                fieldWithPath("[].description").description("해당 회원을 팔로우한 회원의 자기소개"),
                                fieldWithPath("[].profileImageUrl").description("해당 회원을 팔로우한 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("[].profileImageThumbnailUrl").description("해당 회원을 팔로우한 회원의 프로필 이미지 썸네일 이미지 다운로드 URL")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("팔로워 목록 조회 - 조회되는 회원을 찾을 수 없는 경우")
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
    @DisplayName("팔로워 목록 조회 - 조회되는 회원이 탈퇴된 회원인 경우")
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
                .andExpect(jsonPath("status").value(ResponseStatus.FIND_MEMBER_WITHDRAWAL_OR_BAN.getStatus()))
                .andExpect(jsonPath("message").value(new FindMemberIsWithdrawalOrBanException(followedMember.getId()).getMessage()));
    }

    @ParameterizedTest
    @MethodSource("parametersFor_getFollowingMemberList_NotValid")
    @DisplayName("팔로워 목록 조회 - 페이징 기입 값을 잘못 기입한 경우")
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
    @DisplayName("팔로워 목록 조회 - 인증되지 않은 경우")
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
    @DisplayName("팔로워 목록 조회 - 탈퇴한 회원이 리소스 접근")
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


}
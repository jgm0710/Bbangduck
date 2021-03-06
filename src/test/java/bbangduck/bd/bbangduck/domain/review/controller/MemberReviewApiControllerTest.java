package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.api.document.utils.DocUrl;
import bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewSearchType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Stream;

import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.REVIEW_SEARCH_TYPE;
import static bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberReviewApiControllerTest extends BaseJGMApiControllerTest {

    @ParameterizedTest
    @MethodSource("provideParametersForGetMemberReviewList")
    @DisplayName("특정 회원이 생성한 리뷰 목록 조회 - 방탈출 비공개, 자신이 생성한 리뷰 목록 조회")
    public void getMemberReviewList(ReviewSearchType searchType) throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme themeSample = createThemeSample();

        createReviewSampleList(signUpId, friendIds, themeSample.getId());

        memberService.updateRoomEscapeRecodesOpenStatus(signUpId, MemberRoomEscapeRecodesOpenStatus.CLOSE);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/reviews", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "8")
                        .param("searchType", searchType.name())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("contents").isArray())
                .andExpect(jsonPath("nowPageNum").value(2))
                .andExpect(jsonPath("requestAmount").value(8))
                .andExpect(jsonPath("totalResultsCount").hasJsonPath())
                .andDo(document(
                        "get-member-review-list-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("조회할 페이지 기입 +\n" +
                                        "1 보다 작은 페이지는 조회할 수 없습니다."),
                                parameterWithName("amount").description("조회할 수량 기입 +\n" +
                                        "한 번에 조회 가능한 수량은 1~200 개 입니다. +\n" +
                                        "해당 수량은 추후 변경 될 수 있습니다."),
                                parameterWithName("searchType").description("조회 조건 입력 +\n" +
                                        "전체 조회, 클리어 성공한 리뷰 조회, 클리어 실패한 리뷰 조회를 지정하여 검색 +\n" +
                                        generateLinkCode(REVIEW_SEARCH_TYPE))
                        ),
                        relaxedResponseFields(
                                fieldWithPath("contents").description("조회된 리뷰 목록에 대한 실제 응답 Data +\n" +
                                        "간단 리뷰, 간단 및 설문 작성 리뷰, 상세 리뷰, 상세 및 설문 작성 리뷰가 모두 응답됨 -> 각 응답 형태는 리뷰 1건 조회 리소스를 통해 참조"),
                                fieldWithPath("nowPageNum").description("현재 요청한 페이지 번호"),
                                fieldWithPath("requestAmount").description("현재 요청한 수량"),
                                fieldWithPath("totalResultsCount").description("요청 시 입력한 pageNum, amount, searchType 에 의해 조회된 결과의 총 개수")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("특정 회원의 리뷰 목록 조회 - 다른 회원이 생성한 리뷰를 조회하는데 해당 회원이 방탈출 기록을 공개하지 않은 경우")
    public void getMemberReviewList_DifferentMembersReview_RecodesOpenClose() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, member1Id);

        Theme themeSample = createThemeSample();

        createReviewSampleList(member1Id, friendIds, themeSample.getId());

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3218390708");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberService.updateRoomEscapeRecodesOpenStatus(member1Id,MemberRoomEscapeRecodesOpenStatus.CLOSE);

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/reviews", member1Id)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "1")
                        .param("amount", "10")
                        .param("searchType", ReviewSearchType.TOTAL.name())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_NOT_OPEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_NOT_OPEN.getMessage()))
        ;

    }


    @Test
    @DisplayName("특정 회원이 생성한 리뷰 목록 조회 - 다른 회원이 생성한 리뷰 조회, 해당 회원이 친구에게만 조회를 허락한 경우, 성공")
    public void getMemberReviewList_DifferentMembersReview_RecodesOnlyFriendOpen_Success() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme themeSample = createThemeSample();

        createReviewSampleList(signUpId, friendIds, themeSample.getId());

        memberService.updateRoomEscapeRecodesOpenStatus(signUpId, MemberRoomEscapeRecodesOpenStatus.ONLY_FRIENDS_OPEN);

        TokenDto tokenDto = authenticationService.signIn(friendIds.get(0));
        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/reviews", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "8")
                        .param("searchType", ReviewSearchType.TOTAL.name())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("특정 회원이 생성한 리뷰 목록 조회 - 다른 회원이 생성한 리뷰 조회, 해당 회원이 친구에게만 조회를 허락한 경우, 실패")
    public void getMemberReviewList_DifferentMembersReview_RecodesOnlyFriendOpen_Fail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme themeSample = createThemeSample();

        createReviewSampleList(signUpId, friendIds, themeSample.getId());

        memberService.updateRoomEscapeRecodesOpenStatus(signUpId, MemberRoomEscapeRecodesOpenStatus.ONLY_FRIENDS_OPEN);

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("38210371289378");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/reviews", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "8")
                        .param("searchType", ReviewSearchType.TOTAL.name())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_ONLY_FRIEND_OPEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MEMBER_ROOM_ESCAPE_RECODES_ARE_ONLY_FRIEND_OPEN.getMessage()))
        ;

    }

    @Test
    @DisplayName("특정 회원의 리뷰 목록 조회 - 다른 회원이 생성한 리뷰를 조회하는데 해당 회원이 방탈출 기록을 공개한 경우")
    public void getMemberReviewList_DifferentMembersReview_RecodesOpenTrue() throws Exception {
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme themeSample = createThemeSample();

        createReviewSampleList(signUpId, friendIds, themeSample.getId());

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3218390708");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/members/{memberId}/reviews", signUpId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "2")
                        .param("searchType", ReviewSearchType.SUCCESS.name())
        ).andDo(print());

        //then
        perform.andExpect(status().isOk());

    }

    private static Stream<Arguments> provideParametersForGetMemberReviewList() {
        return Stream.of(
                Arguments.of(ReviewSearchType.SUCCESS),
                Arguments.of(ReviewSearchType.FAIL),
                Arguments.of(ReviewSearchType.TOTAL)
                );
    }

}
package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.service.KakaoSignInService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.follow.exception.NotTwoWayFollowRelationException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewSurveyCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.*;
import static bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ThemeReviewApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Test
    @DisplayName("?????? ?????? ??????")
    public void createSimpleReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then

        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "create-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] ??????"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("clearYN").description("?????? ????????? ????????? ??????"),
                                fieldWithPath("clearTime").description("????????? ?????????????????? ?????? ?????? ??????"),
                                fieldWithPath("hintUsageCount").description("????????? ?????????????????? ????????? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(REVIEW_HINT_USAGE_COUNT)),
                                fieldWithPath("rating").description("????????? ?????? ?????? ?????? +\n" +
                                        "????????? ?????? ????????? 1~5 ??? ????????? ???????????? ????????? ???????????????."),
                                fieldWithPath("friendIds").description("????????? ?????? ???????????? ????????? ???????????? ?????? ?????? ?????? ?????? ID ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("????????? ?????? ID"),
                                fieldWithPath("writerInfo").description("????????? ????????? ?????? ??????"),
                                fieldWithPath("writerInfo.memberId").description("????????? ????????? ????????? ?????? ID"),
                                fieldWithPath("writerInfo.nickname").description("????????? ????????? ????????? ?????????"),
                                fieldWithPath("writerInfo.profileImageUrl").description("????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("????????? ????????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("themeInfo").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("themeInfo.themeId").description("????????? ????????? ????????? ?????? ID"),
                                fieldWithPath("themeInfo.themeName").description("????????? ????????? ????????? ??????"),
                                fieldWithPath("themeInfo.themeImageUrl").description("????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("????????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("reviewType").description("????????? Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("????????? ????????? ?????? ??????"),
                                fieldWithPath("themeClearYN").description("?????? ????????? ??????"),
                                fieldWithPath("themeClearTime").description("????????? ?????????????????? ?????? ??????"),
                                fieldWithPath("hintUsageCount").description("?????? ?????? ??????"),
                                fieldWithPath("rating").description("????????? ?????? ??????"),
                                fieldWithPath("playTogetherFriends").description("????????? ?????? ???????????? ????????? ????????? ???????????? ?????? ??????"),
                                fieldWithPath("playTogetherFriends[].memberId").description("????????? ?????? ???????????? ?????? ????????? ?????? ID"),
                                fieldWithPath("playTogetherFriends[].nickname").description("????????? ?????? ???????????? ?????? ????????? ?????????"),
                                fieldWithPath("playTogetherFriends[].profileImageUrl").description("????????? ?????? ???????????? ?????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("playTogetherFriends[].profileImageThumbnailUrl").description("????????? ?????? ???????????? ?????? ????????? ????????? ????????? ????????? ????????? ???????????? URL"),
                                fieldWithPath("likeCount").description("????????? ????????? ?????? ??????"),
                                fieldWithPath("myReview").description("?????? ????????? ???????????? ??????"),
                                fieldWithPath("like").description("???????????? ????????? ???????????? ??????"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("????????? ?????? ????????? ???????????? ??????"),
                                fieldWithPath("surveyYN").description("????????? ????????? ?????????????????? ??????"),
                                fieldWithPath("registerTimes").description("?????? ?????? ??????"),
                                fieldWithPath("updateTimes").description("?????? ????????? ?????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ????????? ????????? ???????????? ?????? ??????")
    public void createReview_ClearButClearTimeIsNull() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
        reviewCreateRequestDto.setClearYN(true);
        reviewCreateRequestDto.setClearTime(null);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getMessage()));

    }

    @Test
    @DisplayName("?????? ?????? - ??????????????? ???????????? ????????? ????????? ????????? ??????")
    public void createReview_NotClearButClearTimeIsNotNull() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
        reviewCreateRequestDto.setClearYN(false);
        reviewCreateRequestDto.setClearTime(LocalTime.of(0, 29, 44));

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ??????")
    public void createReview_DeletedTheme() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createDeletedThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_THEME.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_THEME.getMessage()));

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? x")
    public void createSimpleReview_NoPlayTogether() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
        ;

    }


    @Test
    @DisplayName("?????? ?????? - ?????? ?????? ??? ????????? ?????? ????????? ???????????? ???????????? ?????? ??????")
    public void createReview_Empty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto reviewCreateRequestDto = ReviewCreateRequestDto.builder()
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-review-empty",
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description(OBJECT_NAME_DESCRIPTION),
                                fieldWithPath("data[0].code").description(CODE_DESCRIPTION),
                                fieldWithPath("data[0].defaultMessage").description(DEFAULT_MESSAGE_DESCRIPTION),
                                fieldWithPath("data[0].field").description(FIELD_DESCRIPTION),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ???????????? ?????? ????????? ?????? ??????")
    public void createReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();


        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ?????? ??? ????????? ?????? ?????? ????????? ???????????? ?????? ??????")
    public void createReview_OverPlayTogetherFriendsCount() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        List<Long> friendIds = new ArrayList<>();
        for (long i = 0; i < reviewProperties.getPlayTogetherFriendsCountLimit()+2; i++) {
            friendIds.add(i);
        }

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();


        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-review-over-play-together",
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data[0].objectName").description(OBJECT_NAME_DESCRIPTION),
                                fieldWithPath("data[0].code").description(CODE_DESCRIPTION),
                                fieldWithPath("data[0].defaultMessage").description(DEFAULT_MESSAGE_DESCRIPTION),
                                fieldWithPath("data[0].field").description(FIELD_DESCRIPTION),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ?????? ??????")
    public void createReview_By_WithdrawalMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????? ??? ?????? ??????")
    public void createReview_ThemeNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + 10000L + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.THEME_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.THEME_NOT_FOUND.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? - ?????? ?????? ??? ????????? ????????? ?????? ??????????????? ????????? ?????? ????????? ?????? ??????")
    public void createReview_PlayTogetherNotFriendMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);
        Member requestStateFriendToMember = createOneWayFollowMember(memberSocialSignUpRequestDto, signUpId);
        friendIds.set(4, requestStateFriendToMember.getId());


        Theme theme = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.NOT_TWO_WAY_FOLLOW_RELATION.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new NotTwoWayFollowRelationException(signUpId, requestStateFriendToMember.getId()).getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ??????")
    @Transactional
    public void getReviewList() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("33611223");

        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member3@emai.com");
        memberSocialSignUpRequestDto.setNickname("member3");
        memberSocialSignUpRequestDto.setSocialId("33119372");

        Long member3Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Member member1 = memberService.getMember(member1Id);
        Member member2 = memberService.getMember(member2Id);
        Member member3 = memberService.getMember(member3Id);

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, member1Id);

        List<Member> friends = friendIds.stream().map(friendId -> memberService.getMember(friendId)).collect(Collectors.toList());

        createSampleReviewList(member1, member2, member3, theme, friends);

        em.flush();
        em.clear();

        TokenDto tokenDto = authenticationService.signIn(member1Id);

        //when
        System.out.println("====================================================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "4")
                        .param("sortCondition", "LIKE_COUNT_DESC")
        ).andDo(print());
        System.out.println("====================================================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("nowPageNum").exists())
                .andExpect(jsonPath("requestAmount").exists())
                .andExpect(jsonPath("totalResultsCount").exists())
                .andDo(document(
                        "get-theme-review-list-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("????????? ????????? ?????? +\n" +
                                        "1 ?????? ?????? ???????????? ????????? ??? ????????????."),
                                parameterWithName("amount").description("????????? ?????? ?????? +\n" +
                                        "??? ?????? ?????? ????????? ????????? 1~200 ??? ?????????. +\n" +
                                        "?????? ????????? ?????? ?????? ??? ??? ????????????."),
                                parameterWithName("sortCondition").description("?????? ??? ?????? ?????? ?????? +\n" +
                                        generateLinkCode(REVIEW_SORT_CONDITION))
                        ),
                        relaxedResponseFields(
                                fieldWithPath("contents").description("????????? ?????? ????????? ?????? ?????? ?????? Data +\n" +
                                        "?????? ??????, ?????? ??? ?????? ?????? ??????, ?????? ??????, ?????? ??? ?????? ?????? ????????? ?????? ????????? -> ??? ?????? ????????? ?????? 1??? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("nowPageNum").description("?????? ????????? ????????? ??????"),
                                fieldWithPath("requestAmount").description("?????? ????????? ??????"),
                                fieldWithPath("totalResultsCount").description("????????? ????????? ??? ??????")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ????????? 1?????? ?????? ????????? ??????")
    public void getReviewList_PageNum_LT_1() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createThemeSample();


        TokenDto tokenDto = authenticationService.signIn(member1Id);

        //when
        System.out.println("====================================================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "-1")
                        .param("amount", "4")
                        .param("sortCondition", "LIKE_COUNT_DESC")
        ).andDo(print());
        System.out.println("====================================================================================================================================================================================");

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ????????? 1?????? ?????? ????????? ??????")
    public void getReviewList_Amount_LT_1() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createThemeSample();


        TokenDto tokenDto = authenticationService.signIn(member1Id);

        //when
        System.out.println("====================================================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "1")
                        .param("amount", "-1")
                        .param("sortCondition", "LIKE_COUNT_DESC")
        ).andDo(print());
        System.out.println("====================================================================================================================================================================================");

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? - ????????? ????????? 200?????? ?????? ????????? ??????")
    public void getReviewList_Amount_GT_200() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createThemeSample();


        TokenDto tokenDto = authenticationService.signIn(member1Id);

        //when
        System.out.println("====================================================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/themes/" + theme.getId() + "/reviews")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "1")
                        .param("amount", "201")
                        .param("sortCondition", "LIKE_COUNT_DESC")
        ).andDo(print());
        System.out.println("====================================================================================================================================================================================");

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_THEME_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("?????? ?????? ?????? - ???????????? ?????? ????????? ?????? ?????? ??????")
    @Transactional
    public void getReviewList_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("33611223");

        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member3@emai.com");
        memberSocialSignUpRequestDto.setNickname("member3");
        memberSocialSignUpRequestDto.setSocialId("33119372");

        Long member3Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Member member1 = memberService.getMember(member1Id);
        Member member2 = memberService.getMember(member2Id);
        Member member3 = memberService.getMember(member3Id);

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, member1Id);

        List<Member> friends = friendIds.stream().map(friendId -> memberService.getMember(friendId)).collect(Collectors.toList());

        createSampleReviewList(member1, member2, member3, theme, friends);

        em.flush();
        em.clear();

        TokenDto tokenDto = authenticationService.signIn(member1Id);

        //when
        System.out.println("====================================================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/themes/" + theme.getId() + "/reviews")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .param("pageNum", "2")
                        .param("amount", "4")
                        .param("sortCondition", "LIKE_COUNT_DESC")
        ).andDo(print());
        System.out.println("====================================================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk());

    }

    private void createSampleReviewList(Member member1, Member member2, Member member3, Theme theme, List<Member> friends) throws IOException {
        Review member1SimpleReview1 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 41, 19))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
                .likeCount(312)
                .build();

        member1SimpleReview1.addPlayTogether(friends.get(0));

        Review member2SimpleReview1 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 51, 11))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(4)
                .likeCount(411)
                .build();

        member2SimpleReview1.addPlayTogether(friends.get(2));

        Review member3SimpleReview1 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 41, 19))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
                .likeCount(214)
                .build();

        member3SimpleReview1.addPlayTogether(friends.get(1));

//
        Review member1SimpleReview2 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 46, 29))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
                .likeCount(192)
                .build();

        member1SimpleReview2.addPlayTogether(friends.get(0));

        Review member2SimpleReview2 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 59, 12))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(4)
                .likeCount(442)
                .build();

        member2SimpleReview2.addPlayTogether(friends.get(2));

        Review member3SimpleReview2 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.BASE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 51, 29))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
                .likeCount(143)
                .build();

        member3SimpleReview2.addPlayTogether(friends.get(1));
//


        MockMultipartFile files1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFile1Id = fileStorageService.uploadImageFile(files1);
        FileStorage storedFile1 = fileStorageService.getStoredFile(uploadImageFile1Id);

        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadImageFile2Id = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadImageFile2Id);

        Review member1DetailReview1 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(2)
                .clearYN(false)
                .clearTime(LocalTime.of(1, 2, 19))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(8)
//                .comment("????????? ?????? ???????????? ???????????? ??? ???????????? ?????????~")
                .likeCount(1027)
                .build();

        member1DetailReview1.addPlayTogether(friends.get(0));
        member1DetailReview1.addPlayTogether(friends.get(1));
//        member1DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member1DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member2DetailReview1 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(2)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 38, 23))
                .hintUsageCount(ReviewHintUsageCount.NONE)
                .rating(6)
//                .comment("???????????? ?????? ????????? ??????????????? ???????????? ???????????? ???????????????. ?????? ????????? ??????????????? :)")
                .likeCount(682)
                .build();

        member2DetailReview1.addPlayTogether(friends.get(3));
        member2DetailReview1.addPlayTogether(friends.get(4));
//        member2DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member2DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member3DetailReview1 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(2)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 58, 11))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(9)
//                .comment("????????? ????????? ?????? ????????? ????????? ??????????????? ??????. ?????? ??????????????????.")
                .likeCount(721)
                .build();

        member3DetailReview1.addPlayTogether(friends.get(0));
        member3DetailReview1.addPlayTogether(friends.get(1));
//        member3DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member3DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member1DetailReview2 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 47, 34))
                .hintUsageCount(ReviewHintUsageCount.NONE)
                .rating(6)
//                .comment("???????????? ????????????, ???????????????")
                .likeCount(556)
                .build();

        member1DetailReview2.addPlayTogether(friends.get(0));
        member1DetailReview2.addPlayTogether(friends.get(1));
//        member1DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member1DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member2DetailReview2 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 34, 11))
                .hintUsageCount(ReviewHintUsageCount.NONE)
                .rating(4)
//                .comment("?????? ???????????????. ?????? ??? ????????? ???????????? ????????????.")
                .likeCount(10)
                .build();

        member2DetailReview2.addPlayTogether(friends.get(0));
        member2DetailReview2.addPlayTogether(friends.get(1));
//        member2DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member2DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member3DetailReview2 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(false)
                .clearTime(LocalTime.of(0, 59, 11))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
//                .comment("???????????? ???????????? ???????????????.")
                .likeCount(45)
                .build();

        member3DetailReview2.addPlayTogether(friends.get(0));
        member3DetailReview2.addPlayTogether(friends.get(1));
//        member3DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
//        member3DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        reviewRepository.save(member1SimpleReview1);
        reviewRepository.save(member2SimpleReview1);
        reviewRepository.save(member3SimpleReview1);
        reviewRepository.save(member1SimpleReview2);
        reviewRepository.save(member2SimpleReview2);
        reviewRepository.save(member3SimpleReview2);
        reviewRepository.save(member1DetailReview1);
        reviewRepository.save(member2DetailReview1);
        reviewRepository.save(member3DetailReview1);
        reviewRepository.save(member1DetailReview2);
        reviewRepository.save(member2DetailReview2);
        reviewRepository.save(member3DetailReview2);

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(createPerceivedThemeGenres());
        reviewApplicationService.addSurveyToReview(member3SimpleReview1.getId(), member3.getId(),reviewSurveyCreateRequestDto.toServiceDto());
        reviewApplicationService.addSurveyToReview(member2SimpleReview2.getId(), member2.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewApplicationService.addSurveyToReview(member1DetailReview1.getId(), member1.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewApplicationService.addSurveyToReview(member3DetailReview1.getId(), member3.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewApplicationService.addSurveyToReview(member3DetailReview2.getId(), member3.getId(), reviewSurveyCreateRequestDto.toServiceDto());
    }
}
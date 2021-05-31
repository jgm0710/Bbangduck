package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberFriendState;
import bbangduck.bd.bbangduck.domain.member.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ThemeReviewApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("간단 리뷰 생성")
    public void createSimpleReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        ReviewCreateRequestDto reviewCreateRequestDto = createSimpleReviewCreateRequestDto(friendIds);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_SIMPLE_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_SIMPLE_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "create-simple-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("생성하는 리뷰가 간단 리뷰인지, 상세 리뷰인지, 추가 설문 작성 리뷰인지 명시합니다. +\n" +
                                        "ReviewType 에 따라서 입력 규칙이 달라집니다. +\n" +
                                        REVIEW_TYPE_ENUM_LIST),
                                fieldWithPath("clearYN").description("테마 클리어 여부를 기입"),
                                fieldWithPath("clearTime").description("테마를 클리어하는데 걸린 시간 기입"),
                                fieldWithPath("hintUsageCount").description("테마를 클리어하는데 사용한 힌트 개수 기입"),
                                fieldWithPath("rating").description("테마에 대한 평점 기입"),
                                fieldWithPath("friendIds").description("테마를 함께 플레이한 친구를 등록하기 위해 친구 회원 식별 ID 목록 기입"),
                                fieldWithPath("reviewImages").description("[null]"),
                                fieldWithPath("comment").description("[null]"),
                                fieldWithPath("genreCodes").description("[null]"),
                                fieldWithPath("perceivedDifficulty").description("[null]"),
                                fieldWithPath("perceivedHorrorGrade").description("[null]"),
                                fieldWithPath("perceivedActivity").description("[null]"),
                                fieldWithPath("scenarioSatisfaction").description("[null]"),
                                fieldWithPath("interiorSatisfaction").description("[null]"),
                                fieldWithPath("problemConfigurationSatisfaction").description("[null]")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("간단 리뷰 생성 - 친구 등록 x")
    public void createSimpleReview_NoPlayTogether() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createTheme();

        ReviewCreateRequestDto reviewCreateRequestDto = createSimpleReviewCreateRequestDto(null);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_SIMPLE_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_SIMPLE_REVIEW_SUCCESS.getMessage()))
        ;

    }

    @Test
    @DisplayName("상세 리뷰 생성")
    public void createDetailReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "create-detail-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("생성하는 리뷰가 간단 리뷰인지, 상세 리뷰인지, 추가 설문 작성 리뷰인지 명시합니다. +\n" +
                                        "ReviewType 에 따라서 입력 규칙이 달라집니다. +\n" +
                                        REVIEW_TYPE_ENUM_LIST),
                                fieldWithPath("clearYN").description("테마 클리어 여부를 기입"),
                                fieldWithPath("clearTime").description("테마를 클리어하는데 걸린 시간 기입"),
                                fieldWithPath("hintUsageCount").description("테마를 클리어하는데 사용한 힌트 개수 기입"),
                                fieldWithPath("rating").description("테마에 대한 평점 기입"),
                                fieldWithPath("friendIds").description("테마를 함께 플레이한 친구를 등록하기 위해 친구 회원 식별 ID 목록 기입"),
                                fieldWithPath("reviewImages[0].fileStorageId").description("리뷰에 등록할 이미지 파일의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("리뷰에 등록할 이미지 파일의 파일 이름 기입"),
                                fieldWithPath("comment").description("테마에 대한 상세 코멘트 기입"),
                                fieldWithPath("genreCodes").description("[null]"),
                                fieldWithPath("perceivedDifficulty").description("[null]"),
                                fieldWithPath("perceivedHorrorGrade").description("[null]"),
                                fieldWithPath("perceivedActivity").description("[null]"),
                                fieldWithPath("scenarioSatisfaction").description("[null]"),
                                fieldWithPath("interiorSatisfaction").description("[null]"),
                                fieldWithPath("problemConfigurationSatisfaction").description("[null]")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("추가 설문 리뷰 생성")
    public void createDeepReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DEEP_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DEEP_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "create-deep-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("생성하는 리뷰가 간단 리뷰인지, 상세 리뷰인지, 추가 설문 작성 리뷰인지 명시합니다. +\n" +
                                        "ReviewType 에 따라서 입력 규칙이 달라집니다. +\n" +
                                        REVIEW_TYPE_ENUM_LIST),
                                fieldWithPath("clearYN").description("테마 클리어 여부를 기입"),
                                fieldWithPath("clearTime").description("테마를 클리어하는데 걸린 시간 기입"),
                                fieldWithPath("hintUsageCount").description("테마를 클리어하는데 사용한 힌트 개수 기입"),
                                fieldWithPath("rating").description("테마에 대한 평점 기입"),
                                fieldWithPath("friendIds").description("테마를 함께 플레이한 친구를 등록하기 위해 친구 회원 식별 ID 목록 기입"),
                                fieldWithPath("reviewImages[0].fileStorageId").description("리뷰에 등록할 이미지 파일의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("리뷰에 등록할 이미지 파일의 파일 이름 기입"),
                                fieldWithPath("comment").description("테마에 대한 상세 코멘트 기입"),
                                fieldWithPath("genreCodes").description("테마에 대한 체감 장르를 기입. +\n" +
                                        "장르 코드 목록을 기입하여 테마에 대한 여러 체감 장르를 등록."),
                                fieldWithPath("perceivedDifficulty").description("테마에 대한 체감 난이도 기입 +\n" +
                                        DIFFICULTY_ENUM_LIST),
                                fieldWithPath("perceivedHorrorGrade").description("테마에 대한 체감 공포도 기입 +\n" +
                                        HORROR_GRADE_ENUM_LIST),
                                fieldWithPath("perceivedActivity").description("테마에 대한 체감 활동성 기입 +\n" +
                                        ACTIVITY_ENUM_LIST),
                                fieldWithPath("scenarioSatisfaction").description("테마에 대한 시나리오 만족도 기입 +\n" +
                                        SATISFACTION_ENUM_LIST),
                                fieldWithPath("interiorSatisfaction").description("테마에 대한 인테리어 만족도 기입 +\n" +
                                        SATISFACTION_ENUM_LIST),
                                fieldWithPath("problemConfigurationSatisfaction").description("테마에 대한 문제 구성도 기입 +\n" +
                                        SATISFACTION_ENUM_LIST)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("추가 설문 리뷰 생성 - 장르 목록 중 하나의 장르 코드가 비어있는 경우")
    public void createDeepReview_GenreCodeBlank() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();
        genreCodes.add("");

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DEEP_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DEEP_REVIEW_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰 생성 - 리뷰 생성 시 리뷰에 관한 정보를 아무것도 입력하지 않은 경우")
    public void createReview_Empty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

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
    @DisplayName("간단 리뷰 생성 - 리뷰 생성 시 심플 리뷰인데 그보다 상위 리뷰의 정보를 입력했을 경우")
    public void createSimpleReview_NotSimple() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);
        reviewCreateRequestDto.setReviewType(ReviewType.SIMPLE);


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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_SIMPLE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_SIMPLE_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-simple-review-request-over-data",
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
    @DisplayName("상세 리뷰 생성 - 추가 설문에 관한 정보가 입력 됐을 경우")
    public void createDetailReview_IsDetailReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);
        reviewCreateRequestDto.setReviewType(ReviewType.DETAIL);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-detail-review-request-over-data",
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
    @DisplayName("상세 리뷰 생성 - 리뷰 생성 시 디테일 리뷰인데 코멘트를 입력하지 않았을 경우")
    public void createDetailReview_CommentIsBlank() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);
        reviewCreateRequestDto.setComment("");

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-detail-review-comment-empty",
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
    @DisplayName("상세 리뷰 생성 - 리뷰 생성 시 디테일 리뷰인데 이미지에 관한 정보를 아예 입력하지 않았을 경우 - 성공해야함")
    public void createDetailReview_ReviewImagesIsEmpty() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);
        reviewCreateRequestDto.setReviewImages(null);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_SUCCESS.getMessage()))
        ;


    }

    @Test
    @DisplayName("상세 리뷰 생성 - 리뷰 생성 시 디테일 리뷰인데 이미지에 관한 정보 입력 시 파일 저장소 ID 를 빼먹은 경우")
    public void createDetailReview_ReviewImageFileStorageIdIsNull() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        reviewImageRequestDtos.get(0).setFileStorageId(null);

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("상세 리뷰 생성 - 리뷰 생성 시 디테일 리뷰인데 이미지에 관한 정보 입력 시 파일 이름만 빼먹은 경우")
    public void createDetailReview_ReviewImageFileNameIsBlank() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        reviewImageRequestDtos.get(0).setFileName("");

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-detail-review-image-info-wrong",
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
    @DisplayName("추가 설문 리뷰 생성 - 간단 리뷰 정보 외의 정보는 전혀 기입하지 않은 경우")
    public void createDeepReview_NotDeep() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createSimpleReviewCreateRequestDto(friendIds);
        reviewCreateRequestDto.setReviewType(ReviewType.DEEP);

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
                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DEEP_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DEEP_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "create-deep-review-not-deep",
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
    @DisplayName("리뷰 생성 - 인증되지 않은 회원이 리뷰 작성")
    public void createReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
    @DisplayName("리뷰 생성 - 탈퇴한 회원이 리뷰 작성")
    public void createReview_By_WithdrawalMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

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
    @DisplayName("리뷰 생성 - 테마를 찾을 수 없는 경우")
    public void createReview_ThemeNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
    @DisplayName("리뷰 생성 - 리뷰 생성 시 인증된 회원가 함께 플레이하는 친구가 친구 관계가 아닌 경우")
    public void createReview_PlayTogetherNotFriendMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
        Member requestStateFriendToMember = createRequestStateFriendToMember(memberSocialSignUpRequestDto, signUpId);
        friendIds.add(requestStateFriendToMember.getId());


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
                .andExpect(jsonPath("status").value(ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new RelationOfMemberAndFriendIsNotFriendException(signUpId, requestStateFriendToMember.getId()).getMessage()))
        ;

    }

    @Test
    @DisplayName("추가 설문 리뷰 생성 - 리뷰 생성 시 등록하는 체감 테마 장르가 실제 존재하지 않는 장르일 경우")
    public void createReview_PerceivedThemeGenreNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();
        String amgn1 = "AMGN1";
        genreCodes.add(amgn1);

        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);

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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.GENRE_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new GenreNotFoundException(amgn1).getMessage()));

    }

    private Member createRequestStateFriendToMember(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
        memberSignUpRequestDto.setEmail("notFriend@email.com");
        memberSignUpRequestDto.setNickname("NotFriend");
        memberSignUpRequestDto.setSocialId("333311211");
        Long notFriendId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Member signUpMember = memberService.getMember(signUpId);
        Member notFriendMember = memberService.getMember(notFriendId);
        MemberFriend memberFriend = MemberFriend.builder()
                .member(signUpMember)
                .friend(notFriendMember)
                .state(MemberFriendState.REQUEST)
                .build();
        MemberFriend savedMemberFriend = memberFriendRepository.save(memberFriend);
        return savedMemberFriend.getFriend();
    }


    private List<Long> createFriendToMember(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
        Member signUpMember = memberService.getMember(signUpId);
        List<Long> friendIds = new ArrayList<>();
        for (int i = 100; i < 105; i++) {
            memberSignUpRequestDto.setEmail("test" + i + "@email.com");
            memberSignUpRequestDto.setNickname("test" + i);
            memberSignUpRequestDto.setSocialId("33333" + i);
            Long friendMemberId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
            Member friendMember = memberService.getMember(friendMemberId);

            MemberFriend memberFriend = MemberFriend.builder()
                    .member(signUpMember)
                    .friend(friendMember)
                    .state(MemberFriendState.ALLOW)
                    .build();

            MemberFriend savedMemberFriend = memberFriendRepository.save(memberFriend);
            Member savedFriend = savedMemberFriend.getFriend();
            friendIds.add(savedFriend.getId());
        }
        return friendIds;
    }

    private ReviewCreateDto createReviewCreateDto(List<FileStorage> storedFiles, List<Long> friendIds, List<String> genreCodes) {
        List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
        storedFiles.forEach(storedFile -> reviewImageDtoList.add(new ReviewImageDto(storedFile.getId(), storedFile.getFileName())));

        return ReviewCreateDto.builder()
                .reviewType(ReviewType.DEEP)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageDtoList)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.NORMAL)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.BAD)
                .build();
    }

    private List<String> createGenreCodes() {
        List<String> genreCodes = new ArrayList<>();
        genreCodes.add("RSN1");
        genreCodes.add("RMC1");
        return genreCodes;
    }

    private Theme createTheme() {
        Theme theme = Theme.builder()
                .shop(null)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        Genre rsn1 = genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new);
        theme.addGenre(rsn1);

        return themeRepository.save(theme);
    }

    private Theme createNotRegisterGenreTheme() {
        Theme theme = Theme.builder()
                .shop(null)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        return themeRepository.save(theme);
    }


    private ReviewCreateRequestDto createDeepReviewCreateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos, List<String> genreCodes) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.DEEP)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageRequestDtos)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.NORMAL)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.BAD)
                .build();
    }

    private List<ReviewImageRequestDto> createReviewImageRequestDtos() throws IOException {
        MockMultipartFile files1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId1 = fileStorageService.uploadImageFile(files1);
        FileStorage storedFile1 = fileStorageService.getStoredFile(uploadImageFileId1);

        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadImageFileId2 = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadImageFileId2);

        List<ReviewImageRequestDto> reviewImageRequestDtos = new ArrayList<>();
        reviewImageRequestDtos.add(new ReviewImageRequestDto(storedFile1.getId(), storedFile1.getFileName()));
        reviewImageRequestDtos.add(new ReviewImageRequestDto(storedFile2.getId(), storedFile2.getFileName()));
        return reviewImageRequestDtos;
    }

    private ReviewCreateRequestDto createSimpleReviewCreateRequestDto(List<Long> friendIds) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.SIMPLE)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .build();
    }

    private ReviewCreateRequestDto createDetailReviewCreateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.DETAIL)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageRequestDtos)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .build();
    }
}
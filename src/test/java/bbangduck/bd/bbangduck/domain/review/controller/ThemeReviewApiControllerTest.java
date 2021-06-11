package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.exception.RelationOfMemberAndFriendIsNotFriendException;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewSurveyCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.Review;
import bbangduck.bd.bbangduck.domain.review.entity.ReviewImage;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewSortCondition;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
                                fieldWithPath("comment").description("[null]")
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
                                fieldWithPath("comment").description("테마에 대한 상세 코멘트 기입")
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
                .andExpect(jsonPath("data[0].field").exists())
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

//    @Test
//    @DisplayName("상세 리뷰 생성 - 추가 설문에 관한 정보가 입력 됐을 경우")
//    public void createDetailReview_IsDetailReview() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//
//        Theme theme = createTheme();
//
//        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
//
//        List<String> genreCodes = createGenreCodes();
//
//        ReviewCreateRequestDto reviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);
//        reviewCreateRequestDto.setReviewType(ReviewType.DETAIL);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                post("/api/themes/" + theme.getId() + "/reviews")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewCreateRequestDto))
//        ).andDo(print());
//
//
//        //then
//        perform
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("status").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getStatus()))
//                .andExpect(jsonPath("data[0].objectName").exists())
//                .andExpect(jsonPath("data[0].code").exists())
//                .andExpect(jsonPath("data[0].defaultMessage").exists())
//                .andExpect(jsonPath("data[0].field").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.CREATE_DETAIL_REVIEW_NOT_VALID.getMessage()))
//                .andDo(document(
//                        "create-detail-review-request-over-data",
//                        responseFields(
//                                fieldWithPath("status").description(STATUS_DESCRIPTION),
//                                fieldWithPath("data[0].objectName").description(OBJECT_NAME_DESCRIPTION),
//                                fieldWithPath("data[0].code").description(CODE_DESCRIPTION),
//                                fieldWithPath("data[0].defaultMessage").description(DEFAULT_MESSAGE_DESCRIPTION),
//                                fieldWithPath("data[0].field").description(FIELD_DESCRIPTION),
//                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
//                        )
//                ))
//        ;
//
//    }

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
    @DisplayName("리뷰 생성 - 인증되지 않은 회원이 리뷰 작성")
    public void createReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createSimpleReviewCreateRequestDto(friendIds);

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
    @DisplayName("리뷰 생성 - 리뷰 생성 시 함께한 친구 수가 제한된 개수보다 많을 경우")
    public void createReview_OverPlayTogetherFriendsCount() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        List<Long> friendIds = new ArrayList<>();
        for (long i = 0; i < reviewProperties.getPlayTogetherFriendsCountLimit()+2; i++) {
            friendIds.add(i);
        }

        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

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
    @DisplayName("리뷰 생성 - 탈퇴한 회원이 리뷰 작성")
    public void createReview_By_WithdrawalMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        List<String> genreCodes = createGenreCodes();

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

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

        ReviewCreateRequestDto reviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

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
        friendIds.set(4, requestStateFriendToMember.getId());


        Theme theme = createTheme();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

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
                .andExpect(jsonPath("status").value(ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new RelationOfMemberAndFriendIsNotFriendException(signUpId, requestStateFriendToMember.getId()).getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰 목록 조회")
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

        Theme theme = createTheme();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, member1Id);

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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_LIST_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.list").exists())
                .andExpect(jsonPath("data.pageNum").exists())
                .andExpect(jsonPath("data.amount").exists())
                .andExpect(jsonPath("data.totalPagesCount").exists())
                .andExpect(jsonPath("data.prevPageUrl").exists())
                .andExpect(jsonPath("data.nextPageUrl").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_LIST_SUCCESS.getMessage()))
                .andDo(document(
                        "get-theme-review-list-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("조회할 페이지 기입 +\n" +
                                        "1 보다 작은 페이지는 조회할 수 없습니다."),
                                parameterWithName("amount").description("조회할 수량 기입 +\n" +
                                        "한 번에 조회 가능한 수량은 1~200 개 입니다. +\n" +
                                        "해당 수량은 추후 변경 될 수 있습니다."),
                                parameterWithName("sortCondition").description("조회 시 정렬 조건 기입 +\n" +
                                        ReviewSortCondition.getNameList())
                        ),
                        relaxedResponseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.list").description("조회된 리뷰 목록에 대한 실제 응답 Data +\n" +
                                        "간단 리뷰, 상세 리뷰, 상세 및 추가 설문 작성 리뷰가 모두 응답됨 -> 각 응답 형태는 리뷰 1건 조회 리소스를 통해 참조"),
                                fieldWithPath("data.pageNum").description("현재 요청한 페이지 번호"),
                                fieldWithPath("data.amount").description("현재 요청한 수량"),
                                fieldWithPath("data.totalPagesCount").description("요청 시 입력한 pageNum, amount, sortCondition 에 의해 조회된 결과의 총 페이지 수"),
                                fieldWithPath("data.prevPageUrl").description("현재 페이지 기준 이전 페이지에 대한 요청 URL +\n" +
                                        "이전 페이지가 실제로 존재할 수 없는 페이지 일 경우 [null] 값이 나옴"),
                                fieldWithPath("data.nextPageUrl").description("현재 페이지 기준 다음 페이지에 대한 요청 URL +\n" +
                                        "총 페이지 수 보다 다음 페이지가 커서 실제 존재할 수 없는 페이지 일 경우 [null] 값이 나옴"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰 목록 조회 - 페이지 번호를 1보다 작게 기입한 경우")
    public void getReviewList_PageNum_LT_1() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createTheme();


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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰 목록 조회 - 페이징 수량을 1보다 작게 기입한 경우")
    public void getReviewList_Amount_LT_1() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createTheme();


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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰 목록 조회 시 - 페이징 수량을 200보다 크게 기입한 경우")
    public void getReviewList_Amount_GT_200() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        memberSocialSignUpRequestDto.setEmail("member1@emailcom");
        memberSocialSignUpRequestDto.setNickname("member1");
        memberSocialSignUpRequestDto.setSocialId("3311022333");

        Long member1Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());


        Theme theme = createTheme();


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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_LIST_NOT_VALID.getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰 목록 조회 - 인증되지 않은 회원이 리뷰 목록 조회")
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

        Theme theme = createTheme();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, member1Id);

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
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 41, 19))
                .hintUsageCount(3)
                .rating(6)
                .likeCount(312)
                .build();

        member1SimpleReview1.addPlayTogether(friends.get(0));

        Review member2SimpleReview1 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 51, 11))
                .hintUsageCount(5)
                .rating(4)
                .likeCount(411)
                .build();

        member2SimpleReview1.addPlayTogether(friends.get(2));

        Review member3SimpleReview1 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 41, 19))
                .hintUsageCount(3)
                .rating(6)
                .likeCount(214)
                .build();

        member3SimpleReview1.addPlayTogether(friends.get(1));

//
        Review member1SimpleReview2 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 46, 29))
                .hintUsageCount(3)
                .rating(6)
                .likeCount(192)
                .build();

        member1SimpleReview2.addPlayTogether(friends.get(0));

        Review member2SimpleReview2 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 59, 12))
                .hintUsageCount(5)
                .rating(4)
                .likeCount(442)
                .build();

        member2SimpleReview2.addPlayTogether(friends.get(2));

        Review member3SimpleReview2 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.SIMPLE)
                .recodeNumber(1)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 51, 29))
                .hintUsageCount(3)
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
                .hintUsageCount(5)
                .rating(8)
                .comment("테마가 너무 어렵네요 다음에는 꼭 성공하고 싶어요~")
                .likeCount(1027)
                .build();

        member1DetailReview1.addPlayTogether(friends.get(0));
        member1DetailReview1.addPlayTogether(friends.get(1));
        member1DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member1DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member2DetailReview1 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(2)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 38, 23))
                .hintUsageCount(0)
                .rating(6)
                .comment("어렵다는 평이 있어서 걱정했는데 생각보다 시시해서 아쉬웠어요. 테마 자체는 재밌습니다 :)")
                .likeCount(682)
                .build();

        member2DetailReview1.addPlayTogether(friends.get(3));
        member2DetailReview1.addPlayTogether(friends.get(4));
        member2DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member2DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member3DetailReview1 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(2)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 58, 11))
                .hintUsageCount(4)
                .rating(9)
                .comment("어렵긴 하지만 운이 좋아서 간신히 성공했네요 ㅎㅎ. 너무 재밌었습니다.")
                .likeCount(721)
                .build();

        member3DetailReview1.addPlayTogether(friends.get(0));
        member3DetailReview1.addPlayTogether(friends.get(1));
        member3DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member3DetailReview1.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member1DetailReview2 = Review.builder()
                .member(member1)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 47, 34))
                .hintUsageCount(0)
                .rating(6)
                .comment("스토리가 풍부하고, 재밌었어요")
                .likeCount(556)
                .build();

        member1DetailReview2.addPlayTogether(friends.get(0));
        member1DetailReview2.addPlayTogether(friends.get(1));
        member1DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member1DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member2DetailReview2 = Review.builder()
                .member(member2)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 34, 11))
                .hintUsageCount(0)
                .rating(4)
                .comment("너무 시시했어요. 조금 더 어려운 난이도를 바랍니다.")
                .likeCount(10)
                .build();

        member2DetailReview2.addPlayTogether(friends.get(0));
        member2DetailReview2.addPlayTogether(friends.get(1));
        member2DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member2DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

        Review member3DetailReview2 = Review.builder()
                .member(member3)
                .theme(theme)
                .reviewType(ReviewType.DETAIL)
                .recodeNumber(3)
                .clearYN(false)
                .clearTime(LocalTime.of(0, 59, 11))
                .hintUsageCount(3)
                .rating(6)
                .comment("생각보다 어려워서 힘들었어요.")
                .likeCount(45)
                .build();

        member3DetailReview2.addPlayTogether(friends.get(0));
        member3DetailReview2.addPlayTogether(friends.get(1));
        member3DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile1.getId(), storedFile1.getFileName()));
        member3DetailReview2.addReviewImage(new ReviewImage(null, null, storedFile2.getId(), storedFile2.getFileName()));

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

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(createGenreCodes());
        reviewService.addSurveyToReview(member3SimpleReview1.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewService.addSurveyToReview(member2SimpleReview2.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewService.addSurveyToReview(member1DetailReview1.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewService.addSurveyToReview(member3DetailReview1.getId(), reviewSurveyCreateRequestDto.toServiceDto());
        reviewService.addSurveyToReview(member3DetailReview2.getId(), reviewSurveyCreateRequestDto.toServiceDto());
    }
}
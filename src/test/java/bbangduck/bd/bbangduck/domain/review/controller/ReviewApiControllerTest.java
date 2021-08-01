package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.service.KakaoSignInService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static bbangduck.bd.bbangduck.api.document.utils.DocUrl.*;
import static bbangduck.bd.bbangduck.api.document.utils.DocumentLinkGenerator.generateLinkCode;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewApiControllerTest extends BaseJGMApiControllerTest {

    @MockBean
    KakaoSignInService kakaoSignInService;

    @Test
    @DisplayName("간단 리뷰 조회 - 다른 회원이 생성한 간단 리뷰 조회")
    public void getSimpleReview_DifferentMemberReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("reviewId").exists())
                .andExpect(jsonPath("writerInfo").exists())
                .andExpect(jsonPath("writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("reviewType").value(ReviewType.BASE.name()))
                .andExpect(jsonPath("reviewRecodeNumber").value(1))
                .andExpect(jsonPath("themeClearYN").value(simpleReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("themeClearTime").value(simpleReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("hintUsageCount").value(simpleReviewCreateRequestDto.getHintUsageCount().name()))
                .andExpect(jsonPath("rating").value(simpleReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("playTogetherFriends").exists())
                .andExpect(jsonPath("likeCount").value(1))
                .andExpect(jsonPath("myReview").value(false))
                .andExpect(jsonPath("like").value(true))
                .andExpect(jsonPath("possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("surveyYN").value(false))
                .andExpect(jsonPath("registerTimes").exists())
                .andExpect(jsonPath("updateTimes").exists())
                .andDo(document(
                        "get-simple-review-of-different-member-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("reviewType").description("조회된 리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("updateTimes").description("조회된 리뷰의 마지막 수정 일자")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰 조회 - 삭제된 리뷰를 조회하는 경우")
    public void getReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        reviewApplicationService.deleteReview(signUpId, createdReviewId);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getMessage()));

    }

    @Test
    @DisplayName("간단 리뷰 조회 - 설문이 등록된 경우")
    public void getSimpleReview_AddSurveyTrue() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
        reviewApplicationService.addSurveyToReview(createdReviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-simple-and-survey-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("reviewType").description("조회된 리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("perceivedThemeGenres").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르 목록 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("조회된 리뷰에 등록된 설문에 등록된 체감 난이도"),
                                fieldWithPath("perceivedHorrorGrade").description("조회된 리뷰에 등록된 설문에 등록된 체감 공포도"),
                                fieldWithPath("perceivedActivity").description("조회된 리뷰에 등록된 설문에 등록된 체감 활동성"),
                                fieldWithPath("scenarioSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 시나리오 만족도"),
                                fieldWithPath("interiorSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 인테리어 만족도"),
                                fieldWithPath("problemConfigurationSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 문제 구성 만족도"),
                                fieldWithPath("registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("updateTimes").description("조회된 리뷰의 마지막 수정 일자")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("상세 리뷰 조회 - 다른 회원이 생성한 리뷰 조회")
    public void getDetailReview_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        reviewApplicationService.addDetailToReview(createdReviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("reviewId").exists())
                .andExpect(jsonPath("writerInfo").exists())
                .andExpect(jsonPath("writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("reviewType").value(ReviewType.DETAIL.name()))
                .andExpect(jsonPath("reviewRecodeNumber").value(1))
                .andExpect(jsonPath("themeClearYN").value(detailReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("themeClearTime").value(detailReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("hintUsageCount").value(detailReviewCreateRequestDto.getHintUsageCount().name()))
                .andExpect(jsonPath("rating").value(detailReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("playTogetherFriends").exists())
                .andExpect(jsonPath("reviewImages").exists())
                .andExpect(jsonPath("comment").exists())
                .andExpect(jsonPath("likeCount").value(1))
                .andExpect(jsonPath("myReview").value(false))
                .andExpect(jsonPath("like").value(true))
                .andExpect(jsonPath("possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("surveyYN").value(false))
                .andExpect(jsonPath("registerTimes").exists())
                .andExpect(jsonPath("updateTimes").exists())
                .andDo(document(
                        "get-detail-review-of-different-member-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("reviewType").description("조회된 리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("reviewImages[0].reviewImageId").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 식별 ID"),
                                fieldWithPath("reviewImages[0].reviewImageUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 Download Url"),
                                fieldWithPath("reviewImages[0].reviewImageThumbnailUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("comment").description("조회된 리뷰에 등록된 상세 코멘트"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("updateTimes").description("조회된 리뷰의 마지막 수정 일자"))
                ));

    }

    @Test
    @DisplayName("상세 리뷰 조회 - 설문이 등록된 경우")
    public void getDetailReview_AddSurveyTrue() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        reviewApplicationService.addDetailToReview(createdReviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
        reviewApplicationService.addSurveyToReview(createdReviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "get-detail-and-survey-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("reviewType").description("조회된 리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("reviewImages[0].reviewImageId").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 식별 ID"),
                                fieldWithPath("reviewImages[0].reviewImageUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 Download Url"),
                                fieldWithPath("reviewImages[0].reviewImageThumbnailUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("comment").description("조회된 리뷰에 등록된 상세 코멘트"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("perceivedThemeGenres").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르 목록 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("조회된 리뷰에 등록된 설문에 등록된 체감 난이도"),
                                fieldWithPath("perceivedHorrorGrade").description("조회된 리뷰에 등록된 설문에 등록된 체감 공포도"),
                                fieldWithPath("perceivedActivity").description("조회된 리뷰에 등록된 설문에 등록된 체감 활동성"),
                                fieldWithPath("scenarioSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 시나리오 만족도"),
                                fieldWithPath("interiorSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 인테리어 만족도"),
                                fieldWithPath("problemConfigurationSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 문제 구성 만족도"),
                                fieldWithPath("registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("updateTimes").description("조회된 리뷰의 마지막 수정 일자"))
                ));

    }

    @Test
    @DisplayName("리뷰 조회 - 인증되지 않은 회원이 리뷰 조회")
    public void getReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + createdReviewId)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("리뷰 조회 - 리뷰를 찾을 수 없는 경우")
    public void getReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName()));

        Theme theme = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto deepReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), deepReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        //when
        System.out.println("================================================================================================================================================");
        ResultActions perform = mockMvc.perform(
                get("/api/reviews/" + 1000000L)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());
        System.out.println("================================================================================================================================================");

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()))
        ;

    }

    @Test
    @DisplayName("리뷰에 설문 등록")
    public void addSurveyToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "add-survey-to-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("perceivedThemeGenres").description("리뷰에 추가할 설문에 등록할 체감 테마 장르 기입 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("리뷰에 추가할 설문에 등록할 체감 난이도 기입 +\n" +
                                        generateLinkCode(DIFFICULTY)),
                                fieldWithPath("perceivedHorrorGrade").description("리뷰에 추가할 설문에 등록할 체감 공포도 기입 +\n" +
                                        generateLinkCode(HORROR_GRADE)),
                                fieldWithPath("perceivedActivity").description("리뷰에 추가할 설문에 등록할 체감 활동성 기입 +\n" +
                                        generateLinkCode(ACTIVITY)),
                                fieldWithPath("scenarioSatisfaction").description("리뷰에 추가할 설문에 등록할 시나리오 만족도 기입 +\n" +
                                        generateLinkCode(SATISFACTION)),
                                fieldWithPath("interiorSatisfaction").description("리뷰에 추가할 설문에 등록할 인테리어 만족도 기입 +\n" +
                                        generateLinkCode(SATISFACTION)),
                                fieldWithPath("problemConfigurationSatisfaction").description("리뷰에 추가할 설문에 등록할 문제 구성 만족도 기입 +\n" +
                                        generateLinkCode(SATISFACTION))
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("리뷰의 식별 ID"),
                                fieldWithPath("writerInfo").description("리뷰를 생성한 회원 정보"),
                                fieldWithPath("writerInfo.memberId").description("리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("리뷰를 생성한 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("리뷰를 생성한 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo").description("리뷰가 생성된 테마의 정보"),
                                fieldWithPath("themeInfo.themeId").description("리뷰가 생성된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("리뷰가 생성된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("리뷰가 생성된 테마의 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("리뷰가 생성된 테마의 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("reviewType").description("리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("생성된 리뷰의 기록 번호"),
                                fieldWithPath("themeClearYN").description("테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("테마를 클리어하는데 걸린 시간"),
                                fieldWithPath("hintUsageCount").description("힌트 사용 개수"),
                                fieldWithPath("rating").description("테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends").description("리뷰에 함께 플레이한 친구로 등록한 친구들의 회원 정보"),
                                fieldWithPath("playTogetherFriends[].memberId").description("리뷰에 함께 플레이한 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[].nickname").description("리뷰에 함께 플레이한 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[].profileImageUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("playTogetherFriends[].profileImageThumbnailUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("likeCount").description("리뷰가 좋아요 받은 개수"),
                                fieldWithPath("myReview").description("내가 생성한 리뷰인지 여부"),
                                fieldWithPath("like").description("좋아요를 등록한 리뷰인지 여부"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("리뷰에 설문 추가가 가능한지 여부"),
                                fieldWithPath("surveyYN").description("리뷰에 설문이 등록되었는지 여부"),
                                fieldWithPath("perceivedThemeGenres").description("테마에 대한 평가한 체감 장르 목록 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("체감 난이도"),
                                fieldWithPath("perceivedHorrorGrade").description("체감 공포도"),
                                fieldWithPath("perceivedActivity").description("체감 활동성"),
                                fieldWithPath("scenarioSatisfaction").description("시나리오 만족도"),
                                fieldWithPath("interiorSatisfaction").description("인테리어 만족도"),
                                fieldWithPath("problemConfigurationSatisfaction").description("문제 구성 만족도"),
                                fieldWithPath("registerTimes").description("리뷰 생성 일자"),
                                fieldWithPath("updateTimes").description("리뷰 마지막 수정 일자")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 삭제된 리뷰에 설문 등록")
    public void addSurveyToReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        reviewApplicationService.deleteReview(signUpId, savedReviewId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 등록되는 체감 테마 장르의 개수가 제한된 개수보다 많을 경우")
    public void addSurveyToReview_OverPerceivedThemeGenresCount() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

//        List<String> genreCodes = createGenreCodes();
        List<Genre> perceivedThemeGenres = new ArrayList<>();
        for (int i = 0; i < reviewProperties.getPerceivedThemeGenresCountLimit() + 2; i++) {
            Genre genre = Arrays.stream(Genre.values()).findAny().get();
            perceivedThemeGenres.add(genre);
        }

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "add-survey-to-review-over-perceived-theme-genres-count",
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
    @DisplayName("리뷰에 설문 등록 - 리뷰를 찾을 수 없는 경우")
    public void addSurveyToReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + 100000L + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()));

    }

    @ParameterizedTest
    @MethodSource("provideReviewSurveyCreateRequestDtoForAddReviewSurveyValidation")
    @DisplayName("리뷰에 설문 등록 - validation 검증")
    public void addSurveyToReview_Validation(ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto) throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "add-survey-to-review-not-valid",
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

    private static Stream<Arguments> provideReviewSurveyCreateRequestDtoForAddReviewSurveyValidation() {
        List<Genre> perceivedThemeGenres = List.of(Genre.ACTION);

        return Stream.of(
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(null)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(null)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(null)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(null)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(null)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(null)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .perceivedThemeGenres(perceivedThemeGenres)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(null)
                        .build())

        );
    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 다른 회원이 생성한 리뷰에 설문을 등록하는 경우")
    public void addSurveyToReview_ReviewCreatedByOthersMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("300989218");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 인증되지 않은 회원이 리뷰 설문을 등록하는 경우")
    public void addSurveyToReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 탈퇴한 회원이 리뷰 설문을 등록한 경우")
    public void addSurveyToReview_Forbidden() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewApplicationService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + savedReviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyCreateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정")
//    public void updateSurveyFromReview() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isNoContent())
//                .andDo(document(
//                        "update-survey-from-review-success",
//                        requestHeaders(
//                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION),
//                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정")
//                        ),
//                        requestFields(
//                                fieldWithPath("genreCodes").description("수정할 설문에 등록할 장르 코드 목록 기입"),
//                                fieldWithPath("perceivedDifficulty").description("수정할 설문에 등록할 체감 난이도 기입"),
//                                fieldWithPath("perceivedHorrorGrade").description("수정할 설문에 등록할 체감 공포도 기입"),
//                                fieldWithPath("perceivedActivity").description("수정할 설문에 등록할 체감 활동성 기입"),
//                                fieldWithPath("scenarioSatisfaction").description("수정할 설문에 등록할 시나리오 만족도 기입"),
//                                fieldWithPath("interiorSatisfaction").description("수정할 설문에 등록할 체감 인테리어 만족도 기입"),
//                                fieldWithPath("problemConfigurationSatisfaction").description("수정할 설문에 등록할 문제 구성 만족도 기입")
//                        )
//                ))
//        ;
//
//    }

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 삭제될 리뷰의 설문을 수정하는 경우")
//    public void updateSurveyFromReview_DeletedReview() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        reviewService.deleteReview(reviewId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getMessage()));
//
//    }

//    @ParameterizedTest
//    @MethodSource("provideReviewSurveyUpdateRequestDtoForUpdateSurveyFromReviewValidation")
//    @DisplayName("리뷰에 등록된 설문 수정 - 기본 validation 기입 문제")
//    public void updateSurveyFromReview_NotValid(ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto) throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId,reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getStatus()))
//                .andExpect(jsonPath("data[0].objectName").exists())
//                .andExpect(jsonPath("data[0].code").exists())
//                .andExpect(jsonPath("data[0].defaultMessage").exists())
//                .andExpect(jsonPath("data[0].field").exists())
//                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getMessage()))
//                .andDo(document(
//                        "update-survey-from-review-not-valid",
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

    private static Stream<Arguments> provideReviewSurveyUpdateRequestDtoForUpdateSurveyFromReviewValidation() {
        List<String> genreCodes = List.of("RSN1");

        return Stream.of(
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(null)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(null)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(null)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(null)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(null)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(null)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyUpdateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(null)
                        .build())

        );
    }

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 장르 코드를 5개보다 많이 기입한 경우")
//    public void updateSurveyFromReview_OverPerceivedThemeGenresCount() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId,signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = new ArrayList<>();
//        int perceivedThemeGenresCountLimit = reviewProperties.getPerceivedThemeGenresCountLimit();
//        for (int i = 0; i < perceivedThemeGenresCountLimit + 2; i++) {
//            newGenreCodes.add("" + i);
//        }
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getStatus()))
//                .andExpect(jsonPath("data[0].objectName").exists())
//                .andExpect(jsonPath("data[0].code").exists())
//                .andExpect(jsonPath("data[0].defaultMessage").exists())
//                .andExpect(jsonPath("data[0].field").exists())
//                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getMessage()))
//                .andDo(document(
//                        "update-survey-from-review-over-perceived-theme-genres-count",
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

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 다른 회원이 생성한 리뷰의 설문을 수정하는 경우")
//    public void updateSurveyFromReview_ReviewCreatedByOtherMembers() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId,reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        memberSocialSignUpRequestDto.setEmail("member2@email.com");
//        memberSocialSignUpRequestDto.setNickname("member2");
//        memberSocialSignUpRequestDto.setSocialId("339217839127");
//        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        TokenDto tokenDto = authenticationService.signIn(member2Id);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getMessage()));
//
//    }

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰를 찾을 수 없는 경우")
//    public void updateSurveyFromReview_ReviewNotFound() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId,signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + 10000000L + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()));
//
//    }

//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 장르를 찾을 수 없는 경우")
//    public void updateSurveyFromReview_GenreNotFound() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId, signUpId,reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("AMGN1", "AMGN2");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//
//        //then
//        perform
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("status").value(ResponseStatus.GENRE_NOT_FOUND.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(new GenreNotFoundException("AMGN1").getMessage()));
//
//    }
//
//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 인증되지 않은 사용자가 리소스 접근")
//    public void updateSurveyFromReview_Unauthorized() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId,signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
////                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//
//        //then
//        perform
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));
//
//    }
//
//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 탈퇴한 사용자가 리소스 접근")
//    public void updateSurveyFromReview_Forbidden() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
//        reviewApplicationService.addSurveyToReview(reviewId,signUpId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        authenticationService.withdrawal(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));
//
//    }
//
//    @Test
//    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰에 설문이 등록되어 있지 않은 경우")
//    public void updateSurveyFromReview_ReviewHasNotSurvey() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        Theme theme = createThemeSample();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//
//        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
//
//        Long reviewId = reviewApplicationService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());
//
//        List<String> genreCodes = createGenreCodes();
//        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
//
////        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());
//
//        List<String> newGenreCodes = List.of("HR1", "ADVT1");
//        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_HAS_NOT_SURVEY.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_HAS_NOT_SURVEY.getMessage()));
//    }

    // TODO: 2021-06-22 리뷰 타입 목록 문서에 반영
    @Test
    @DisplayName("리뷰 수정 - 간단 리뷰 to 상세 리뷰")
    public void updateReview_BaseToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-review-base-to-detail-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("수정할 ReviewType 기입 +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("clearYN").description("수정할 클리어 여부 기입"),
                                fieldWithPath("clearTime").description("수정할 클리어 시간 기입"),
                                fieldWithPath("hintUsageCount").description("수정할 힌트 사용 개수 기입 +\n" +
                                        generateLinkCode(REVIEW_HINT_USAGE_COUNT)),
                                fieldWithPath("rating").description("수정할 테마에 대한 평점 기입 +\n" +
                                        "테마에 대한 평점은 1~5 점 사이의 점수만 기입이 가능합니다."),
                                fieldWithPath("friendIds").description("수정 시 리뷰에 등록할 친구 ID 목록 기입"),
                                fieldWithPath("reviewImages[0].fileStorageId").description("수정 시 리뷰에 등록할 이미지 목록의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("수정 시 리뷰에 등록할 이미지 목록의 파일 이름 기입"),
                                fieldWithPath("comment").description("수정할 코멘트 기입")
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰 수정 - 코멘트가 2000자를 넘긴 경우")
    public void updateReview_CommentOverLength() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);
        String macroComment = createMacroComment();
        detailReviewUpdateRequestDto.setComment(macroComment);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("리뷰 수정 - 리뷰를 찾을 수 없는 경우")
    public void updateReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + 10000000L)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("리뷰 수정 - 상세 리뷰 to 간단 리뷰")
    public void updateReview_DetailToBase() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewApplicationService.addDetailToReview(createdReviewId, signUpId, reviewDetailCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        ReviewUpdateRequestDto simpleReviewUpdateRequestDto = createSimpleReviewUpdateRequestDto(newFriendIds);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(simpleReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "update-review-detail-to-base-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("수정할 ReviewType 기입 +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("clearYN").description("수정할 클리어 여부 기입"),
                                fieldWithPath("clearTime").description("수정할 클리어 시간 기입"),
                                fieldWithPath("hintUsageCount").description("수정할 힌트 사용 개수 기입 +\n" +
                                        generateLinkCode(REVIEW_HINT_USAGE_COUNT)),
                                fieldWithPath("rating").description("수정할 테마에 대한 평점 기입 +\n" +
                                        "테마에 대한 평점은 1~5 점 사이의 점수만 기입이 가능합니다."),
                                fieldWithPath("friendIds").description("수정 시 리뷰에 등록할 친구 ID 목록 기입"),
                                fieldWithPath("reviewImages").description("[null]"),
                                fieldWithPath("comment").description("[null]")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("리뷰 수정 - 리뷰에 등록하는 친구가 5명 이상일 경우")
    public void updateReview_OverPlayTogether() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        friendIds.add(10000L);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(friendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_REVIEW_NOT_VALID.getMessage()))
        ;

    }


    @Test
    @DisplayName("리뷰 수정 - 리뷰에 등록하는 친구가 친구 관계가 아닐 경우")
    public void updateReview_FriendIsNotFriend() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

//        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<Long> newFriendIds = new ArrayList<>();
        newFriendIds.add(friendId1);
        newFriendIds.add(friendId3);

        Member requestStateFriendToMember = createOneWayFollowMember(memberSocialSignUpRequestDto, signUpId);
        newFriendIds.add(requestStateFriendToMember.getId());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.NOT_TWO_WAY_FOLLOW_RELATION.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(Matchers.containsString(ResponseStatus.NOT_TWO_WAY_FOLLOW_RELATION.getMessage())));

    }

    @Test
    @DisplayName("리뷰 수정 - 다른 회원이 생성한 리뷰를 수정하는 경우")
    public void updateReview_ReviewCreatedByOtherMembers() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("1321097897");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_CREATED_BY_OTHER_MEMBERS.getMessage()));

    }

    @Test
    @DisplayName("리뷰 수정 - 삭제된 리뷰일 경우 수정 불가 ")
    public void updateReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        reviewApplicationService.deleteReview(signUpId, createdReviewId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getMessage()));

    }

    @Test
    @DisplayName("리뷰 수정 - 인증되지 않은 경우")
    public void updateReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("리뷰 수정 - 탈퇴한 회원이 리소스 접근")
    public void updateReview_Forbidden() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationApplicationService.withdrawal(signUpId, signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + createdReviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailReviewUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 리뷰 상세 추가")
    public void addDetailToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSocialSignUpRequestDto, signUpId);
        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);
        Long reviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + reviewId + "/details")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailCreateRequestDto))
        );

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "add-detail-to-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 적용")
                        ),
                        requestFields(
                                fieldWithPath("reviewImages[0].fileStorageId").description("리뷰 상세에 추가할 이미지 파일의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("리뷰 상세에 추가할 이미지 파일의 파일 이름 기입"),
                                fieldWithPath("comment").description("리뷰 상세에 추가할 코멘트 기입 +\n" +
                                        "최대 2000자 제한")
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("리뷰의 식별 ID"),
                                fieldWithPath("writerInfo").description("리뷰를 생성한 회원 정보"),
                                fieldWithPath("writerInfo.memberId").description("리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("리뷰를 생성한 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("리뷰를 생성한 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo").description("리뷰가 생성된 테마의 정보"),
                                fieldWithPath("themeInfo.themeId").description("리뷰가 생성된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("리뷰가 생성된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("리뷰가 생성된 테마의 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("리뷰가 생성된 테마의 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("reviewType").description("리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("생성된 리뷰의 기록 번호"),
                                fieldWithPath("themeClearYN").description("테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("테마를 클리어하는데 걸린 시간"),
                                fieldWithPath("hintUsageCount").description("힌트 사용 개수"),
                                fieldWithPath("rating").description("테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends").description("리뷰에 함께 플레이한 친구로 등록한 친구들의 회원 정보"),
                                fieldWithPath("playTogetherFriends[].memberId").description("리뷰에 함께 플레이한 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[].nickname").description("리뷰에 함께 플레이한 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[].profileImageUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("playTogetherFriends[].profileImageThumbnailUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("likeCount").description("리뷰가 좋아요 받은 개수"),
                                fieldWithPath("myReview").description("내가 생성한 리뷰인지 여부"),
                                fieldWithPath("like").description("좋아요를 등록한 리뷰인지 여부"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("리뷰에 설문 추가가 가능한지 여부"),
                                fieldWithPath("surveyYN").description("리뷰에 설문이 등록되었는지 여부"),
                                fieldWithPath("reviewImages").description("상세 리뷰에 등록된 이미지 정보"),
                                fieldWithPath("reviewImages[].reviewImageId").description("상세 리뷰에 등록된 이미지의 식별 ID"),
                                fieldWithPath("reviewImages[].reviewImageUrl").description("상세 리뷰에 등록된 이미지의 다운로드 URL"),
                                fieldWithPath("reviewImages[].reviewImageThumbnailUrl").description("상세 리뷰에 등록된 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("comment").description("상세 리뷰에 등록된 코멘트"),
                                fieldWithPath("registerTimes").description("리뷰 생성 일자"),
                                fieldWithPath("updateTimes").description("리뷰 마지막 수정 일자")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("리뷰에 리뷰 상세 및 설문 추가")
    public void addDetailAndSurveyToReview() throws Exception {
        //given
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createTwoWayFollowMembers(memberSignUpRequestDto, signUpId);

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();

        ReviewDetailAndSurveyCreateDtoRequestDto reviewDetailAndSurveyCreateDtoRequestDto = createReviewDetailAndSurveyCreateDtoRequestDto(reviewImageRequestDtos, perceivedThemeGenres);

        TokenDto tokenDto = authenticationService.signIn(signUpId);
        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + reviewId + "/details-and-surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDetailAndSurveyCreateDtoRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "add-detail-and-survey-to-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정")
                        ),
                        requestFields(
                                fieldWithPath("reviewImages[0].fileStorageId").description("리뷰 상세에 등록할 이미지 파일의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("리뷰 상세에 등록할 이미지 파일의 파일 이름 기입"),
                                fieldWithPath("comment").description("리뷰 상세에 등록할 코멘트 기입"),
                                fieldWithPath("perceivedThemeGenres").description("리뷰 설문에 등록할 체감 테마 장르 목록 기입 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("리뷰 설문에 등록할 체감 난이도 기입"),
                                fieldWithPath("perceivedHorrorGrade").description("리뷰 설문에 등록할 체감 공포도 기입"),
                                fieldWithPath("perceivedActivity").description("리뷰 설문에 등록할 체감 활동성 기입"),
                                fieldWithPath("scenarioSatisfaction").description("리뷰 설문에 등록할 시나리오 만족도 기입"),
                                fieldWithPath("interiorSatisfaction").description("리뷰 설문에 등록할 인테리어 만족도 기입"),
                                fieldWithPath("problemConfigurationSatisfaction").description("리뷰 설문에 등록할 문제 구성 만족도 기입")
                        ),
                        responseFields(
                                fieldWithPath("reviewId").description("리뷰의 식별 ID"),
                                fieldWithPath("writerInfo").description("리뷰를 생성한 회원 정보"),
                                fieldWithPath("writerInfo.memberId").description("리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("writerInfo.nickname").description("리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("writerInfo.profileImageUrl").description("리뷰를 생성한 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("writerInfo.profileImageThumbnailUrl").description("리뷰를 생성한 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo").description("리뷰가 생성된 테마의 정보"),
                                fieldWithPath("themeInfo.themeId").description("리뷰가 생성된 테마의 식별 ID"),
                                fieldWithPath("themeInfo.themeName").description("리뷰가 생성된 테마의 이름"),
                                fieldWithPath("themeInfo.themeImageUrl").description("리뷰가 생성된 테마의 이미지 다운로드 URL"),
                                fieldWithPath("themeInfo.themeImageThumbnailUrl").description("리뷰가 생성된 테마의 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("reviewType").description("리뷰의 Type +\n" +
                                        generateLinkCode(REVIEW_TYPE)),
                                fieldWithPath("reviewRecodeNumber").description("생성된 리뷰의 기록 번호"),
                                fieldWithPath("themeClearYN").description("테마 클리어 여부"),
                                fieldWithPath("themeClearTime").description("테마를 클리어하는데 걸린 시간"),
                                fieldWithPath("hintUsageCount").description("힌트 사용 개수"),
                                fieldWithPath("rating").description("테마에 대한 평점"),
                                fieldWithPath("playTogetherFriends").description("리뷰에 함께 플레이한 친구로 등록한 친구들의 회원 정보"),
                                fieldWithPath("playTogetherFriends[].memberId").description("리뷰에 함께 플레이한 친구 회원의 식별 ID"),
                                fieldWithPath("playTogetherFriends[].nickname").description("리뷰에 함께 플레이한 친구 회원의 닉네임"),
                                fieldWithPath("playTogetherFriends[].profileImageUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 다운로드 URL"),
                                fieldWithPath("playTogetherFriends[].profileImageThumbnailUrl").description("리뷰에 함께 플레이한 친구 회원의 프로필 이미지 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("likeCount").description("리뷰가 좋아요 받은 개수"),
                                fieldWithPath("myReview").description("내가 생성한 리뷰인지 여부"),
                                fieldWithPath("like").description("좋아요를 등록한 리뷰인지 여부"),
                                fieldWithPath("possibleRegisterForSurveyYN").description("리뷰에 설문 추가가 가능한지 여부"),
                                fieldWithPath("surveyYN").description("리뷰에 설문이 등록되었는지 여부"),
                                fieldWithPath("reviewImages").description("상세 리뷰에 등록된 이미지 정보"),
                                fieldWithPath("reviewImages[].reviewImageId").description("상세 리뷰에 등록된 이미지의 식별 ID"),
                                fieldWithPath("reviewImages[].reviewImageUrl").description("상세 리뷰에 등록된 이미지의 다운로드 URL"),
                                fieldWithPath("reviewImages[].reviewImageThumbnailUrl").description("상세 리뷰에 등록된 이미지의 썸네일 이미지 다운로드 URL"),
                                fieldWithPath("comment").description("상세 리뷰에 등록된 코멘트"),
                                fieldWithPath("perceivedThemeGenres").description("테마에 대한 평가한 체감 장르 목록 +\n" +
                                        generateLinkCode(GENRE)),
                                fieldWithPath("perceivedDifficulty").description("체감 난이도"),
                                fieldWithPath("perceivedHorrorGrade").description("체감 공포도"),
                                fieldWithPath("perceivedActivity").description("체감 활동성"),
                                fieldWithPath("scenarioSatisfaction").description("시나리오 만족도"),
                                fieldWithPath("interiorSatisfaction").description("인테리어 만족도"),
                                fieldWithPath("problemConfigurationSatisfaction").description("문제 구성 만족도"),
                                fieldWithPath("registerTimes").description("리뷰 생성 일자"),
                                fieldWithPath("updateTimes").description("리뷰 마지막 수정 일자")
                        )

                ))
        ;

    }

    @Test
    @DisplayName("리뷰 삭제")
    public void deleteReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long reviewId = reviewApplicationService.createReview(signUpId, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/reviews/" + reviewId)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "delete-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰에 좋아요 등록")
    public void addLikeToReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("382109321789");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long reviewId = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                post("/api/reviews/" + reviewId + "/likes")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isCreated())
                .andDo(document(
                        "add-like-to-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        )
                ))
        ;

    }

    @Test
    @DisplayName("리뷰에 등록된 좋아요 제거")
    public void removeLikeFromReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long member1Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        memberSignUpRequestDto.setEmail("member2@email.com");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("382109321789");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);
        Long reviewId = reviewApplicationService.createReview(member1Id, themeSample.getId(), reviewCreateRequestDto.toServiceDto());

        reviewApplicationService.addLikeToReview(member2Id, reviewId);

        boolean existsReviewLike = reviewLikeService.isMemberLikeToReview(member2Id, reviewId);
        assertTrue(existsReviewLike, "리뷰에 좋아요가 등록되어 있어야 한다.");

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                delete("/api/reviews/" + reviewId + "/likes")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andDo(document(
                        "remove-like-from-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        )
                ))
        ;

    }
}
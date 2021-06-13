package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.Satisfaction;
import bbangduck.bd.bbangduck.domain.review.controller.dto.*;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewApiControllerTest extends BaseJGMApiControllerTest {

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.reviewId").exists())
                .andExpect(jsonPath("data.writerInfo").exists())
                .andExpect(jsonPath("data.writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("data.writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("data.themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("data.reviewType").value(ReviewType.BASE.name()))
                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
                .andExpect(jsonPath("data.themeClearYN").value(simpleReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("data.themeClearTime").value(simpleReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("data.hintUsageCount").value(simpleReviewCreateRequestDto.getHintUsageCount()))
                .andExpect(jsonPath("data.rating").value(simpleReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("data.playTogetherFriends").exists())
                .andExpect(jsonPath("data.likeCount").value(1))
                .andExpect(jsonPath("data.myReview").value(false))
                .andExpect(jsonPath("data.like").value(true))
                .andExpect(jsonPath("data.possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("data.surveyYN").value(false))
                .andExpect(jsonPath("data.registerTimes").exists())
                .andExpect(jsonPath("data.updateTimes").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "get-simple-review-of-different-member-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("data.writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("data.writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("data.writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("data.writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("data.themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("data.themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.reviewType").description("조회된 리뷰의 Type +\n" +
                                        ReviewType.getNameList()),
                                fieldWithPath("data.reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("data.themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("data.themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("data.hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("data.rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("data.playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("data.playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("data.myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("data.like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("data.possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("data.surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("data.registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("data.updateTimes").description("조회된 리뷰의 마지막 수정 일자"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

        TokenDto tokenDto = authenticationService.signIn(signUpId2);

        reviewService.deleteReview(createdReviewId);

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        reviewService.addSurveyToReview(createdReviewId, reviewSurveyCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.reviewId").exists())
                .andExpect(jsonPath("data.writerInfo").exists())
                .andExpect(jsonPath("data.writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("data.writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("data.themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("data.reviewType").value(ReviewType.BASE.name()))
                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
                .andExpect(jsonPath("data.themeClearYN").value(simpleReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("data.themeClearTime").value(simpleReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("data.hintUsageCount").value(simpleReviewCreateRequestDto.getHintUsageCount()))
                .andExpect(jsonPath("data.rating").value(simpleReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("data.playTogetherFriends").exists())
                .andExpect(jsonPath("data.likeCount").value(1))
                .andExpect(jsonPath("data.myReview").value(false))
                .andExpect(jsonPath("data.like").value(true))
                .andExpect(jsonPath("data.possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("data.surveyYN").value(true))
                .andExpect(jsonPath("data.registerTimes").exists())
                .andExpect(jsonPath("data.updateTimes").exists())
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreId").exists())
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreCode").exists())
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreName").exists())
                .andExpect(jsonPath("data.perceivedDifficulty").value(reviewSurveyCreateRequestDto.getPerceivedDifficulty().name()))
                .andExpect(jsonPath("data.perceivedHorrorGrade").value(reviewSurveyCreateRequestDto.getPerceivedHorrorGrade().name()))
                .andExpect(jsonPath("data.perceivedActivity").value(reviewSurveyCreateRequestDto.getPerceivedActivity().name()))
                .andExpect(jsonPath("data.scenarioSatisfaction").value(reviewSurveyCreateRequestDto.getScenarioSatisfaction().name()))
                .andExpect(jsonPath("data.interiorSatisfaction").value(reviewSurveyCreateRequestDto.getInteriorSatisfaction().name()))
                .andExpect(jsonPath("data.problemConfigurationSatisfaction").value(reviewSurveyCreateRequestDto.getProblemConfigurationSatisfaction().name()))
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "get-simple-and-survey-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("data.writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("data.writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("data.writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("data.writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("data.themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("data.themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.reviewType").description("조회된 리뷰의 Type +\n" +
                                        ReviewType.getNameList()),
                                fieldWithPath("data.reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("data.themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("data.themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("data.hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("data.rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("data.playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("data.playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("data.myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("data.like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("data.possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("data.surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreId").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 식별 ID"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreCode").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 코드"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreName").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 이름"),
                                fieldWithPath("data.perceivedDifficulty").description("조회된 리뷰에 등록된 설문에 등록된 체감 난이도"),
                                fieldWithPath("data.perceivedHorrorGrade").description("조회된 리뷰에 등록된 설문에 등록된 체감 공포도"),
                                fieldWithPath("data.perceivedActivity").description("조회된 리뷰에 등록된 설문에 등록된 체감 활동성"),
                                fieldWithPath("data.scenarioSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 시나리오 만족도"),
                                fieldWithPath("data.interiorSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 인테리어 만족도"),
                                fieldWithPath("data.problemConfigurationSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 문제 구성 만족도"),
                                fieldWithPath("data.registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("data.updateTimes").description("조회된 리뷰의 마지막 수정 일자"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        reviewService.addDetailToReview(createdReviewId, reviewDetailCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.reviewId").exists())
                .andExpect(jsonPath("data.writerInfo").exists())
                .andExpect(jsonPath("data.writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("data.writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("data.themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("data.reviewType").value(ReviewType.DETAIL.name()))
                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
                .andExpect(jsonPath("data.themeClearYN").value(detailReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("data.themeClearTime").value(detailReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("data.hintUsageCount").value(detailReviewCreateRequestDto.getHintUsageCount()))
                .andExpect(jsonPath("data.rating").value(detailReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("data.playTogetherFriends").exists())
                .andExpect(jsonPath("data.reviewImages").exists())
                .andExpect(jsonPath("data.comment").exists())
                .andExpect(jsonPath("data.likeCount").value(1))
                .andExpect(jsonPath("data.myReview").value(false))
                .andExpect(jsonPath("data.like").value(true))
                .andExpect(jsonPath("data.possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("data.surveyYN").value(false))
                .andExpect(jsonPath("data.registerTimes").exists())
                .andExpect(jsonPath("data.updateTimes").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "get-detail-review-of-different-member-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("data.writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("data.writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("data.writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("data.writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("data.themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("data.themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.reviewType").description("조회된 리뷰의 Type +\n" +
                                        ReviewType.getNameList()),
                                fieldWithPath("data.reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("data.themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("data.themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("data.hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("data.rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("data.playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("data.playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("data.myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("data.like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("data.reviewImages[0].reviewImageId").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 식별 ID"),
                                fieldWithPath("data.reviewImages[0].reviewImageUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 Download Url"),
                                fieldWithPath("data.reviewImages[0].reviewImageThumbnailUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.comment").description("조회된 리뷰에 등록된 상세 코멘트"),
                                fieldWithPath("data.possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("data.surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("data.registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("data.updateTimes").description("조회된 리뷰의 마지막 수정 일자"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION))
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);

        reviewService.addDetailToReview(createdReviewId, reviewDetailCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);
        reviewService.addSurveyToReview(createdReviewId, reviewSurveyCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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
                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data.reviewId").exists())
                .andExpect(jsonPath("data.writerInfo").exists())
                .andExpect(jsonPath("data.writerInfo.memberId").value(signUpId))
                .andExpect(jsonPath("data.writerInfo.profileImageUrl").exists())
                .andExpect(jsonPath("data.themeInfo.themeId").value(theme.getId()))
                .andExpect(jsonPath("data.reviewType").value(ReviewType.DETAIL.name()))
                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
                .andExpect(jsonPath("data.themeClearYN").value(detailReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("data.themeClearTime").value(detailReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("data.hintUsageCount").value(detailReviewCreateRequestDto.getHintUsageCount()))
                .andExpect(jsonPath("data.rating").value(detailReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("data.playTogetherFriends").exists())
                .andExpect(jsonPath("data.reviewImages").exists())
                .andExpect(jsonPath("data.comment").exists())
                .andExpect(jsonPath("data.likeCount").value(1))
                .andExpect(jsonPath("data.myReview").value(false))
                .andExpect(jsonPath("data.like").value(true))
                .andExpect(jsonPath("data.possibleRegisterForSurveyYN").value(true))
                .andExpect(jsonPath("data.surveyYN").value(true))
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreId").exists())
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreCode").exists())
                .andExpect(jsonPath("data.perceivedThemeGenres[0].genreName").exists())
                .andExpect(jsonPath("data.perceivedDifficulty").value(reviewSurveyCreateRequestDto.getPerceivedDifficulty().name()))
                .andExpect(jsonPath("data.perceivedHorrorGrade").value(reviewSurveyCreateRequestDto.getPerceivedHorrorGrade().name()))
                .andExpect(jsonPath("data.perceivedActivity").value(reviewSurveyCreateRequestDto.getPerceivedActivity().name()))
                .andExpect(jsonPath("data.scenarioSatisfaction").value(reviewSurveyCreateRequestDto.getScenarioSatisfaction().name()))
                .andExpect(jsonPath("data.interiorSatisfaction").value(reviewSurveyCreateRequestDto.getInteriorSatisfaction().name()))
                .andExpect(jsonPath("data.problemConfigurationSatisfaction").value(reviewSurveyCreateRequestDto.getProblemConfigurationSatisfaction().name()))
                .andExpect(jsonPath("data.registerTimes").exists())
                .andExpect(jsonPath("data.updateTimes").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "get-detail-and-survey-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data.reviewId").description("조회된 리뷰의 식별 ID"),
                                fieldWithPath("data.writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
                                fieldWithPath("data.writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
                                fieldWithPath("data.writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
                                fieldWithPath("data.writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
                                fieldWithPath("data.themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
                                fieldWithPath("data.themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
                                fieldWithPath("data.themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.reviewType").description("조회된 리뷰의 Type +\n" +
                                        ReviewType.getNameList()),
                                fieldWithPath("data.reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
                                fieldWithPath("data.themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
                                fieldWithPath("data.themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
                                fieldWithPath("data.hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
                                fieldWithPath("data.rating").description("조회된 리뷰의 테마에 대한 평점"),
                                fieldWithPath("data.playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
                                fieldWithPath("data.playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
                                fieldWithPath("data.playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
                                fieldWithPath("data.myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
                                fieldWithPath("data.like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
                                fieldWithPath("data.reviewImages[0].reviewImageId").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 식별 ID"),
                                fieldWithPath("data.reviewImages[0].reviewImageUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 Download Url"),
                                fieldWithPath("data.reviewImages[0].reviewImageThumbnailUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 썸네일 이미지 파일 Download Url"),
                                fieldWithPath("data.comment").description("조회된 리뷰에 등록된 상세 코멘트"),
                                fieldWithPath("data.possibleRegisterForSurveyYN").description("조회된 리뷰에 설문이 등록 가능한지 여부 +\n" +
                                        "리뷰를 생성한 이후 일정 기간이 지나면 설문을 등록할 수 없습니다."),
                                fieldWithPath("data.surveyYN").description("조회된 리뷰에 설문이 등록되어 있는지 여부"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreId").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 식별 ID"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreCode").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 코드"),
                                fieldWithPath("data.perceivedThemeGenres[0].genreName").description("조회된 리뷰에 등록된 설문에 등록된 체감 테마 장르의 이름"),
                                fieldWithPath("data.perceivedDifficulty").description("조회된 리뷰에 등록된 설문에 등록된 체감 난이도"),
                                fieldWithPath("data.perceivedHorrorGrade").description("조회된 리뷰에 등록된 설문에 등록된 체감 공포도"),
                                fieldWithPath("data.perceivedActivity").description("조회된 리뷰에 등록된 설문에 등록된 체감 활동성"),
                                fieldWithPath("data.scenarioSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 시나리오 만족도"),
                                fieldWithPath("data.interiorSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 인테리어 만족도"),
                                fieldWithPath("data.problemConfigurationSatisfaction").description("조회된 리뷰에 등록된 설문에 등록된 문제 구성 만족도"),
                                fieldWithPath("data.registerTimes").description("조회된 리뷰의 생성 일자"),
                                fieldWithPath("data.updateTimes").description("조회된 리뷰의 마지막 수정 일자"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION))
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto deepReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), deepReviewCreateRequestDto.toServiceDto());

        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("3323311321");

        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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
                .andExpect(jsonPath("status").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.ADD_SURVEY_TO_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "add-survey-to-review-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("genreCodes").description("리뷰에 추가할 설문에 등록할 체감 테마 장르 코드 기입"),
                                fieldWithPath("perceivedDifficulty").description("리뷰에 추가할 설문에 등록할 체감 난이도 기입 +\n" +
                                        Difficulty.getNameList()),
                                fieldWithPath("perceivedHorrorGrade").description("리뷰에 추가할 설문에 등록할 체감 공포도 기입 +\n" +
                                        HorrorGrade.getNameList()),
                                fieldWithPath("perceivedActivity").description("리뷰에 추가할 설문에 등록할 체감 활동성 기입 +\n" +
                                        Activity.getNameList()),
                                fieldWithPath("scenarioSatisfaction").description("리뷰에 추가할 설문에 등록할 시나리오 만족도 기입 +\n" +
                                        Satisfaction.getNameList()),
                                fieldWithPath("interiorSatisfaction").description("리뷰에 추가할 설문에 등록할 인테리어 만족도 기입 +\n" +
                                        Satisfaction.getNameList()),
                                fieldWithPath("problemConfigurationSatisfaction").description("리뷰에 추가할 설문에 등록할 문제 구성 만족도 기입 +\n" +
                                        Satisfaction.getNameList())
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
    @DisplayName("리뷰에 설문 등록 - 삭제된 리뷰에 설문 등록")
    public void addSurveyToReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        reviewService.deleteReview(savedReviewId);

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

//        List<String> genreCodes = createGenreCodes();
        List<String> genreCodes = new ArrayList<>();
        for (int i = 0; i < reviewProperties.getPerceivedThemeGenresCountLimit() + 2; i++) {
            genreCodes.add("" + i);
        }

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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

    @Test
    @DisplayName("리뷰에 설문 등록 - 리뷰 설문에 등록할 장르를 찾을 수 없는 경우")
    public void addSurveyToReview_GenreNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = List.of("AMGN1", "AMGN2");

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.GENRE_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new GenreNotFoundException("AMGN1").getMessage()));

    }

    @ParameterizedTest
    @MethodSource("provideReviewSurveyCreateRequestDtoForAddReviewSurveyValidation")
    @DisplayName("리뷰에 설문 등록 - validation 검증")
    public void addSurveyToReview_Validation(ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto) throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

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
        List<String> genreCodes = List.of("RSN1");

        return Stream.of(
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(null)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(null)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(null)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(null)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(null)
                        .interiorSatisfaction(Satisfaction.VERY_BAD)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
                        .genreCodes(genreCodes)
                        .perceivedDifficulty(Difficulty.EASY)
                        .perceivedHorrorGrade(HorrorGrade.NORMAL)
                        .perceivedActivity(Activity.NORMAL)
                        .scenarioSatisfaction(Satisfaction.GOOD)
                        .interiorSatisfaction(null)
                        .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                        .build()),
                Arguments.of(ReviewSurveyCreateRequestDto.builder()
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

    @Test
    @DisplayName("리뷰에 설문 등록 - 다른 회원이 생성한 리뷰에 설문을 등록하는 경우")
    public void addSurveyToReview_ReviewCreatedByOthersMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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
                .andExpect(jsonPath("status").value(ResponseStatus.ADD_SURVEYS_TO_REVIEWS_CREATED_BY_OTHER_MEMBERS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.ADD_SURVEYS_TO_REVIEWS_CREATED_BY_OTHER_MEMBERS.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 설문 등록 - 인증되지 않은 회원이 리뷰 설문을 등록하는 경우")
    public void addSurveyToReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Theme theme = createThemeSample();

        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();

        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long savedReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();

        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.NORMAL)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.VERY_BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

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

    @Test
    @DisplayName("리뷰에 등록된 설문 수정")
    public void updateSurveyFromReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "update-survey-from-review-success",
                        requestHeaders(
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정")
                        ),
                        requestFields(
                                fieldWithPath("genreCodes").description("수정할 설문에 등록할 장르 코드 목록 기입"),
                                fieldWithPath("perceivedDifficulty").description("수정할 설문에 등록할 체감 난이도 기입"),
                                fieldWithPath("perceivedHorrorGrade").description("수정할 설문에 등록할 체감 공포도 기입"),
                                fieldWithPath("perceivedActivity").description("수정할 설문에 등록할 체감 활동성 기입"),
                                fieldWithPath("scenarioSatisfaction").description("수정할 설문에 등록할 시나리오 만족도 기입"),
                                fieldWithPath("interiorSatisfaction").description("수정할 설문에 등록할 체감 인테리어 만족도 기입"),
                                fieldWithPath("problemConfigurationSatisfaction").description("수정할 설문에 등록할 문제 구성 만족도 기입")
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
    @DisplayName("리뷰에 등록된 설문 수정 - 삭제될 리뷰의 설문을 수정하는 경우")
    public void updateSurveyFromReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        reviewService.deleteReview(reviewId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.MANIPULATE_DELETED_REVIEW.getMessage()));

    }

    @ParameterizedTest
    @MethodSource("provideReviewSurveyUpdateRequestDtoForUpdateSurveyFromReviewValidation")
    @DisplayName("리뷰에 등록된 설문 수정 - 기본 validation 기입 문제")
    public void updateSurveyFromReview_NotValid(ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto) throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "update-survey-from-review-not-valid",
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

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 장르 코드를 5개보다 많이 기입한 경우")
    public void updateSurveyFromReview_OverPerceivedThemeGenresCount() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = new ArrayList<>();
        int perceivedThemeGenresCountLimit = reviewProperties.getPerceivedThemeGenresCountLimit();
        for (int i = 0; i < perceivedThemeGenresCountLimit + 2; i++) {
            newGenreCodes.add("" + i);
        }
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getStatus()))
                .andExpect(jsonPath("data[0].objectName").exists())
                .andExpect(jsonPath("data[0].code").exists())
                .andExpect(jsonPath("data[0].defaultMessage").exists())
                .andExpect(jsonPath("data[0].field").exists())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_NOT_VALID.getMessage()))
                .andDo(document(
                        "update-survey-from-review-over-perceived-theme-genres-count",
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
    @DisplayName("리뷰에 등록된 설문 수정 - 다른 회원이 생성한 리뷰의 설문을 수정하는 경우")
    public void updateSurveyFromReview_ReviewCreatedByOtherMembers() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        memberSocialSignUpRequestDto.setEmail("member2@email.com");
        memberSocialSignUpRequestDto.setNickname("member2");
        memberSocialSignUpRequestDto.setSocialId("339217839127");
        Long member2Id = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        TokenDto tokenDto = authenticationService.signIn(member2Id);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_CREATED_BY_OTHER_MEMBERS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_SURVEY_FROM_REVIEW_CREATED_BY_OTHER_MEMBERS.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰를 찾을 수 없는 경우")
    public void updateSurveyFromReview_ReviewNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + 10000000L + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 장르를 찾을 수 없는 경우")
    public void updateSurveyFromReview_GenreNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("AMGN1", "AMGN2");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.GENRE_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(new GenreNotFoundException("AMGN1").getMessage()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 인증되지 않은 사용자가 리소스 접근")
    public void updateSurveyFromReview_Unauthorized() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());


        //then
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 탈퇴한 사용자가 리소스 접근")
    public void updateSurveyFromReview_Forbidden() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()));

    }

    @Test
    @DisplayName("리뷰에 등록된 설문 수정 - 리뷰에 설문이 등록되어 있지 않은 경우")
    public void updateSurveyFromReview_ReviewHasNotSurvey() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme theme = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long reviewId = reviewService.createReview(signUpId, theme.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<String> genreCodes = createGenreCodes();
        ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(genreCodes);

//        reviewService.addSurveyToReview(reviewId, reviewSurveyCreateRequestDto.toServiceDto());

        List<String> newGenreCodes = List.of("HR1", "ADVT1");
        ReviewSurveyUpdateRequestDto reviewSurveyUpdateRequestDto = createReviewSurveyUpdateRequestDto(newGenreCodes);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        //when
        ResultActions perform = mockMvc.perform(
                put("/api/reviews/" + reviewId + "/surveys")
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewSurveyUpdateRequestDto))
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_HAS_NOT_SURVEY.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_HAS_NOT_SURVEY.getMessage()));
    }

    // TODO: 2021-06-12 문서화
    @Test
    @DisplayName("리뷰 수정 - 간단 리뷰 to 상세 리뷰")
    public void updateReview_SimpleToDetail() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "update-review-simple-to-detail-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("수정할 ReviewType 기입"),
                                fieldWithPath("clearYN").description("수정할 클리어 여부 기입"),
                                fieldWithPath("clearTime").description("수정할 클리어 시간 기입"),
                                fieldWithPath("hintUsageCount").description("수정할 힌트 사용 개수 기입"),
                                fieldWithPath("rating").description("수정할 테마에 대한 평점 기입"),
                                fieldWithPath("friendIds").description("수정 시 리뷰에 등록할 친구 ID 목록 기입"),
                                fieldWithPath("reviewImages[0].fileStorageId").description("수정 시 리뷰에 등록할 이미지 목록의 파일 저장소 ID 기입"),
                                fieldWithPath("reviewImages[0].fileName").description("수정 시 리뷰에 등록할 이미지 목록의 파일 이름 기입"),
                                fieldWithPath("comment").description("수정할 코멘트 기입")
                        ),
                        responseFields(
                                fieldWithPath("status").description(STATUS_DESCRIPTION),
                                fieldWithPath("data").description("[null]"),
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

    }

    // TODO: 2021-06-13 문서 1줄 반영
    @Test
    @DisplayName("리뷰 수정 - 코멘트가 2000자를 넘긴 경우")
    public void updateReview_CommentOverLength() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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

    // TODO: 2021-06-12 문서화
    @Test
    @DisplayName("리뷰 수정 - 상세 리뷰 to 간단 리뷰")
    public void updateReview_DetailToSimple() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createReviewCreateRequestDto(friendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), detailReviewCreateRequestDto.toServiceDto());

        ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
        reviewService.addDetailToReview(createdReviewId, reviewDetailCreateRequestDto.toServiceDto());

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
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_REVIEW_SUCCESS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_REVIEW_SUCCESS.getMessage()))
                .andDo(document(
                        "update-review-detail-to-simple-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[application/json;charset=UTF-8] 지정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestFields(
                                fieldWithPath("reviewType").description("수정할 ReviewType 기입"),
                                fieldWithPath("clearYN").description("수정할 클리어 여부 기입"),
                                fieldWithPath("clearTime").description("수정할 클리어 시간 기입"),
                                fieldWithPath("hintUsageCount").description("수정할 힌트 사용 개수 기입"),
                                fieldWithPath("rating").description("수정할 테마에 대한 평점 기입"),
                                fieldWithPath("friendIds").description("수정 시 리뷰에 등록할 친구 ID 목록 기입"),
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
    @DisplayName("리뷰 수정 - 리뷰에 등록하는 친구가 5명 이상일 경우")
    public void updateReview_OverPlayTogether() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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
    @DisplayName("리뷰 수정 - 리뷰에 등록하는 친구가 실제 친구 관계가 아닐 경우")
    public void updateReview_FriendIsNotFriend() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

//        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<Long> newFriendIds = new ArrayList<>();
        newFriendIds.add(friendId1);
        newFriendIds.add(friendId3);

        Member requestStateFriendToMember = createRequestStateFriendToMember(memberSocialSignUpRequestDto, signUpId);
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
                .andExpect(jsonPath("status").value(ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(Matchers.containsString(ResponseStatus.RELATION_OF_MEMBER_AND_FRIEND_IS_NOT_FRIEND.getMessage())));

    }

    @Test
    @DisplayName("리뷰 수정 - 다른 회원이 생성한 리뷰를 수정하는 경우")
    public void updateReview_ReviewCreatedByOtherMembers() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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
                .andExpect(jsonPath("status").value(ResponseStatus.UPDATE_REVIEW_CREATED_BY_OTHER_MEMBERS.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPDATE_REVIEW_CREATED_BY_OTHER_MEMBERS.getMessage()));

    }

    @Test
    @DisplayName("리뷰 수정 - 삭제된 리뷰일 경우 수정 불가 ")
    public void updateReview_DeletedReview() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        Theme themeSample = createThemeSample();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        reviewService.deleteReview(createdReviewId);

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

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

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        Long friendId1 = friendIds.get(0);
        Long friendId2 = friendIds.get(1);
        Long friendId3 = friendIds.get(2);

        List<Long> oldFriendIds = List.of(friendId1, friendId2);
        ReviewCreateRequestDto simpleReviewCreateRequestDto = createReviewCreateRequestDto(oldFriendIds);

        Long createdReviewId = reviewService.createReview(signUpId, themeSample.getId(), simpleReviewCreateRequestDto.toServiceDto());

        List<Long> newFriendIds = List.of(friendId1, friendId3);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewUpdateRequestDto detailReviewUpdateRequestDto = createDetailReviewUpdateRequestDto(newFriendIds, reviewImageRequestDtos);

        TokenDto tokenDto = authenticationService.signIn(signUpId);

        authenticationService.withdrawal(signUpId);

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

}
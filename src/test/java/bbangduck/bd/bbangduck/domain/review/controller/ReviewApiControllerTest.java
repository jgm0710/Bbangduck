package bbangduck.bd.bbangduck.domain.review.controller;

import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.member.service.dto.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(),storedFile.getFileName()));

        Theme theme = createTheme();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createSimpleReviewCreateRequestDto(friendIds);

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
                .andExpect(jsonPath("data.reviewType").value(simpleReviewCreateRequestDto.getReviewType().name()))
                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
                .andExpect(jsonPath("data.themeClearYN").value(simpleReviewCreateRequestDto.getClearYN()))
                .andExpect(jsonPath("data.themeClearTime").value(simpleReviewCreateRequestDto.getClearTime().toString()))
                .andExpect(jsonPath("data.hintUsageCount").value(simpleReviewCreateRequestDto.getHintUsageCount()))
                .andExpect(jsonPath("data.rating").value(simpleReviewCreateRequestDto.getRating()))
                .andExpect(jsonPath("data.playTogetherFriends").exists())
                .andExpect(jsonPath("data.likeCount").value(1))
                .andExpect(jsonPath("data.myReview").value(false))
                .andExpect(jsonPath("data.like").value(true))
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
                                        REVIEW_TYPE_ENUM_LIST),
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
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION)
                        )
                ))
        ;

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

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(),storedFile.getFileName()));

        Theme theme = createTheme();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);

        ReviewCreateRequestDto simpleReviewCreateRequestDto = createSimpleReviewCreateRequestDto(friendIds);

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
    @DisplayName("상세 리뷰 조회 - 다른 회원이 생성한 리뷰 조회")
    public void getDetailReview_DifferentMember() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);

        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(),storedFile.getFileName()));

        Theme theme = createTheme();

        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
        ReviewCreateRequestDto detailReviewCreateRequestDto = createDetailReviewCreateRequestDto(friendIds, reviewImageRequestDtos);

        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), detailReviewCreateRequestDto.toServiceDto());

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
                .andExpect(jsonPath("data.reviewType").value(detailReviewCreateRequestDto.getReviewType().name()))
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
                                        REVIEW_TYPE_ENUM_LIST),
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
                                fieldWithPath("message").description(MESSAGE_DESCRIPTION))
                ));

    }


//    @Test
//    @DisplayName("상세 및 추가 설문 작성 리뷰 조회 - 다른 회원이 생성한 리뷰 조회")
//    public void getDeepReview_DifferentMember() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
//        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
//        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
//
//        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(),storedFile.getFileName()));
//
//        Theme theme = createTheme();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
//        List<String> genreCodes = createGenreCodes();
//        ReviewCreateRequestDto deepReviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);
//
//        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), deepReviewCreateRequestDto.toServiceDto());
//
//        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
//        memberSocialSignUpRequestDto.setNickname("member2");
//        memberSocialSignUpRequestDto.setSocialId("3323311321");
//
//        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId2);
//
//        //when
//        System.out.println("================================================================================================================================================");
//        ResultActions perform = mockMvc.perform(
//                get("/api/reviews/" + createdReviewId)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//        ).andDo(print());
//        System.out.println("================================================================================================================================================");
//
//        //then
//        perform
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("status").value(ResponseStatus.GET_REVIEW_SUCCESS.getStatus()))
//                .andExpect(jsonPath("data.reviewId").exists())
//                .andExpect(jsonPath("data.writerInfo").exists())
//                .andExpect(jsonPath("data.writerInfo.memberId").value(signUpId))
//                .andExpect(jsonPath("data.writerInfo.profileImageUrl").exists())
//                .andExpect(jsonPath("data.themeInfo.themeId").value(theme.getId()))
//                .andExpect(jsonPath("data.reviewType").value(deepReviewCreateRequestDto.getReviewType().name()))
//                .andExpect(jsonPath("data.reviewRecodeNumber").value(1))
//                .andExpect(jsonPath("data.themeClearYN").value(deepReviewCreateRequestDto.getClearYN()))
//                .andExpect(jsonPath("data.themeClearTime").value(deepReviewCreateRequestDto.getClearTime().toString()))
//                .andExpect(jsonPath("data.hintUsageCount").value(deepReviewCreateRequestDto.getHintUsageCount()))
//                .andExpect(jsonPath("data.rating").value(deepReviewCreateRequestDto.getRating()))
//                .andExpect(jsonPath("data.playTogetherFriends").exists())
//                .andExpect(jsonPath("data.reviewImages").exists())
//                .andExpect(jsonPath("data.comment").exists())
//                .andExpect(jsonPath("data.reviewPerceivedThemeGenres").exists())
////                .andExpect(jsonPath("data.perceivedDifficulty").value(deepReviewCreateRequestDto.getPerceivedDifficulty().name()))
////                .andExpect(jsonPath("data.perceivedHorrorGrade").value(deepReviewCreateRequestDto.getPerceivedHorrorGrade().name()))
////                .andExpect(jsonPath("data.perceivedActivity").value(deepReviewCreateRequestDto.getPerceivedActivity().name()))
////                .andExpect(jsonPath("data.scenarioSatisfaction").value(deepReviewCreateRequestDto.getScenarioSatisfaction().name()))
////                .andExpect(jsonPath("data.interiorSatisfaction").value(deepReviewCreateRequestDto.getInteriorSatisfaction().name()))
////                .andExpect(jsonPath("data.problemConfigurationSatisfaction").value(deepReviewCreateRequestDto.getProblemConfigurationSatisfaction().name()))
//                .andExpect(jsonPath("data.likeCount").value(1))
//                .andExpect(jsonPath("data.myReview").value(false))
//                .andExpect(jsonPath("data.like").value(true))
//                .andExpect(jsonPath("message").value(ResponseStatus.GET_REVIEW_SUCCESS.getMessage()))
//                .andDo(document(
//                        "get-deep-review-of-different-member-success",
//                        requestHeaders(
//                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
//                        ),
//                        responseFields(
//                                fieldWithPath("status").description(STATUS_DESCRIPTION),
//                                fieldWithPath("data.reviewId").description("조회된 리뷰의 식별 ID"),
//                                fieldWithPath("data.writerInfo.memberId").description("조회된 리뷰를 생성한 회원의 식별 ID"),
//                                fieldWithPath("data.writerInfo.nickname").description("조회된 리뷰를 생성한 회원의 닉네임"),
//                                fieldWithPath("data.writerInfo.profileImageUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 Download Url"),
//                                fieldWithPath("data.writerInfo.profileImageThumbnailUrl").description("조회된 리뷰를 생성한 회원의 프로필 이미지 파일의 썸네일 이미지 파일 Download Url"),
//                                fieldWithPath("data.themeInfo.themeId").description("조회된 리뷰가 등록된 테마의 식별 ID"),
//                                fieldWithPath("data.themeInfo.themeName").description("조회된 리뷰가 등록된 테마의 이름"),
//                                fieldWithPath("data.themeInfo.themeImageUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일 Download Url"),
//                                fieldWithPath("data.themeInfo.themeImageThumbnailUrl").description("조회된 리뷰가 등록된 테마에 등록된 이미지 파일의 썸네일 이미지 파일 Download Url"),
//                                fieldWithPath("data.reviewType").description("조회된 리뷰의 Type +\n" +
//                                        REVIEW_TYPE_ENUM_LIST),
//                                fieldWithPath("data.reviewRecodeNumber").description("조회된 리뷰의 기록 번호 +\n" +
//                                        "리뷰 생성 시 회원별 방탈출 기록 번호를 매긴다고 생각하면 된다. (주로 회원의 방탈출 기록 조회 시 사용)"),
//                                fieldWithPath("data.themeClearYN").description("조회된 리뷰의 테마 클리어 여부"),
//                                fieldWithPath("data.themeClearTime").description("조회된 리뷰의 테마 클리어 시간"),
//                                fieldWithPath("data.hintUsageCount").description("조회된 리뷰의 테마 플레이 시 사용한 힌트 개수"),
//                                fieldWithPath("data.rating").description("조회된 리뷰의 테마에 대한 평점"),
//                                fieldWithPath("data.playTogetherFriends[0].memberId").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 식별 ID"),
//                                fieldWithPath("data.playTogetherFriends[0].nickname").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 닉네임"),
//                                fieldWithPath("data.playTogetherFriends[0].profileImageUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 Download Url"),
//                                fieldWithPath("data.playTogetherFriends[0].profileImageThumbnailUrl").description("조회된 리뷰에 등록된 함께 플레이한 친구 목록 중 첫 번째 친구 회원의 프로필 이미지 파일 썸네일 이미지 파일 Download Url"),
//                                fieldWithPath("data.likeCount").description("조회된 리뷰에 등록된 좋아요 개수"),
//                                fieldWithPath("data.myReview").description("조회된 리뷰가 인증된 사용자가 생성한 리뷰인지 여부 +\n" +
//                                        "[true -> 본인이 생성한 리뷰, false -> 다른 회원이 생성한 리뷰]"),
//                                fieldWithPath("data.like").description("조회된 리뷰에 인증된 사용자가 좋아요를 등록했는지 여부 +\n" +
//                                        "[true -> 본인이 좋아요를 등록한 리뷰, false -> 본인이 좋아요를 등록하지 않은 리뷰]"),
//                                fieldWithPath("data.reviewImages[0].reviewImageId").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 식별 ID"),
//                                fieldWithPath("data.reviewImages[0].reviewImageUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 Download Url"),
//                                fieldWithPath("data.reviewImages[0].reviewImageThumbnailUrl").description("조회된 리뷰에 등록된 이미지 파일 목록 중 첫 번째 이미지 파일의 썸네일 이미지 파일 Download Url"),
//                                fieldWithPath("data.comment").description("조회된 리뷰에 등록된 상세 코멘트"),
//                                fieldWithPath("data.reviewPerceivedThemeGenres[0].genreId").description("조회된 리뷰에 등록된 테마에 대한 체감 장르 목록 중 첫 번째 장르의 식별 ID"),
//                                fieldWithPath("data.reviewPerceivedThemeGenres[0].genreCode").description("조회된 리뷰에 등록된 테마에 대한 체감 장르 목록 중 첫 번째 장르의 코드"),
//                                fieldWithPath("data.reviewPerceivedThemeGenres[0].genreName").description("조회된 리뷰에 등록된 테마에 대한 체감 장르 목록 중 첫 번째 장르의 이름"),
//                                fieldWithPath("data.perceivedDifficulty").description("조회된 리뷰에 등록된 테마에 대한 체감 난이도 +\n" +
//                                        DIFFICULTY_ENUM_LIST),
//                                fieldWithPath("data.perceivedHorrorGrade").description("조회된 리뷰에 등록된 테마에 대한 체감 공포도 +\n" +
//                                        HORROR_GRADE_ENUM_LIST),
//                                fieldWithPath("data.perceivedActivity").description("조회된 리뷰에 등록된 테마에 대한 체감 활동성 +\n" +
//                                        ACTIVITY_ENUM_LIST),
//                                fieldWithPath("data.scenarioSatisfaction").description("조회된 리뷰에 등록된 테마에 대한 시나리오 만족도 +\n" +
//                                        SATISFACTION_ENUM_LIST),
//                                fieldWithPath("data.interiorSatisfaction").description("조회된 리뷰에 등록된 테마에 대한 인테리어 만족도 +\n" +
//                                        SATISFACTION_ENUM_LIST),
//                                fieldWithPath("data.problemConfigurationSatisfaction").description("조회된 리뷰에 등록된 테마에 대한 문제 구성 만족도 +\n" +
//                                        SATISFACTION_ENUM_LIST),
//                                fieldWithPath("message").description(MESSAGE_DESCRIPTION))
//                ));
//
//    }

//    @Test
//    @DisplayName("리뷰 조회 - 리뷰를 찾을 수 없는 경우")
//    public void getReview_ReviewNotFound() throws Exception {
//        //given
//        MemberSocialSignUpRequestDto memberSocialSignUpRequestDto = createMemberSocialSignUpRequestDto();
//        Long signUpId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
//        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
//        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
//
//        memberService.updateProfileImage(signUpId, new MemberProfileImageDto(storedFile.getId(),storedFile.getFileName()));
//
//        Theme theme = createTheme();
//
//        List<Long> friendIds = createFriendToMember(memberSocialSignUpRequestDto, signUpId);
//        List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
//        List<String> genreCodes = createGenreCodes();
//        ReviewCreateRequestDto deepReviewCreateRequestDto = createDeepReviewCreateRequestDto(friendIds, reviewImageRequestDtos, genreCodes);
//
//        Long createdReviewId = reviewService.createReview(signUpId, theme.getId(), deepReviewCreateRequestDto.toServiceDto());
//
//        memberSocialSignUpRequestDto.setEmail("member2@emailcom");
//        memberSocialSignUpRequestDto.setNickname("member2");
//        memberSocialSignUpRequestDto.setSocialId("3323311321");
//
//        Long signUpId2 = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
//
//        reviewLikeService.addLikeToReview(signUpId2, createdReviewId);
//
//        TokenDto tokenDto = authenticationService.signIn(signUpId2);
//
//        //when
//        System.out.println("================================================================================================================================================");
//        ResultActions perform = mockMvc.perform(
//                get("/api/reviews/" + 1000000L)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//        ).andDo(print());
//        System.out.println("================================================================================================================================================");
//
//        //then
//        perform
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("status").value(ResponseStatus.REVIEW_NOT_FOUND.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.REVIEW_NOT_FOUND.getMessage()))
//        ;
//
//    }

}
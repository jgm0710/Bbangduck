package bbangduck.bd.bbangduck.domain.file.controller;

import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.global.common.MD5Utils;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.BaseJGMApiControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("파일 저장소 API Controller 테스트")
@ExtendWith(MockitoExtension.class)
class FileStorageApiControllerTest extends BaseJGMApiControllerTest {

    @Test
    @DisplayName("이미지 파일 업로드")
    public void uploadImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        MockMultipartFile multipartFile2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);

        MemberSocialSignUpRequestDto signUpDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")

                .socialType(null)
                .socialId(null)
                .build();

        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);


        //when
        ResultActions perform = mockMvc.perform(
                multipart("/api/files/images")
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].fileId").exists())
                .andExpect(jsonPath("$[0].fileName").exists())
                .andExpect(jsonPath("$[0].fileDownloadUrl").exists())
                .andExpect(jsonPath("$[0].fileThumbnailDownloadUrl").exists())
                .andDo(document(
                        "upload-image-file-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("[multipart/form-data;charset=UTF-8] 고정"),
                                headerWithName(securityJwtProperties.getJwtTokenHeader()).description(JWT_TOKEN_HEADER_DESCRIPTION)
                        ),
                        requestParts(
                                partWithName("files").description("업로드 파일을 files 로 요청")
                        ),
                        responseFields(
                                fieldWithPath("[0].fileId").description("업로드 된 파일 목록 중 첫 번째 파일의 식별 ID"),
                                fieldWithPath("[0].fileName").description("업로드 된 파일 목록 중 첫 번째 파일의 이름"),
                                fieldWithPath("[0].fileDownloadUrl").description("업로드 된 파일 목록 중 첫 번째 파일을 다운로드 받을 URL"),
                                fieldWithPath("[0].fileThumbnailDownloadUrl").description("업로드 된 파일 목록 중 첫 번째 파일의 썸네일 이미지를 다운로드 받을 URL")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("인증되지 않은 회원이 파일을 업로드 할 경우")
    public void uploadFile_Unauthorized() throws Exception {
        //given
        MockMultipartFile multipartFile1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        MockMultipartFile multipartFile2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);

        MemberSocialSignUpRequestDto signUpDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")

                .socialType(null)
                .socialId(null)
                .build();

        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);


        //when
        ResultActions perform = mockMvc.perform(
                multipart("/api/files/images")
                        .file(multipartFile1)
                        .file(multipartFile2)
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(ResponseStatus.UNAUTHORIZED.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UNAUTHORIZED.getMessage()))
        ;

    }

    @Test
    @DisplayName("탈퇴한 회원이 이미지 파일을 업로드 하는 경우")
    public void uploadImageFile_By_WithdrawalMember() throws Exception {
        //given
        MockMultipartFile multipartFile1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        MockMultipartFile multipartFile2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);

        MemberSocialSignUpRequestDto signUpDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")

                .socialType(null)
                .socialId(null)
                .build();

        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
        authenticationApplicationService.withdrawal(signUpMemberId, signUpMemberId);

        //when
        ResultActions perform = mockMvc.perform(
                multipart("/api/files/images")
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("status").value(ResponseStatus.FORBIDDEN.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.FORBIDDEN.getMessage()))
        ;

    }

    @Test
    @DisplayName("uploadImageFiles 이미지 파일이 아닌 파일을 업로드 할 경우")
    public void uploadImageFiles_NotImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile1 = createMockMultipartFile("files", HTML_FILE_CLASS_PATH);

        MemberSocialSignUpRequestDto signUpDto = MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")

                .socialType(null)
                .socialId(null)
                .build();

        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);


        //when
        ResultActions perform = mockMvc.perform(
                multipart("/api/files/images")
                        .file(multipartFile1)
                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.UPLOAD_NOT_IMAGE_FILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.UPLOAD_NOT_IMAGE_FILE.getMessage()));

    }

    @Test
    @DisplayName("파일 다운로드 테스트")
    public void downloadFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);
        String fileName = storedFile.getFileName();

        //when
        String requestUrl = "/api/files/" + fileName;
        ResultActions perform = mockMvc.perform(
                get(requestUrl)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "download-file-success",
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보"),
                                headerWithName(HttpHeaders.CONTENT_LENGTH).description("다운로드된 파일의 크기")
                        )
                ));

        //given
        MvcResult mvcResult = perform.andReturn();

        String eTag = mvcResult.getResponse().getHeader(HttpHeaders.ETAG);

        //when
        ResultActions perform2 = mockMvc.perform(
                get(requestUrl)
                        .header(HttpHeaders.IF_NONE_MATCH, eTag)
        ).andDo(print());

        //then

        perform2
                .andExpect(status().isNotModified())
                .andDo(document(
                        "download-file-if-none-match",
                        requestHeaders(
                                headerWithName(HttpHeaders.IF_NONE_MATCH).description("처음 파일 다운로드 요청 시 발급된 Etag 값을 요청 헤더에 실어서 요청")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보 (같은 Etag 값 응답)"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보")
                        )
                ))
        ;

        //given
        LocalDateTime tempUpdateDate = storedFile.getUpdateTimes().minusDays(1);
        String tempEncode = MD5Utils.encode(tempUpdateDate.format(DateTimeFormatter.ISO_DATE_TIME));

        //when
        ResultActions perform3 = mockMvc.perform(
                get(requestUrl)
                        .header(HttpHeaders.IF_NONE_MATCH, tempEncode)
        ).andDo(print());

        //then

        perform3
                .andExpect(status().isOk())
                .andDo(document("download-file-if-match",
                        requestHeaders(
                                headerWithName(HttpHeaders.IF_NONE_MATCH).description("처음 파일 다운로드 요청 시 발급된 Etag 값을 요청 헤더에 실어서 요청")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보 (재발급)"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보"),
                                headerWithName(HttpHeaders.CONTENT_LENGTH).description("다운로드된 파일의 크기")
                        )
                ))
        ;


    }

    @Test
    @DisplayName("파일 다운로드 시 파일을 찾을 수 없는 경우")
    public void downloadFile_NotFound() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);
        String fileName = storedFile.getFileName();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/files/" + "neklwfnmfewjiofewjio")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getMessage()))
        ;

    }

    @Test
    @DisplayName("파일 다운로드 시 실제 파일이 존재하지 않는 경우")
    public void downloadFile_NotExist() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);
        String fileName = storedFile.getFileName();

        fileStorageService.deleteActualFile(storedFile);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/files/" + fileName)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("status").value(ResponseStatus.STORED_FILE_NOT_EXIST.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.STORED_FILE_NOT_EXIST.getMessage()));

    }

    @Test
    @DisplayName("파일 썸네일 이미지 다운로드")
    public void displayThumbnail() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        String fileName = storedFile.getFileName();
        String requestUrl = "/api/files/images/thumbnails/" + fileName;

        //when

        ResultActions perform = mockMvc.perform(
                get(requestUrl)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isOk())
                .andDo(document(
                        "display-thumbnail-success",
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보"),
                                headerWithName(HttpHeaders.CONTENT_LENGTH).description("다운로드된 파일의 크기")
                        )
                ));

        //given
        MvcResult mvcResult = perform.andReturn();
        String etag = mvcResult.getResponse().getHeader(HttpHeaders.ETAG);

        //when
        ResultActions perform2 = mockMvc.perform(
                get(requestUrl)
                        .header(HttpHeaders.IF_NONE_MATCH, etag)
        ).andDo(print());

        //then
        perform2.andExpect(status().isNotModified())
                .andDo(document(
                        "display-thumbnail-if-none-match",
                        requestHeaders(
                                headerWithName(HttpHeaders.IF_NONE_MATCH).description("처음 파일 다운로드 요청 시 발급된 Etag 값을 요청 헤더에 실어서 요청")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보 (같은 Etag 값 응답)"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보")
                        )
                ));

        //given
        LocalDateTime tempUpdateDate = storedFile.getUpdateTimes().minusDays(1);
        String tempEncode = MD5Utils.encode(tempUpdateDate.format(DateTimeFormatter.ISO_DATE_TIME));

        //when
        ResultActions perform3 = mockMvc.perform(
                get(requestUrl)
                        .header(HttpHeaders.IF_NONE_MATCH, tempEncode)
        ).andDo(print());

        //then

        perform3
                .andExpect(status().isOk())
                .andDo(document("display-thumbnail-if-match",
                        requestHeaders(
                                headerWithName(HttpHeaders.IF_NONE_MATCH).description("처음 파일 다운로드 요청 시 발급된 Etag 값을 요청 헤더에 실어서 요청")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.ETAG).description("파일 다운로드 재 요청 시 캐시된 정보가 최신의 정보인지 검증하기 위한 Etag 정보 (재발급)"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("다운로드된 파일의 Content type"),
                                headerWithName(HttpHeaders.CONTENT_DISPOSITION).description("다운로드된 파일의 Disposition"),
                                headerWithName(HttpHeaders.CACHE_CONTROL).description("다운로드된 파일에 대한 Cache-Control 정보"),
                                headerWithName(HttpHeaders.CONTENT_LENGTH).description("다운로드된 파일의 크기")
                        )
                ))
        ;


    }

    @Test
    @DisplayName("이미지 파일이 아닌 파일의 썸네일 이미지를 다운로드 하는 경우")
    public void displayThumbnail_NotImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", HTML_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        String fileName = storedFile.getFileName();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/files/images/thumbnails/" + fileName)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ResponseStatus.DOWNLOAD_THUMBNAIL_OF_NON_IMAGE_FILE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.DOWNLOAD_THUMBNAIL_OF_NON_IMAGE_FILE.getMessage()));

    }

    @Test
    @DisplayName("썸네일 다운로드 시 파일을 찾을 수 없는 경우")
    public void displayThumbnail_NotFound() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        String fileName = storedFile.getFileName();

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/files/images/thumbnails/" + "fdjklafjdklafjslafjdsioa")
        ).andDo(print());

        //then
        perform
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getMessage()));

    }

    @Test
    @DisplayName("썸네일 다운로드 시 실제 파일이 존재하지 않는 경우")
    public void displayThumbnail_NotExist() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        String fileName = storedFile.getFileName();

        fileStorageService.deleteActualFile(storedFile);

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/files/images/thumbnails/" + fileName)
        ).andDo(print());

        //then
        perform
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("status").value(ResponseStatus.STORED_FILE_NOT_EXIST.getStatus()))
                .andExpect(jsonPath("data").doesNotExist())
                .andExpect(jsonPath("message").value(ResponseStatus.STORED_FILE_NOT_EXIST.getMessage()));
    }

    // FIXME: 2021-05-12 파일 삭제 기능 부분 논의 완료되면 해당 주석 삭제
//    @Test
//    @DisplayName("이미지 파일 삭제 테스트")
//    public void deleteFile() throws Exception {
//        //given
//        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
//        Long uploadFileId = fileStorageService.uploadImageFile(multipartFile);
//        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);
//
//        MemberSignUpDto signUpDto = MemberSignUpDto.builder()
//                .email("test@email.com")
//                .nickname("test")
//
//                .socialType(null)
//                .socialId(null)
//                .build();
//
//        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
//        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                delete("/api/files/" + storedFile.getFileName())
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("status").value(ResponseStatus.DELETE_FILE_SUCCESS.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.DELETE_FILE_SUCCESS.getMessage()));
//
//        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadStoredFileAsResource(storedFile.getFileName()));
//        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadThumbnailOfStoredImageFile(storedFile.getFileName()));
//        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () -> fileStorageService.getResource(storedFile.getFileName(), storedFile.getUploadPathString()));
//        assertThrows(ActualStoredFileDownloadFailUnknownException.class,
//                () -> fileStorageService.getResource(storedFile.getThumbnailImageFileName(fileStorageProperties.getThumbnailPrefix()), storedFile.getUploadPathString()));
//    }
//
//    @Test
//    @DisplayName("이미 삭제된 파일을 다시 삭제하는 경우")
//    public void delete_DeletedFile() throws Exception {
//        //given
//        MockMultipartFile multipartFile = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
//        Long uploadFileId = fileStorageService.uploadFile(multipartFile);
//        FileStorage storedFile = fileStorageService.getStoredFile(uploadFileId);
//        String fileName = storedFile.getFileName();
//        fileStorageService.deleteFile(fileName);
//
//        MemberSignUpDto signUpDto = MemberSignUpDto.builder()
//                .email("test@email.com")
//                .nickname("test")
//
//                .socialType(null)
//                .socialId(null)
//                .build();
//
//        Long signUpMemberId = authenticationService.signUp(signUpDto.toServiceDto());
//        TokenDto tokenDto = authenticationService.signIn(signUpMemberId);
//
//        //when
//        ResultActions perform = mockMvc.perform(
//                delete("/api/files/" + storedFile.getFileName())
//                        .header(securityJwtProperties.getJwtTokenHeader(), tokenDto.getAccessToken())
//        ).andDo(print());
//
//        //then
//        perform
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("status").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getStatus()))
//                .andExpect(jsonPath("data").doesNotExist())
//                .andExpect(jsonPath("message").value(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE.getMessage()));
//
//    }

}
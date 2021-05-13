package bbangduck.bd.bbangduck.domain.file.controller;

import bbangduck.bd.bbangduck.domain.file.dto.UploadedImageFileResponseDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.global.common.exception.MD5EncodingUnknownException;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.global.common.MD5Utils;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.URLEncodingUnknownException;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 파일 업로드, 다운로드 요청을 담당하여 처리하는 Controller<br>
 * 파일 삭제 기능은 각 도메인별 파일 삭제 기능을 통해 진행합니다.<br>
 * (다른 회원이 업로드한 파일은 삭제하지 못하도록 처리하기 위함)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Slf4j
public class FileStorageApiController {

    private final FileStorageService fileStorageService;

    private final FileStorageProperties fileStorageProperties;

    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<List<UploadedImageFileResponseDto>>> uploadImageFiles(
            @RequestParam MultipartFile[] files
    ) {
        List<UploadedImageFileResponseDto> uploadedImageFileResponseDtos = Arrays.stream(files)
                .map(file -> {
                    Long storedFileId = fileStorageService.uploadImageFile(file);
                    FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
                    try {
                        return UploadedImageFileResponseDto.convert(storedFile);
                    } catch (UnsupportedEncodingException e) {
                        log.error("URL Encoding Error 발생");
                        e.printStackTrace();
                        throw new URLEncodingUnknownException();
                    }
                }).collect(Collectors.toList());

        log.info("Upload image file success");
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.UPLOAD_IMAGE_FILE_SUCCESS, uploadedImageFileResponseDtos));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadStoredFileAsResource(fileName);
        FileStorage storedFile = fileStorageService.getStoredFile(fileName);
        MediaType parseContentType = getParseContentType(storedFile.getFileType());
        String contentDisposition = getContentDisposition(resource.getFilename());
        LocalDateTime lastModified = storedFile.getUpdateDate();

        return getResourceResponseEntity(resource, parseContentType, contentDisposition, lastModified);
    }

    @GetMapping("/images/thumbnails/{fileName:.+}")
    public ResponseEntity<Resource> displayThumbnail(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadThumbnailOfStoredImageFile(fileName);
        FileStorage storedFile = fileStorageService.getStoredFile(fileName);
        MediaType parseContentType = getParseContentType(storedFile.getFileType());
        String contentDisposition = getContentDisposition(resource.getFilename());
        LocalDateTime lastModified = storedFile.getUpdateDate();

        return getResourceResponseEntity(resource, parseContentType, contentDisposition, lastModified);
    }

    // TODO: 2021-05-12 각 도메인별로 파일 삭제 기능을 따로 둬야할 듯 싶음. 권한에 대한 검증이 어려움
    // FIXME: 2021-05-12 파일 삭제 기능 부분 논의 완료되면 해당 주석 삭제
//    @DeleteMapping("/{fileName:.+}")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public ResponseEntity deleteFile(@PathVariable String fileName) {
//        fileStorageService.deleteFile(fileName);
//
//        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.DELETE_FILE_SUCCESS, null));
//    }

    private String getContentDisposition(String resourceFileName) {
        return "attachment; filename=\"" + resourceFileName + "\"";
    }

    private MediaType getParseContentType(String fileType) {
        return MediaType.parseMediaType(fileType);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(Resource resource, MediaType parseContentType, String contentDisposition, LocalDateTime lastModified) {
        try {
            CacheControl cacheControl = getCacheControl();
            String lastModifiedString = lastModified.format(DateTimeFormatter.ISO_DATE_TIME);
            String encode = MD5Utils.encode(lastModifiedString);

            log.info("File download success");
            return ResponseEntity.ok()
                    .contentType(parseContentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .cacheControl(cacheControl)
                    .eTag(encode)
                    .body(resource);
        } catch (Exception e) {
            log.error("MD5 Encoding error 발생");
            e.printStackTrace();
            throw new MD5EncodingUnknownException();
        }
    }

    private CacheControl getCacheControl() {
        long fileCacheSecond = fileStorageProperties.getFileCacheSecond();
        return CacheControl.maxAge(fileCacheSecond, TimeUnit.SECONDS)
                .noTransform()
                .mustRevalidate();
    }
}

package bbangduck.bd.bbangduck.domain.file.controller;

import bbangduck.bd.bbangduck.domain.file.dto.UploadedImageFileResponseDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileStorageApiController {

    private final FileStorageService fileStorageService;

    @PostMapping("/images")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<List<UploadedImageFileResponseDto>>> uploadImageFiles(
            @RequestParam MultipartFile[] files
    ) {
        List<UploadedImageFileResponseDto> uploadedImageFileResponseDtos = Arrays.stream(files)
                .map(file -> {
                    Long storedFileId = fileStorageService.uploadImageFile(file);
                    FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
                    return UploadedImageFileResponseDto.convert(storedFile);
                }).collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.GET_MEMBER_PROFILE_SUCCESS, uploadedImageFileResponseDtos));
    }

    // TODO: 2021-05-09 파일 다운로드 기능 구현
    @GetMapping("/{fileName:.+}")
    public ResponseEntity downloadFile(@PathVariable String fileName) {
        return null;
    }

    // TODO: 2021-05-09 이미지 파일 썸네일 이미지 다운로드 기능 구현
    @GetMapping("/images/thumbnails/{fileName:.+}")
    public ResponseEntity displayThumbnail(@PathVariable String fileName) {
        return null;
    }
}

package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

// TODO: 2021-05-09 Class 주석 달기
@Service
@Slf4j
public class FileStorageService {

    private final FileStorageRepository fileStorageRepository;

    private final FileStorageProperties fileStorageProperties;

    private final Path fileStorageLocation;

    private final String[] DENIED_FILE_EXTENSION = {".exe", ".sh", ".zip", ".alz"};

    public FileStorageService(FileStorageRepository fileStorageRepository, FileStorageProperties fileStorageProperties) {
        this.fileStorageRepository = fileStorageRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadPath()).toAbsolutePath().normalize();
    }

    // TODO: 2021-05-09 이미지 파일의 경우만 받을 수 있도록 메서드 구현
    // TODO: 2021-05-09 파일 다운로드 기능 구현

    @Transactional
    public StoredFileDto storeFile(MultipartFile file) {
        String originalFilename = getOriginalFileName(file);
        fileExtensionCheck(originalFilename);

        String fileStoredName = UUID.randomUUID() + "_" + StringUtils.cleanPath(originalFilename);
        Path finalFileStorageLocation = createTodayDir(fileStorageLocation);

        try {
            if (fileStoredName.contains("..")) {
                log.error("File name contains \"..\"");
                throw new FileStorageException(ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH, ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH.getMessage() + " File Name : " + fileStoredName);
            }

            if (isImageType(file)) {
                storeOriginalImage(file, fileStoredName, finalFileStorageLocation);
                storeThumbnailImage(file, fileStoredName, finalFileStorageLocation);
            } else {
                Path targetLocation = finalFileStorageLocation.resolve(fileStoredName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("Store file success, it is not image file");
            }

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileStoredName)
                    .uploadPath(finalFileStorageLocation.toAbsolutePath().toString())
                    .fileType(file.getContentType())
                    .size(file.getSize())
                    .build();
            fileStorageRepository.save(fileStorage);

            return StoredFileDto.builder()
                    .fileName(fileStoredName)
                    .uploadPath(finalFileStorageLocation)
                    .build();

        } catch (Exception e) {
            log.error("Could not store file");
            e.printStackTrace();
            throw new FileStorageException(ResponseStatus.COULD_NOT_STORE_FILE, ResponseStatus.MEMBER_NOT_FOUND.getMessage() + " File Name : " + fileStoredName);
        }
    }

    private void storeThumbnailImage(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
        Path targetLocation = finalFileStorageLocation.resolve(thumbnailPrefix+fileStoredName);
        File targetFile = targetLocation.toFile();
        FileOutputStream targetOutputStream = new FileOutputStream(targetFile);

        int width = fileStorageProperties.getThumbnailImageWidth();
        int height = fileStorageProperties.getThumbnailImageHeight();
        Thumbnailator.createThumbnail(file.getInputStream(), targetOutputStream, width, height);
        log.info("Store thumbnail image success");

        targetOutputStream.close();
    }

    private void storeOriginalImage(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        Path targetLocation = finalFileStorageLocation.resolve(fileStoredName);
        File targetFile = targetLocation.toFile();
        FileOutputStream targetOutputStream = new FileOutputStream(targetFile);

        int width = fileStorageProperties.getOriginalImageWidth();
        int height = fileStorageProperties.getOriginalImageHeight();
        Thumbnailator.createThumbnail(file.getInputStream(), targetOutputStream, width, height);
        log.info("Store original image success");

        targetOutputStream.close();
    }

    private boolean isImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            boolean startsWith = contentType.startsWith("image");
            log.info("File is image type : {}", startsWith);
            return startsWith;
        }

        log.warn("Content type of file does not exist");
        return false;
    }

    private Path createTodayDir(Path fileStorageLocation) {
        LocalDate now = LocalDate.now();
        String year = Integer.toString(now.getYear());
        String month = Integer.toString(now.getMonthValue());
        String dayOfMonth = Integer.toString(now.getDayOfMonth());
        Path datePath = Paths.get(year, month, dayOfMonth);
        Path finalFileStorageLocation = fileStorageLocation.resolve(datePath);

        try {
            if (Files.notExists(finalFileStorageLocation)) {
                log.info("Today directory does not exist in upload path");
                Files.createDirectories(finalFileStorageLocation);
                log.info("Create today directory");
            }
        } catch (Exception e) {
            log.error("Could not create today directory");
            e.printStackTrace();
            throw new FileStorageException(ResponseStatus.COULD_NOT_CREATE_DIRECTORY);
        }
        return finalFileStorageLocation;
    }

    private String getOriginalFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error("Original file name is blank");
            throw new FileUploadFailException(ResponseStatus.ORIGINAL_FILE_IS_BLANK);
        }
        return originalFilename;
    }

    private void fileExtensionCheck(String originalFilename) {
        String lowFileName = originalFilename.toLowerCase();

        for (String dfe :
                DENIED_FILE_EXTENSION) {
            if (lowFileName.endsWith(dfe)) {
                log.error("File extension is denied extension");
                throw new FileUploadFailException(ResponseStatus.DENIED_FILE_EXTENSION);
            }
        }
    }
}

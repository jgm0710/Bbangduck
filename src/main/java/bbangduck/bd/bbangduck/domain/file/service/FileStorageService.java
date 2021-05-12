package bbangduck.bd.bbangduck.domain.file.service;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.*;
import bbangduck.bd.bbangduck.domain.file.repository.FileStorageRepository;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 파일 업로드, 다운로드, 삭제에 대한 비즈니스 로직을 담당하는 Service
 */
@Service
@Slf4j
@Transactional(readOnly = true)
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

    @Transactional
    public Long uploadImageFile(MultipartFile file) {
        log.info("Try store image file");
        if (!isImageType(file.getContentType())) {
            log.error("File is not image file, so file upload fail");
            throw new FileUploadBadRequestException(ResponseStatus.UPLOAD_NOT_IMAGE_FILE);
        }

        log.info("Try store image file");
        return uploadFile(file);
    }

    @Transactional
    public Long uploadFile(MultipartFile file) {
        log.info("Try store file");
        String originalFilename = getOriginalFileName(file);
        fileExtensionCheck(originalFilename);

        String fileStoredName = UUID.randomUUID() + "_" + StringUtils.cleanPath(originalFilename);
        Path finalFileStorageLocation = createTodayDir(fileStorageLocation);

        try {
            if (fileStoredName.contains("..")) {
                log.error("File name contains \"..\"");
                throw new FileUploadBadRequestException(ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH, ResponseStatus.FILE_NAME_CONTAINS_WRONG_PATH.getMessage() + " File Name : " + fileStoredName);
            }

            if (isImageType(file.getContentType())) {
                storeOriginalImage(file, fileStoredName, finalFileStorageLocation);
                storeThumbnailImage(file, fileStoredName, finalFileStorageLocation);
            } else {
                storeFile(file, fileStoredName, finalFileStorageLocation);
            }

            FileStorage fileStorage = FileStorage.builder()
                    .fileName(fileStoredName)
                    .uploadPath(finalFileStorageLocation.toAbsolutePath().toString())
                    .fileType(file.getContentType())
                    .size(file.getSize())
                    .build();
            FileStorage savedFile = fileStorageRepository.save(fileStorage);

            return savedFile.getId();

        } catch (Exception e) {
            log.error("Could not store file");
            e.printStackTrace();
            throw new FileActualStorageFailUnknownException(ResponseStatus.COULD_NOT_STORE_FILE, ResponseStatus.COULD_NOT_STORE_FILE.getMessage() + " File Name : " + fileStoredName);
        }
    }

    private void storeFile(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        Path targetLocation = finalFileStorageLocation.resolve(fileStoredName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        log.info("Store file success, it is not image file");
    }

    public Resource loadStoredFileAsResource(String fileName) {
        log.info("Try download stored file");
        FileStorage storedFile = getStoredFile(fileName);

        return getResource(storedFile.getFileName(), storedFile.getUploadPathString());
    }

    public Resource loadThumbnailOfStoredImageFile(String fileName) {
        log.info("Try download thumbnail of stored image file");
        FileStorage storedFile = getStoredFile(fileName);

        if (!isImageType(storedFile.getFileType())) {
            log.error("Thumbnails of non-image files cannot be downloaded");
            throw new StoredFileDownloadBadRequestException(ResponseStatus.DOWNLOAD_THUMBNAIL_OF_NON_IMAGE_FILE);
        }

        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();

        return getResource(storedFile.getThumbnailImageFileName(thumbnailPrefix), storedFile.getUploadPathString());
    }

    @Transactional
    public void deleteFile(String fileName) {
        log.info("Try delete file");
        FileStorage storedFile = getStoredFile(fileName);
        fileStorageRepository.delete(storedFile);
        deleteActualFile(storedFile);
    }

    public void deleteActualFile(FileStorage storedFile) {
        try {
            Path fileStoredPath = storedFile.getFileStoredPath();
            Files.deleteIfExists(fileStoredPath);
            log.info("Delete file success");

            if (isImageType(storedFile.getFileType())) {
                log.info("Try delete thumbnail of image file");
                String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
                Path thumbnailStoredPath = storedFile.getThumbnailStoredPath(thumbnailPrefix);
                Files.deleteIfExists(thumbnailStoredPath);
                log.info("Delete thumbnail of image file success");
            }

        } catch (IOException e) {
            log.error("File delete fail for unknown reason");
            e.printStackTrace();
            throw new ActualStoredFileDeleteFailUnknownException(ResponseStatus.FILE_DELETE_FAIL_FOR_UNKNOWN_REASON);
        }
    }

    public FileStorage getStoredFile(String fileName) {
        return fileStorageRepository.findByFileName(fileName).orElseThrow(() -> {
            log.error("File lookup through the file name failed");
            throw new StoredFileNotFoundException(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE);
        });
    }

    public FileStorage getStoredFile(Long fileId) {
        return fileStorageRepository.findById(fileId).orElseThrow(() ->{
            log.error("File lookup through the file id failed");
            throw new StoredFileNotFoundException(ResponseStatus.STORED_FILE_NOT_FOUND_IN_DATABASE);
        });
    }

    public Resource getResource(String fileName, String uploadPath) {
        Path path = Path.of(uploadPath);
        Path fileStoredPath = path.resolve(fileName).normalize();

        try {
            UrlResource urlResource = new UrlResource(fileStoredPath.toUri());

            if (urlResource.exists()) {
                log.info("Succeeded in creating a resource from the file storage path");
                return urlResource;
            } else {
                log.error("The requested file does not actually exist, File name : {}, Upload path : {}", fileName, uploadPath);
                throw new ActualStoredFileDownloadFailUnknownException(ResponseStatus.STORED_FILE_NOT_EXIST);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error("An unknown error occurred while converting the file to a resource");
            throw new ActualStoredFileDownloadFailUnknownException(ResponseStatus.FILE_DOWNLOAD_FAIL_FOR_UNKNOWN_REASON);
        }
    }

    private void storeThumbnailImage(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
        String thumbnailImageName = thumbnailPrefix + fileStoredName;
        Path targetLocation = finalFileStorageLocation.resolve(thumbnailImageName);
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

    private boolean isImageType(String contentType) {
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
            throw new FileActualStorageFailUnknownException(ResponseStatus.COULD_NOT_CREATE_DIRECTORY);
        }
        return finalFileStorageLocation;
    }

    private String getOriginalFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error("Original file name is blank");
            throw new FileUploadBadRequestException(ResponseStatus.ORIGINAL_FILE_IS_BLANK);
        }
        return originalFilename;
    }

    private void fileExtensionCheck(String originalFilename) {
        String lowFileName = originalFilename.toLowerCase();

        for (String dfe :
                DENIED_FILE_EXTENSION) {
            if (lowFileName.endsWith(dfe)) {
                log.error("File extension is denied extension");
                throw new FileUploadBadRequestException(ResponseStatus.DENIED_FILE_EXTENSION);
            }
        }
    }
}

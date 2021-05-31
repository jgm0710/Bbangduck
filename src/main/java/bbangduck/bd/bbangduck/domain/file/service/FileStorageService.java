package bbangduck.bd.bbangduck.domain.file.service;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.*;
import bbangduck.bd.bbangduck.domain.file.repository.FileStorageRepository;
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
        String contentType = file.getContentType();
        if (!isImageType(contentType)) {
            log.error("File is not image file, so file upload fail. File content type : {}", contentType);
            throw new UploadFileNotImageFileException();
        }

        return uploadFile(file);
    }

    @Transactional
    public Long uploadFile(MultipartFile file) {
        String originalFilename = getOriginalFileName(file);
        fileExtensionCheck(originalFilename);

        String fileStoredName = UUID.randomUUID() + "_" + StringUtils.cleanPath(originalFilename);
        Path finalFileStorageLocation = createTodayDir(fileStorageLocation);

        try {
            if (fileStoredName.contains("..")) {
                log.error("File name contains \"..\"");
                log.error("File name : {}",fileStoredName);
                throw new FileNameContainsWrongPathException(fileStoredName);
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
            throw new CouldNotStoreFileUnknownException(fileStoredName);
        }
    }

    private void storeFile(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        Path targetLocation = finalFileStorageLocation.resolve(fileStoredName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    public Resource loadStoredFileAsResource(String fileName) {
        FileStorage storedFile = getStoredFile(fileName);

        return getResource(storedFile.getFileName(), storedFile.getUploadPathString());
    }

    public Resource loadThumbnailOfStoredImageFile(String fileName) {
        FileStorage storedFile = getStoredFile(fileName);
        String fileType = storedFile.getFileType();

        if (!isImageType(fileType)) {
            log.error("Thumbnails of non-image files cannot be downloaded. File type : {}",fileType);
            throw new DownloadThumbnailOfNonImageFileException();
        }

        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();

        return getResource(storedFile.getThumbnailImageFileName(thumbnailPrefix), storedFile.getUploadPathString());
    }

    @Transactional
    public void deleteFile(String fileName) {
        FileStorage storedFile = getStoredFile(fileName);
        fileStorageRepository.delete(storedFile);
        deleteActualFile(storedFile);
    }

    @Transactional
    public void deleteFile(Long fileStorageId) {
        FileStorage storedFile = getStoredFile(fileStorageId);
        fileStorageRepository.delete(storedFile);
        deleteActualFile(storedFile);
    }

    public void deleteActualFile(FileStorage storedFile) {
        try {
            Path fileStoredPath = storedFile.getFileStoredPath();
            Files.deleteIfExists(fileStoredPath);

            if (isImageType(storedFile.getFileType())) {
                String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
                Path thumbnailStoredPath = storedFile.getThumbnailStoredPath(thumbnailPrefix);
                Files.deleteIfExists(thumbnailStoredPath);
            }

        } catch (IOException e) {
            log.error("File delete fail for unknown reason");
            e.printStackTrace();
            throw new ActualStoredFileDeleteFailUnknownException();
        }
    }

    public FileStorage getStoredFile(String fileName) {
        return fileStorageRepository.findByFileName(fileName).orElseThrow(() -> {
            log.error("File lookup through the file name failed. File name : {}", fileName);
            throw new StoredFileNotFoundException();
        });
    }

    public FileStorage getStoredFile(Long fileId) {
        return fileStorageRepository.findById(fileId).orElseThrow(() ->{
            log.error("File lookup through the file id failed");
            throw new StoredFileNotFoundException();
        });
    }

    public Resource getResource(String fileName, String uploadPath) {
        Path path = Path.of(uploadPath);
        Path fileStoredPath = path.resolve(fileName).normalize();

        try {
            UrlResource urlResource = new UrlResource(fileStoredPath.toUri());

            if (urlResource.exists()) {
                return urlResource;
            } else {
                log.error("The requested file does not actually exist, File name : {}, Upload path : {}", fileName, uploadPath);
                throw new ActualStoredFileNotExistException();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error("An unknown error occurred while converting the file to a resource");
            throw new FileDownloadFailForUnknownReasonException();
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

        targetOutputStream.close();
    }

    private void storeOriginalImage(MultipartFile file, String fileStoredName, Path finalFileStorageLocation) throws IOException {
        Path targetLocation = finalFileStorageLocation.resolve(fileStoredName);
        File targetFile = targetLocation.toFile();
        FileOutputStream targetOutputStream = new FileOutputStream(targetFile);

        int width = fileStorageProperties.getOriginalImageWidth();
        int height = fileStorageProperties.getOriginalImageHeight();
        Thumbnailator.createThumbnail(file.getInputStream(), targetOutputStream, width, height);

        targetOutputStream.close();
    }

    private boolean isImageType(String contentType) {
        if (contentType != null) {
            return contentType.startsWith("image");
        }

        log.warn("Content type of file does not exist. Content type is null.");
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
                Files.createDirectories(finalFileStorageLocation);
            }
        } catch (Exception e) {
            log.error("Could not create today directory");
            e.printStackTrace();
            throw new CouldNotCreateDirectoryUnknownException();
        }
        return finalFileStorageLocation;
    }

    private String getOriginalFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error("Original file name is blank");
            throw new OriginalFileNameIsBlankException();
        }
        return originalFilename;
    }

    private void fileExtensionCheck(String originalFilename) {
        String lowFileName = originalFilename.toLowerCase();

        for (String dfe :
                DENIED_FILE_EXTENSION) {
            if (lowFileName.endsWith(dfe)) {
                log.error("File extension is denied extension. Original file name : {}", originalFilename);
                throw new DeniedFileExtensionException();
            }
        }
    }
}

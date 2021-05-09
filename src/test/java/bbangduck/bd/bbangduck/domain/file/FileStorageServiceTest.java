package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.FileUploadBadRequestException;
import bbangduck.bd.bbangduck.domain.file.exception.StoredFileDownloadBadRequestException;
import bbangduck.bd.bbangduck.domain.file.exception.ActualStoredFileDownloadFailUnknownException;
import bbangduck.bd.bbangduck.domain.file.exception.StoredFileNotFoundException;
import bbangduck.bd.bbangduck.domain.file.repository.FileStorageRepository;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest extends BaseJGMServiceTest {

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    FileStorageProperties fileStorageProperties;

    @Autowired
    FileStorageRepository fileStorageRepository;

    @Test
    @DisplayName("파일 저장 테스트")
    public void storeFileTest() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);

        //when
        Long storedFileId = fileStorageService.uploadFile(multipartFile);

        //then
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        String filename = multipartFile.getOriginalFilename();
        boolean contains = storedFile.getFileName().contains(filename);
        String uploadPath = fileStorageProperties.getUploadPath();
        LocalDateTime now = LocalDateTime.now();
        String expectUploadFilePath = uploadPath + "\\" + now.getYear() + "\\" + now.getMonthValue() + "\\" + now.getDayOfMonth();

        assertTrue(contains);
        assertEquals(expectUploadFilePath, storedFile.getUploadPathString());

        FileStorage findFileStorage = fileStorageRepository.findByFileName(storedFile.getFileName()).orElse(null);

        assert findFileStorage != null;
        assertEquals(storedFile.getFileName(), findFileStorage.getFileName());
        assertEquals(storedFile.getUploadPathString(), findFileStorage.getUploadPathString());
        assertEquals(multipartFile.getSize(), findFileStorage.getSize());
        assertEquals(multipartFile.getContentType(), findFileStorage.getFileType());
    }

    @Test
    @DisplayName("파일 저장 시 파일명을 알 수 없는 경우")
    public void storeFile_FileNameIsBlank() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource(IMAGE_FILE_CLASS_PATH);
        File file = classPathResource.getFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        //when

        //then
        assertThrows(FileUploadBadRequestException.class, () -> fileStorageService.uploadFile(multipartFile));
    }

    @Test
    @DisplayName("zip 파일을 업로드할 경우")
    public void storeZipFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", ZIP_FILE_CLASS_PATH);

        //when

        //then
        assertThrows(FileUploadBadRequestException.class, () -> fileStorageService.uploadFile(multipartFile));
    }

    @Test
    @DisplayName("html 파일을 업로드 할 경우")
    public void storeHtmlFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", HTML_FILE_CLASS_PATH);
        //when
        Long storedFileId = fileStorageService.uploadFile(multipartFile);

        //then
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        String filename = multipartFile.getOriginalFilename();
        boolean contains = storedFile.getFileName().contains(filename);
        String uploadPath = fileStorageProperties.getUploadPath();
        LocalDateTime now = LocalDateTime.now();
        String expectUploadFilePath = uploadPath + "\\" + now.getYear() + "\\" + now.getMonthValue() + "\\" + now.getDayOfMonth();

        assertTrue(contains);
        assertEquals(expectUploadFilePath, storedFile.getUploadPathString());

        FileStorage findFileStorage = fileStorageRepository.findByFileName(storedFile.getFileName()).orElse(null);

        assert findFileStorage != null;
        assertEquals(storedFile.getFileName(), findFileStorage.getFileName());
        assertEquals(storedFile.getUploadPathString(), findFileStorage.getUploadPathString());
        assertEquals(multipartFile.getSize(), findFileStorage.getSize());
        assertEquals(multipartFile.getContentType(), findFileStorage.getFileType());
    }

    @Test
    @DisplayName("이미지 파일 업로드")
    public void storeImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);

        //when
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);

        //then
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        String filename = multipartFile.getOriginalFilename();
        boolean contains = storedFile.getFileName().contains(filename);
        String uploadPath = fileStorageProperties.getUploadPath();
        LocalDateTime now = LocalDateTime.now();
        String expectUploadFilePath = uploadPath + "\\" + now.getYear() + "\\" + now.getMonthValue() + "\\" + now.getDayOfMonth();

        assertTrue(contains);
        assertEquals(expectUploadFilePath, storedFile.getUploadPathString());

        FileStorage findFileStorage = fileStorageRepository.findByFileName(storedFile.getFileName()).orElse(null);

        assert findFileStorage != null;
        assertEquals(storedFile.getFileName(), findFileStorage.getFileName());
        assertEquals(storedFile.getUploadPathString(), findFileStorage.getUploadPathString());
        assertEquals(multipartFile.getSize(), findFileStorage.getSize());
        assertEquals(multipartFile.getContentType(), findFileStorage.getFileType());

    }

    @Test
    @DisplayName("이미지 파일 업로드 시 파일이 이미지 파일이 아닌 경우")
    public void storeImageFile_NotImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", HTML_FILE_CLASS_PATH);
        //when

        //then
        assertThrows(FileUploadBadRequestException.class, () -> fileStorageService.uploadImageFile(multipartFile));


    }

    @Test
    @DisplayName("파일 다운로드 테스트")
    public void loadStoredFileAsResource() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);

        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);

        //when
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        Resource resource = fileStorageService.loadStoredFileAsResource(storedFile.getFileName());

        //then
        boolean fileNameContains = resource.toString().contains(storedFile.getFileName());
        assertTrue(fileNameContains);
    }

    @Test
    @DisplayName("파일 썸네일 이미지 다운로드 테스트")
    public void loadThumbnailOfStoredImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);

        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);

        //when
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        Resource resource = fileStorageService.loadThumbnailOfStoredImageFile(storedFile.getFileName());

        //then
        String thumbnailImageName = fileStorageProperties.getThumbnailPrefix() + storedFile.getFileName();
        boolean fileNameContains = resource.toString().contains(thumbnailImageName);

        assertTrue(fileNameContains);
    }

    @Test
    @DisplayName("파일 썸네일 이미지 다운로드 시 해당 파일이 이미지 파일이 아닌 경우")
    public void loadThumbnailOfStoredImageFile_NotImageFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", HTML_FILE_CLASS_PATH);

        Long storedFileId = fileStorageService.uploadFile(multipartFile);

        //when

        //then
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        assertThrows(StoredFileDownloadBadRequestException.class, () -> fileStorageService.loadThumbnailOfStoredImageFile(storedFile.getFileName()));

    }

    @Test
    @DisplayName("실제 파일이 존재하지 않는 경우 파일 다운로드 테스트")
    public void loadStoredFileAsResource_NotExistFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);

        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);

        Resource resource = fileStorageService.loadStoredFileAsResource(storedFile.getFileName());

        System.out.println("resource = " + resource);
        System.out.println("storedFile = " + storedFile);
        boolean fileNameContains = resource.toString().contains(storedFile.getFileName());

        assertTrue(fileNameContains);

        fileStorageService.deleteActualFile(storedFile);

        //when

        //then
        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () ->fileStorageService.loadStoredFileAsResource(storedFile.getFileName()));
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    public void deleteFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        String fileName = storedFile.getFileName();

        //when
        fileStorageService.deleteFile(fileName);

        //then

        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.getStoredFile(fileName));
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadStoredFileAsResource(fileName));
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadThumbnailOfStoredImageFile(fileName));
        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () -> fileStorageService.getResource(storedFile.getFileName(), storedFile.getUploadPathString()));
        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () -> fileStorageService.getResource(storedFile.getThumbnailImageFileName(thumbnailPrefix), storedFile.getUploadPathString()));
    }

    @Test
    @DisplayName("이미 삭제된 파일을 다시 삭제할 경우")
    public void delete_DeletedFile() throws Exception {
        //given
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        String fileName = storedFile.getFileName();

        fileStorageService.deleteFile(fileName);
        //when

        //then
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.deleteFile(fileName));

    }

    @Test
    @DisplayName("실제 존재하지 않는 파일을 삭제하는 경우 테스트")
    public void deleteFile_NotExistFile() throws Exception {
        //given
        MockMultipartFile multipartFile = createMockMultipartFile("file", IMAGE_FILE_CLASS_PATH);
        Long storedFileId = fileStorageService.uploadImageFile(multipartFile);
        FileStorage storedFile = fileStorageService.getStoredFile(storedFileId);
        String fileName = storedFile.getFileName();

        fileStorageService.deleteActualFile(storedFile);

        //when
        fileStorageService.deleteFile(fileName);

        //then
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.getStoredFile(fileName));
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadStoredFileAsResource(fileName));
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.loadThumbnailOfStoredImageFile(fileName));
        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () -> fileStorageService.getResource(storedFile.getFileName(), storedFile.getUploadPathString()));
        String thumbnailPrefix = fileStorageProperties.getThumbnailPrefix();
        assertThrows(ActualStoredFileDownloadFailUnknownException.class, () -> fileStorageService.getResource(storedFile.getThumbnailImageFileName(thumbnailPrefix), storedFile.getUploadPathString()));

    }
}
package bbangduck.bd.bbangduck.domain.file;

import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import bbangduck.bd.bbangduck.member.BaseJGMServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
        String paramName = "file";
        String path = "/static/test/puppy.jpg";

        MockMultipartFile multipartFile = createMockMultipartFile(paramName, path);

        //when
        StoredFileDto storedFileDto = fileStorageService.storeFile(multipartFile);

        //then
        String filename = multipartFile.getOriginalFilename();
        boolean contains = storedFileDto.getFileName().contains(filename);
        String uploadPath = fileStorageProperties.getUploadPath();
        LocalDateTime now = LocalDateTime.now();
        String expectUploadFilePath = uploadPath + "\\" + now.getYear() + "\\" + now.getMonthValue() + "\\" + now.getDayOfMonth();

        assertTrue(contains);
        assertEquals(expectUploadFilePath, storedFileDto.getUploadPathString());

        FileStorage findFileStorage = fileStorageRepository.findByFileName(storedFileDto.getFileName()).orElse(null);

        assert findFileStorage != null;
        assertEquals(storedFileDto.getFileName(), findFileStorage.getFileName());
        assertEquals(storedFileDto.getUploadPathString(), findFileStorage.getUploadPath());
        assertEquals(multipartFile.getSize(), findFileStorage.getSize());
        assertEquals(multipartFile.getContentType(), findFileStorage.getFileType());
    }

    @Test
    @DisplayName("파일 저장 시 파일명을 알 수 없는 경우")
    public void storeFile_FileNameIsBlank() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("/static/test/puppy.jpg");
        File file = classPathResource.getFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        //when

        //then
        assertThrows(FileUploadFailException.class, () -> fileStorageService.storeFile(multipartFile));
    }
}
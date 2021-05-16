package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLConnection;

@Disabled
@Transactional
public class BaseJGMServiceTest extends BaseTest {
    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected SecurityJwtProperties securityJwtProperties;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected FileStorageService fileStorageService;

    protected final String IMAGE_FILE_CLASS_PATH = "/static/test/puppy.jpg";

    protected final String ZIP_FILE_CLASS_PATH = "/static/test/category.zip";

    protected final String HTML_FILE_CLASS_PATH = "/static/test/category.html";

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
    }

    protected MemberSocialSignUpRequestDto createMemberSignUpRequestDto() {
        return MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")
                .socialId("123213")
                .socialType(SocialType.KAKAO)
                .build();
    }

    protected MemberUpdateProfileRequestDto createMemberUpdateRequestDto(Long fileId, String fileName) {
        return MemberUpdateProfileRequestDto.builder()
                .nickname("홍길동")
                .description("새로 적는 자기 소개")
                .fileStorageId(fileId)
                .fileName(fileName)
                .roomEscapeRecordsOpenYN(false)
                .build();
    }

}

package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberProfileImageRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.repository.SocialAccountRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URLConnection;

@Disabled
public class BaseJGMServiceTest extends BaseTest {

    @Autowired
    protected SocialAccountRepository socialAccountRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

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

    @Autowired
    protected MemberProfileImageRepository memberProfileImageRepository;

    protected final String IMAGE_FILE2_CLASS_PATH = "/static/test/bbangduck.jpg";

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



}

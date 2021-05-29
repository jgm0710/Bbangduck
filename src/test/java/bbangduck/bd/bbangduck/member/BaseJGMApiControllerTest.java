package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.URLConnection;

@Disabled
public class BaseJGMApiControllerTest extends BaseControllerTest {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected SecurityJwtProperties securityJwtProperties;

    @Autowired
    protected FileStorageService fileStorageService;

    @Autowired
    protected FileStorageProperties fileStorageProperties;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected ThemeRepository themeRepository;

    @Autowired
    protected MemberPlayInclinationRepository memberPlayInclinationRepository;

    @Autowired
    protected MemberFriendRepository memberFriendRepository;

    protected static int REFRESH_TOKEN_EXPIRED_DATE;

    protected static String JWT_TOKEN_HEADER_DESCRIPTION = "리소스 접근 시 회원 인증을 위해 필요한 JWT 토큰 인증 헤더";

    protected static String STATUS_DESCRIPTION = "요청에 대해 HttpStatus 외에 별도로 응답 상태를 구분하기 위한 상태 값";

    protected static String MESSAGE_DESCRIPTION = "요청에 따른 응답에 대한 간단한 Message";

    protected final String IMAGE_FILE_CLASS_PATH = "/static/test/puppy.jpg";

    protected final String ZIP_FILE_CLASS_PATH = "/static/test/category.zip";

    protected final String HTML_FILE_CLASS_PATH = "/static/test/category.html";

    protected final String IMAGE_FILE2_CLASS_PATH = "/static/test/bbangduck.jpg";

    protected String OBJECT_NAME_DESCRIPTION = "예외가 발생한 객체의 이름";

    protected String CODE_DESCRIPTION = "예외 코드";

    protected String DEFAULT_MESSAGE_DESCRIPTION = "발생한 예외에 대한 메세지";

    protected String FIELD_DESCRIPTION = "예외가 발생한 필드의 이름";

    @BeforeEach
    public void setUp() {
        REFRESH_TOKEN_EXPIRED_DATE = securityJwtProperties.getRefreshTokenExpiredDate();
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        memberPlayInclinationRepository.deleteAll();
        memberFriendRepository.deleteAll();
        memberRepository.deleteAll();
    }

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
    }

    protected MemberSocialSignUpRequestDto createMemberSocialSignUpRequestDto() {
        return MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();
    }

}

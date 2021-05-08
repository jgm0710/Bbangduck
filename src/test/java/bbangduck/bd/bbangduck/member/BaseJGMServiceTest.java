package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLConnection;

@Transactional
@Disabled
public class BaseJGMServiceTest extends BaseTest {
    @Autowired
    protected MemberRepository memberRepository;

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
    }
}

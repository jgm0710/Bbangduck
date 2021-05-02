package bbangduck.bd.bbangduck.common;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 기본 테스트 상속
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled
public abstract class BaseTest {
}

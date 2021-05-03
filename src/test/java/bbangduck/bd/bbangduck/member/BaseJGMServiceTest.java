package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
@Disabled
public class BaseJGMServiceTest extends BaseTest {
    @Autowired
    protected MemberRepository memberRepository;
}

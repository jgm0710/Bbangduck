package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class BaseMemberServiceTest extends BaseTest {
    @Autowired
    protected MemberRepository memberRepository;
}

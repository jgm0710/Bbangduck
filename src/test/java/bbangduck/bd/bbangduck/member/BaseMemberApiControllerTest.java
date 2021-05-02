package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class BaseMemberApiControllerTest extends BaseControllerTest {

    @Autowired
    protected MemberRepository memberRepository;
}

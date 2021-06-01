package bbangduck.bd.bbangduck.domain.admin;

import bbangduck.bd.bbangduck.config.TestConfig;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/29/0029
 * Time: 오전 10:27:03
 */
//@SpringBootTest(properties = {
//        "spring.config.location=classpath:application-test.yml"
//})
@SpringBootTest
@Transactional
//@DataJpaTest(properties = {
//        "spring.config.location=classpath:application-test.yml"
//})
//@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
@DisplayName("관리자 정보 테스트")
public class AdminInfoRepositoryTest {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminInfoRepository adminInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        Member member1 = Member.builder()
                .email("otrodevym1@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecordsOpenYN(false)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member1);
        Member member2 = Member.builder()
                .email("otrodevym2@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecordsOpenYN(false)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member2);

        Member member3 = Member.builder()
                .email("otrodevym3@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecordsOpenYN(false)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member3);


        AdminInfo adminInfoSave1 = AdminInfo.builder()
                .address("서울시 용산구1")
                .companyName("빵덕1")
                .deleteYN(false)
                .owner("빵덕님1")
                .telephone("010-0000-1111")
                .member(member1)
                .build();
        this.adminInfoRepository.save(adminInfoSave1);

        AdminInfo adminInfoSave2 = AdminInfo.builder()
                .address("서울시 용산구2")
                .companyName("빵덕2")
                .deleteYN(false)
                .owner("빵덕님2")
                .telephone("010-0000-2222")
                .member(member2)
                .build();
        this.adminInfoRepository.save(adminInfoSave2);

        AdminInfo adminInfoSave3 = AdminInfo.builder()
                .address("서울시 용산구3")
                .companyName("빵덕3")
                .deleteYN(false)
                .owner("빵덕님3")
                .telephone("010-0000-3333")
                .member(member3)
                .build();
        this.adminInfoRepository.save(adminInfoSave3);


        this.entityManager.flush();
        this.entityManager.clear();

    }


    @Test
    public void admin_info_페이징_조회_테스트() {

        AdminInfo adminInfoBase = AdminInfo.builder()
                .companyName("빵덕1")
                .build();


        Sort.Order order = Sort.Order.desc("id");
        Sort sort = Sort.by(order);

        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<AdminInfo> adminInfoPage = adminInfoRepository.searchPage(adminInfoBase, pageable);
        assertThat(adminInfoPage.getTotalPages(), CoreMatchers.is(1));
    }

    @Test
    public void admin_info_전체_조회_테스트() {

        // 전체 조회
        List<AdminInfo> adminInfos = this.adminInfoRepository.findAll();
        assertThat(adminInfos.size(), CoreMatchers.is(3));
    }

    @Test
    public void admin_info_회사명_조회_테스트() {

        // 회사 이름으로 조회
        AdminInfo adminInfo1 = AdminInfo.builder()
                .companyName("빵덕1")
                .build();
        List<AdminInfo> adminInfos1 = adminInfoRepository.search(adminInfo1);

        assertThat(adminInfos1.get(0).getAddress(), CoreMatchers.is("서울시 용산구1"));
    }




}
package bbangduck.bd.bbangduck.domain.admin;

import bbangduck.bd.bbangduck.domain.admin.dto.AdminInfoDto;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.admin.service.AdminInfoService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/5/30/0030
 * Time: 오후 3:46:19
 */
//@WebMvcTest(controllers = AdminInfoContoller.class
//        , properties = {"spring.config.location=classpath:application-test.yml"}
//)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("admin_info Contoller Test")
@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@Import(TestConfig.class)
@Transactional
@ActiveProfiles("test") // db 커넥션을 application-test.yml 파일을 따르도록 설정하는 annotation
public class AdminInfoAllTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminInfoRepository adminInfoRepository;


    @Autowired
    private EntityManager entityManager;

//    @Autowired
    @MockBean
    private AdminInfoService adminInfoService;

    @BeforeEach
    public void setup() {
        Member member1 = Member.builder()
                .email("otrodevym1@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member1);
        Member member2 = Member.builder()
                .email("otrodevym2@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member2);

        Member member3 = Member.builder()
                .email("otrodevym3@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
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

    //
    @Test
    public void admin_info_기본_테스트() throws Exception {
        AdminInfoDto build = AdminInfoDto.builder()
                .owner("빵덕")
                .address("용산구")
                .telephone("010-0000-0000")
                .companyName("뻥덕네")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(build);
        this.mockMvc.perform(get("/admin/info/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void admin_info_service_test() {
//        AdminInfoDto build = AdminInfoDto.builder().owner("빵덕님1").build();
        AdminInfoDto build = AdminInfoDto.builder()
                .companyName("빵덕1")
                .build();
        List<AdminInfoDto> adminInfoList = adminInfoService.getAdminInfoList(build);
        System.out.println("+==============================================");
        adminInfoList.stream().forEach(System.out::println);
        System.out.println("+==============================================");
    }

    @Test
    public void admin_info_contoller_test() throws Exception {
//        BDDMockito.given()
//        BDDMockito.when()
//        BDDMockito.then()

        AdminInfoDto build = AdminInfoDto.builder().owner("빵덕님1").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(build);
        ResultActions perform = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/admin/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andDo(print());

        //then
        perform
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("Test1"))
                .andReturn().getResponse().getContentAsString();

    }
}

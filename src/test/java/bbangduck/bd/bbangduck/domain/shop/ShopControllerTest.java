package bbangduck.bd.bbangduck.domain.shop;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
//@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShopControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AreaRepository areaRepository;



    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminInfoRepository adminInfoRepository;


    @Autowired
    private FranchiseRepository franchiseRepository;

    private Member member1;

    private AdminInfo adminInfoSave1;
    private Area area;
    private Franchise franchise;

    @BeforeEach
//    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    public void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
//                .apply(springSecurity())
//                .alwaysDo(print())
//                .apply(documentationConfiguration(restDocumentation)
//                        .operationPreprocessors()
//                        .withRequestDefaults(prettyPrint())
//                        .withResponseDefaults(prettyPrint()))
                .build();



        member1 = Member.builder()
                .email("otrodevym1@gmail.com")
                .password("1234")
                .nickname("developer")
                .description("개발자")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
                .refreshInfo(RefreshInfo.init(1000))
                .roles(Set.of(MemberRole.DEVELOP, MemberRole.USER, MemberRole.ADMIN))
                .build();
        this.memberRepository.save(member1);

        adminInfoSave1 = AdminInfo.builder()
                .address("서울시 용산구1")
                .companyName("빵덕1")
                .deleteYN(false)
                .owner("빵덕님1")
                .telephone("010-0000-1111")
                .member(member1)
                .build();
        this.adminInfoRepository.save(adminInfoSave1);


        Area area = Area.builder().code("SU00").name("서울").build();
        this.areaRepository.save(area);

        Franchise franchise = Franchise.builder()
                .name("용미니 가맹")
                .ownerTelephone("010-0000-0000")
                .owner("용미니")
                .adminInfo(adminInfoSave1)
                .deleteYN(false)
                .build();
        this.franchiseRepository.save(franchise);

        this.entityManager.flush();
        this.entityManager.clear();
    }


    @Test
    public void shopFindByIdTest() throws Exception {

//        this.mockMvc.perform(MockMvcRequestBuilders.get("/shop/1")).andDo(print());
    }


    @Test
    public void shopSaveTest() throws Exception {


        Area area = this.areaRepository.findByName("서울");
        
        Franchise franchise1 = this.franchiseRepository.findByName("용미니 가맹");


        ShopDto shopDto = ShopDto.builder()
                .address("서울시")
                .shopInfo("재미")
                .areaId(area.getId())
                .franchiseId(franchise1.getId())
                .build();
//        String shopDtoString = objectMapper.writeValueAsString(shopDto);

        ShopImageDto shopImageDto = ShopImageDto.builder()
                .fileName("미니 사진")
                .fileStorageId(1l)
                .build();
//        String shopImageDtoString = objectMapper.writeValueAsString(shopImageDto);

        Map<String, Object> map = new HashMap<>();

        map.put("ShopDto", shopDto);
        map.put("ShopImageDto", shopImageDto);
        String content = objectMapper.writeValueAsString(map);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/shop/")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

        ).andDo(print());
    }
}

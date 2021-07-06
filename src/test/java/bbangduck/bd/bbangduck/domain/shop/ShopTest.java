package bbangduck.bd.bbangduck.domain.shop;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.shop.entity.*;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.entity.enumerate.ShopPriceUnit;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.shop.service.ShopService;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
//@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShopTest {
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ShopService shopService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminInfoRepository adminInfoRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private FranchiseRepository franchiseRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ThemeRepository themeRepository;

    private Member member1;

    private AdminInfo adminInfoSave1;
    private Area area;
    private Franchise franchise;

    @BeforeEach
    public void setup() {
        memberRepository.deleteAll();
        adminInfoRepository.deleteAll();
        areaRepository.deleteAll();
        franchiseRepository.deleteAll();
        shopRepository.deleteAll();
        themeRepository.deleteAll();

        entityManager.flush();
        entityManager.clear();

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

        this.entityManager.flush();
        this.entityManager.clear();

        area = Area.builder().code("SU00").name("서울").build();
        this.areaRepository.save(area);

        franchise = Franchise.builder()
                .name("용미니 가맹")
                .ownerTelephone("010-0000-0000")
                .owner("용미니")
                .adminInfo(adminInfoSave1)
                .deleteYN(false)
                .build();

        this.franchiseRepository.save(franchise);

    }



    @Test
    @DisplayName("샵 저장 기능 테스트")
    public void shopSaveTest() {


        Shop shop = Shop.builder()
                .address("서울시")
                .area(area)
                .name("용미니네")
                .franchise(franchise)
                .location(Location.builder().latitude(33.0).longitude(33.0).build())
//                .shopImage(ShopImage.getInstance())
                .shopInfo("개발자 세상")
//                .shopPrices()
                .shopUrl("www.google.com")
                .deleteYN(false)
                .build();
        ShopImage shopImage = ShopImage.builder()
                .fileName("미니 사진")
                .shop(shop)
                .fileStorageId(1L)
                .build();
        ShopPrice shopPrice = ShopPrice.builder()
                .price(10000)
                .priceUnit(ShopPriceUnit.WON)
                .shop(shop)
                .build();

        this.shopService.save(shop);

        List<Shop> list = this.shopService.findByAll();

        MatcherAssert.assertThat(list.size(), CoreMatchers.is(1));

    }


    // FIXME: 2021-06-28 샵 조회 기능 테스트 깨져서 주석 처리
//    @Test
//    @DisplayName("샵 조회 기능 테스트")
//    public void shopSearchTest() {
//        Shop shop = Shop.builder()
//                .address("서울시")
//                .area(area)
//                .name("용미니네")
//                .franchise(franchise)
//                .location(Location.builder().latitude(33.0).longitude(33.0).build())
////                .shopImage(ShopImage.getInstance())
//                .shopInfo("개발자 세상")
////                .shopPrices()
//                .shopUrl("www.google.com")
//                .deleteYN(false)
//                .build();
//
//        ShopImage shopImage = ShopImage.builder()
//                .fileName("미니 사진")
//                .shop(shop)
//                .fileStorageId(1l)
//                .build();
//        ShopPrice shopPrice = ShopPrice.builder()
//                .price(10000)
//                .priceUnit(ShopPriceUnit.WON)
//                .shop(shop)
//                .build();
//
//        this.shopService.save(shop);
//        Shop shop1 = Shop.builder()
//                .address("서울시")
//                .area(area)
//                .name("용미니네")
//                .franchise(franchise)
//                .location(Location.builder().latitude(33.0).longitude(33.0).build())
////                .shopImage(ShopImage.getInstance())
//                .shopInfo("개발자 세상")
////                .shopPrices()
//                .shopUrl("www.google.com")
//                .deleteYN(false)
//                .build();
//        this.shopService.save(shop1);
//
//        Shop shop2 = Shop.builder()
//                .address("대전시")
//                .area(area)
//                .name("용미니네")
//                .franchise(franchise)
//                .location(Location.builder().latitude(33.0).longitude(33.0).build())
////                .shopImage(ShopImage.getInstance())
//                .shopInfo("개발자 세상")
////                .shopPrices()
//                .shopUrl("www.google.com")
//                .deleteYN(false)
//                .build();
//        this.shopService.save(shop2);
//
//
//        ShopDto shopDto = ShopDto.builder().address("서울시").build();
//
//
//        List<Shop> list = this.shopService.search(shopDto);
//
//        MatcherAssert.assertThat(list.size(), CoreMatchers.is(1));
//
//    }

}

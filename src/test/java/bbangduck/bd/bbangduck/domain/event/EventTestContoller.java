package bbangduck.bd.bbangduck.domain.event;

import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.board.dto.BoardDto;
import bbangduck.bd.bbangduck.domain.board.entity.Board;
import bbangduck.bd.bbangduck.domain.board.entity.enumerate.BoardType;
import bbangduck.bd.bbangduck.domain.event.dto.ShopEventDto;
import bbangduck.bd.bbangduck.domain.board.repository.BoardRepository;
import bbangduck.bd.bbangduck.domain.event.entity.ShopEvent;
import bbangduck.bd.bbangduck.domain.event.repository.ShopEventRepository;
import bbangduck.bd.bbangduck.domain.event.service.EventService;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.shop.dto.ShopDto;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.entity.*;
import bbangduck.bd.bbangduck.domain.shop.entity.enumerate.ShopPriceUnit;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventTestContoller {

    @Autowired
    private EventService eventService;


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminInfoRepository adminInfoRepository;


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    private Member member1;

    private AdminInfo adminInfoSave1;

    @Autowired
    private ShopEventRepository shopEventRepository;

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private FranchiseRepository franchiseRepository;



    @BeforeEach
    public void setup() {
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

    }


//    @Test
    public void eventSaveTest() {
        Board board = Board.builder()
                .adminInfo(adminInfoSave1)
                .content("이벤트 합니당")
                .title("이벤트")
                .type(BoardType.E)
                .writer("용미니")
                .build();
        this.boardRepository.save(board);


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





        Shop shop = Shop.builder()
                .address("서울시")
                .area(area)
                .name("용미니네")
                .franchise(franchise)
                .location(Location.builder().latitude(33.).longitude(33.).build())
//                .shopImage(ShopImage.getInstance())
                .shopInfo("개발자 세상")
//                .shopPrices()
                .shopUrl("www.google.com")
                .deleteYN(false)
                .build();
        ShopImage shopImage = ShopImage.builder()
                .fileName("미니 사진")
                .shop(shop)
                .fileStorageId(1l)
                .build();
        ShopPrice shopPrice = ShopPrice.builder()
                .price(10000)
                .priceUnit(ShopPriceUnit.WON)
                .shop(shop)
                .build();


        this.shopRepository.save(shop);


        this.entityManager.flush();
        this.entityManager.clear();

        String writer = "용미니";

//        Board boardResult = this.boardRepository.findByWriter(writer);

        BoardDto boardDto = BoardDto.builder()
                .writer(writer)
                .build();

        String shopName = "용미니네";
//        Shop shopResult = this.shopRepository.findByName(shopName);
        ShopDto shopDto = ShopDto.builder().name(shopName).build();

        ShopEventDto shopEventDto = ShopEventDto.builder()
//                .boardId(boardResult.getId())
                .startTimes(LocalDateTime.now())
                .endTimes(LocalDateTime.now().plusMonths(1))
                .shopId(shop.getId())
                .build();

        this.eventService.shopEventSave(shopEventDto);

        List<ShopEvent> shopEventRepositoryAll = this.shopEventRepository.findAll();

        System.out.println("=======================");
        shopEventRepositoryAll.stream().forEach(System.out::println);
        System.out.println("=======================");

        MatcherAssert.assertThat(shopEventRepositoryAll.size(), CoreMatchers.is(1));


    }



//    @Test
    public void shopEventUpdateTest() {
        eventSaveTest();
        List<ShopEvent> shopEventRepositoryAll = this.shopEventRepository.findAll();



        ShopEvent oldShopEvent = Optional.of(shopEventRepositoryAll.get(0)).get();

        ShopEventDto newShopEvent = ShopEventDto.builder()
                .endTimes(LocalDateTime.now())
                .startTimes(LocalDateTime.now().plusDays(10))
                .build();


        System.out.println("=======================");
        System.out.println("old : " + oldShopEvent.toString());
        System.out.println("=======================");
        System.out.println("new : " + newShopEvent.toString());
        System.out.println("=======================");


        ShopEvent update = this.eventService.update(oldShopEvent.getId(), newShopEvent);

        ShopEvent shopEvent = this.shopEventRepository.findById(oldShopEvent.getId()).orElseThrow();
        System.out.println(shopEvent.toString());

        MatcherAssert.assertThat(oldShopEvent.getStartTimes(),
                CoreMatchers.not(CoreMatchers.is(shopEvent.getStartTimes())));

    }

    @Test
    public void runningEventTest() {
        eventSaveTest();
        List<ShopEvent> list = this.eventService.runningEvent();

        list.forEach(shopEvent -> shopEvent.toString());


    }


}

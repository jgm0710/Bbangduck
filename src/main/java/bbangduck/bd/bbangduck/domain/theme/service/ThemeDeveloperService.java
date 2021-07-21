package bbangduck.bd.bbangduck.domain.theme.service;

import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.model.emumerate.Activity;
import bbangduck.bd.bbangduck.domain.model.emumerate.Difficulty;
import bbangduck.bd.bbangduck.domain.model.emumerate.HorrorGrade;
import bbangduck.bd.bbangduck.domain.model.emumerate.NumberOfPeople;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.embeded.Location;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import bbangduck.bd.bbangduck.domain.theme.enumerate.ThemeType;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * 개발자 권한을 통해 테마 리소스를 조작하기 위한 비즈니스 로직 구현을 위한 Service
 *
 * @author jgm
 */
@Service
@RequiredArgsConstructor
public class ThemeDeveloperService {

    private final AreaRepository areaRepository;

    private final FranchiseRepository franchiseRepository;

    private final ShopRepository shopRepository;

    private final ThemeRepository themeRepository;

    @Transactional
    public void dummyDataSetUp() {
        if (!themeRepository.findAll().isEmpty()) {
            return;
        }

        Genre rsn1 = Genre.REASONING;
        Genre od1 = Genre.OUTDOOR;
        Genre cmd1 = Genre.COMEDY;

        createThemeSample1(od1);
        createThemeSample2(rsn1);
        createThemeSample3(rsn1);
        createThemeSample4(cmd1);
        createThemeSample5(rsn1);

    }

    private void createThemeSample5(Genre genre) {
        Franchise franchise = Franchise.builder()
                .adminInfo(null)
                .name("비트포비아 미션브레이크")
                .owner(null)
                .ownerTelephone(null)
                .deleteYN(false)
                .build();

        franchiseRepository.save(franchise);

        Area area = Area.builder()
                .code("TMPYS1")
                .name("용산")
                .build();

        areaRepository.save(area);

        Shop shop = Shop.builder()
                .franchise(franchise)
                .shopImage(null)
                .name("비트포비아 미션브레이크 CGV 용산점")
                .shopUrl("https://www.xphobia.net/")
                .shopInfo("비트포비아 미션브레이크 CGV 용산점")
                .shopPrices(null)
                .location(new Location(14133650.372479577, 4513209.6607917305))
                .address("서울 용산구 한강대로23길 55")
                .area(area)
                .deleteYN(false)
                .build();

        shopRepository.save(shop);

        Theme theme = Theme.builder()
                .name("[건대] 방탈출 아카데미 (Master과정)")
                .shop(shop)
                .description("최고 인기 프로그램 tvN 신서유기의 막내 작가인 당신,\n" +
                        "이번 특집은 레전드 특집!\n" +
                        "한창 프로그램 준비에 바쁜 와중,\n" +
                        "신서유기의 마스코트인 신묘한 힘이 사라져버렸다는 소식을 듣게 된다.\n" +
                        "촬영이 시작되기 전에 사라진 묘한이를 찾기 위해,\n" +
                        "당신은 묘한이가 마지막으로 목격되었다는 장소인\n" +
                        "신서유기 대기실로 향하게 되는데 ..")
                .playTime(LocalTime.of(1, 0))
                .difficulty(Difficulty.NORMAL)
                .type(ThemeType.HALF)
                .numberOfPeoples(List.of(NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR))
                .activity(Activity.LITTLE_ACTIVITY)
                .horrorGrade(HorrorGrade.LITTLE_HORROR)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .genre(genre)
                .deleteYN(false)
                .build();

        themeRepository.save(theme);
    }

    private void createThemeSample4(Genre genre) {
        Franchise franchise = Franchise.builder()
                .adminInfo(null)
                .name("넥스트에디션")
                .owner(null)
                .ownerTelephone(null)
                .deleteYN(false)
                .build();

        franchiseRepository.save(franchise);

        Area area = Area.builder()
                .code("TMPGD1")
                .name("건대")
                .build();

        areaRepository.save(area);

        Shop shop = Shop.builder()
                .franchise(franchise)
                .shopImage(null)
                .name("넥스트에디션 건대2호점")
                .shopUrl("https://www.nextedition.co.kr/")
                .shopInfo("넥스트에디션 건대2호점")
                .shopPrices(null)
                .location(new Location(14144808.949181106, 4514864.163597779))
                .address("서울 광진구 아차산로 192")
                .area(area)
                .deleteYN(false)
                .build();

        shopRepository.save(shop);

        Theme theme = Theme.builder()
                .name("[건대] 방탈출 아카데미 (Master과정)")
                .shop(shop)
                .description("방탈출에 입문하고 싶으신 분들,\n" +
                        "방탈출을 잘 하고 싶으신 분들을 위해 준비했습니다.\n" +
                        "방탈출의 정석. 방탈출의 개념원리.\n" +
                        "기출 유형 파악부터 복습까지!\n" +
                        "마감 일주일 전입니다.\n" +
                        "어서 수강신청을 서둘러주세요!\n" +
                        "\n" +
                        "[※ 예약 전 필독 사항 ※]\n" +
                        "* 예약 시, 메모 란에 어떤 과정으로 진행하실 지 적어주세요!\n" +
                        "\n" +
                        "1단계. Normal과정 (난이도 ★★★) – 수정된 기본가이드 + 참고서 (힌트폰 제공 / 힌트 무제한)\n" +
                        "기존의 기본과정의 추가 가이드와 기본 가이드를 합쳐 수정된 기본가이드와 참고서가 함께 지급됩니다.\n" +
                        "방탈출을 처음하는 입문자들 혹은 아직 탈출경험이 적은 방탈출러에게 추천드리는 과정입니다!\n" +
                        "방탈출 입문자들에게도 이해할 수 있을 정도로 쉽게 설명된 참고서와 함께라면\n" +
                        "문제방임에도 불구하고 방탈출의 흥미를 느낄 수 있을 겁니다!\n" +
                        "또한 참고서엔 방탈출의 유형에 대해서도 자세하게 기입되어 있어\n" +
                        "이제 흥미를 느낀 방탈출러에게도 큰 도움이 될 수 있을거라 자부합니다!!\n" +
                        "\n" +
                        "2단계. Hard 과정 (난이도★★★★) – (힌트폰 제공 / 힌트 무제한)\n" +
                        "기존의 기본과정의 추가 가이드와 기본가이드를 합쳐 수정된 기본가이드만 제공됩니다.\n" +
                        "방탈출을 어느정도 해보시고 탈출 경험도 어느정도 있으신 분들에게 추천드리는 과정입니다!\n" +
                        "참고서가 없이 진행되어 다소 난해할 수 있지만 힌트와 함께라면 여러분도 해낼 수 있습니다.\n" +
                        "\n" +
                        "3단계. Master과정 (난이도★★★★★)– (힌트폰 지급X/ 노힌트)\n" +
                        "방탈출을 많이 해보시고 탈출성공은 기본인 분들에게 추천드리는 과정입니다!\n" +
                        "힌트를 사용할 수 있는 힌트폰이 아예 지급되지 않아\n" +
                        "중간에 막히시는 문제가 있으시더라도 스스로 해결하셔야 합니다!\n" +
                        "다른 방탈출 카페에서 랭킹을 도전하실 정도의 실력이시라면 과감하게 도전하셔도 좋습니다!\n" +
                        "\n" +
                        "2. Hard 과정의 랭킹 기준은 [탈출 시간] 이며, Master 과정의 랭킹 기준은 [NO HINT & 80점 이상 획득 & 탈출시간] 입니다.\n" +
                        "3. Master 과정으로 진행하실 경우, 50% 이상 진행하지 못하고 실패 시 남은 문제들에 대한 설명을 해드릴 수 없으니, 신중하게 선택 부탁드립니다.")
                .playTime(LocalTime.of(1, 0))
                .difficulty(Difficulty.VERY_DIFFICULT)
                .type(ThemeType.DEVICE)
                .numberOfPeoples(List.of(NumberOfPeople.TWO, NumberOfPeople.THREE))
                .activity(Activity.LITTLE_ACTIVITY)
                .horrorGrade(HorrorGrade.LITTLE_HORROR)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .deleteYN(false)
                .genre(genre)
                .build();

        themeRepository.save(theme);
    }

    private void createThemeSample3(Genre genre) {
        Franchise franchise = Franchise.builder()
                .adminInfo(null)
                .name("셜록홈즈")
                .owner(null)
                .ownerTelephone(null)
                .deleteYN(false)
                .build();

        franchiseRepository.save(franchise);

        Area area = Area.builder()
                .code("TMPYSN1")
                .name("연신내")
                .build();

        areaRepository.save(area);

        Shop shop = Shop.builder()
                .franchise(franchise)
                .shopImage(null)
                .name("셜록홈즈 연신내점")
                .shopUrl("http://sherlock-holmes.co.kr/")
                .shopInfo("셜록홈즈 연신내점")
                .shopPrices(null)
                .location(new Location(14128510.562346522, 4525780.074472591))
                .address("서울 은평구 연서로29길 16")
                .area(area)
                .deleteYN(false)
                .build();

        shopRepository.save(shop);


        Theme theme = Theme.builder()
                .name("[연신내] 무속인 살인사건")
                .shop(shop)
                .description("TV에 출연할 정도로 유명한 무속인 이 어느 날 싸늘한 시체로 발견? " +
                        "피해자는 TV에 출연하여 화려한 달변과 점술로 유명해진 박수무당으로, " +
                        "어느 날 자신의 점집에서 잘린 머리가 불에 탄 채 단골 고객에게 발견되었다." +
                        " 과연 어떠한 연유로 박무당 은 죽음을 맞이해야 했는가? 빛나는 추리로 진실을 파헤쳐 보자.")
                .playTime(LocalTime.of(1, 0))
                .difficulty(Difficulty.DIFFICULT)
                .type(ThemeType.DEVICE)
                .numberOfPeoples(List.of(NumberOfPeople.THREE, NumberOfPeople.FOUR))
                .activity(Activity.NORMAL)
                .horrorGrade(HorrorGrade.NORMAL)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .genre(genre)
                .deleteYN(false)
                .build();

        themeRepository.save(theme);
    }

    private void createThemeSample2(Genre genre) {
        Franchise franchise = Franchise.builder()
                .adminInfo(null)
                .name("도어 이스케이프 블루 ")
                .owner(null)
                .ownerTelephone(null)
                .deleteYN(false)
                .build();

        franchiseRepository.save(franchise);

        Area area = Area.builder()
                .code("TMPSNH1")
                .name("신논현")
                .build();

        areaRepository.save(area);

        Shop shop = Shop.builder()
                .franchise(franchise)
                .shopImage(null)
                .name("도어 이스케이프 블루 신논현점")
                .shopUrl("http://blue.doorescape.co.kr/")
                .shopInfo("도어 이스케이프 블루 신논현점")
                .shopPrices(null)
                .location(new Location(14140016.522651013, 4509711.059601137))
                .address("서울 서초구 사평대로53길 8-1")
                .area(area)
                .deleteYN(false)
                .build();

        shopRepository.save(shop);


        Theme theme = Theme.builder()
                .name("[신논현] 이방인(일반모드)")
                .shop(shop)
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .playTime(LocalTime.of(2, 0))
                .difficulty(Difficulty.NORMAL)
                .type(ThemeType.HALF)
                .numberOfPeoples(List.of(NumberOfPeople.THREE, NumberOfPeople.FOUR))
                .activity(Activity.VERY_ACTIVITY)
                .horrorGrade(HorrorGrade.LITTLE_HORROR)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .genre(genre)
                .deleteYN(false)
                .build();


        themeRepository.save(theme);
    }

    private void createThemeSample1(Genre genre) {
        Franchise franchise = Franchise.builder()
                .adminInfo(null)
                .name("전국방탈출")
                .owner(null)
                .ownerTelephone(null)
                .deleteYN(false)
                .build();

        franchiseRepository.save(franchise);

        Area area = Area.builder()
                .code("TMPMP1")
                .name("마포")
                .build();

        areaRepository.save(area);

        Shop shop = Shop.builder()
                .name("마포점")
                .franchise(franchise)
                .shopImage(null)
                .name("마포점")
                .shopUrl("https://www.roomescape.co.kr/theme/detail.php?theme=3424")
                .shopInfo(null)
                .shopPrices(null)
                .location(new Location(14128869.857135002, 4516369.540955951))
                .address("서울 마포구 와우산로21길 37")
                .area(area)
                .deleteYN(false)
                .build();

        shopRepository.save(shop);


        Theme theme = Theme.builder()
                .name("[마포] 방린이의 보물찾기")
                .shop(shop)
                .description("항상 방 이야기를 들려주기만 하시던 할아버지가 수수께끼를 내셨다…? 수수께끼를 풀어 보물을 찾아보자!\n" +
                        "(플레이 도중 특정 장소를 방문하는 루트를 포함하고 있으며 평일 오후 2시부터 9시, 주말 오전 11시부터 오후 9시 까지만 플레이할 수 있는 테마입니다.)\n" +
                        "\n" +
                        "예약하기 버튼을 눌러 플레이할 수 있습니다.\n" +
                        "시작위치: 서울 마포구 와우산로21길 37 (홍대입구역 9번 출구로 나와 패션거리를 따라 쭉 내려오다 보면 수노래방과 투썸플레이스, 러쉬, BBQ가 있는 곳)\n" +
                        "\n" +
                        "★ 제한시간이 없는 테마이므로 성공, 실패가 없으니 기록에 연연하지 말고 플레이해주세요.\n" +
                        "★ 후기 작성시엔 걸린시간을 기록하거나, 성공 체크 후 기록 및 힌트를 적지 않은 상태로 내용에 걸린시간을 기재해주세요.")
                .playTime(LocalTime.of(3, 0))
                .difficulty(Difficulty.DIFFICULT)
                .type(ThemeType.DEVICE)
                .numberOfPeoples(List.of(NumberOfPeople.TWO, NumberOfPeople.THREE, NumberOfPeople.FOUR))
                .activity(Activity.VERY_ACTIVITY)
                .horrorGrade(HorrorGrade.LITTLE_HORROR)
                .totalRating(0L)
                .genre(genre)
                .totalEvaluatedCount(0L)
                .deleteYN(false)
                .build();

        themeRepository.save(theme);
    }

    public void addImageToTheme(Theme theme, Long fileStorageId, String fileName) {
        ThemeImage themeImage = ThemeImage.builder()
                .fileStorageId(fileStorageId)
                .fileName(fileName)
                .build();

        theme.setThemeImage(themeImage);
    }
}

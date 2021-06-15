package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberFriendState;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.*;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewDetailAndSurveyCreateDtoRequestDto;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.repository.*;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.entity.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.entity.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.entity.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.ReviewProperties;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URLConnection;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Disabled
public class BaseJGMServiceTest extends BaseTest {

    @Autowired
    protected ReviewLikeService reviewLikeService;

    @Autowired
    protected ReviewPerceivedThemeGenreRepository reviewPerceivedThemeGenreRepository;

    @Autowired
    protected MemberFriendQueryRepository memberFriendQueryRepository;

    @Autowired
    protected ReviewProperties reviewProperties;

    @Autowired
    protected ReviewQueryRepository reviewQueryRepository;

    @Autowired
    protected MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    @Autowired
    protected MemberFriendRepository memberFriendRepository;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected SocialAccountRepository socialAccountRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected SecurityJwtProperties securityJwtProperties;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected ThemeRepository themeRepository;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected FileStorageService fileStorageService;

    @Autowired
    protected MemberProfileImageRepository memberProfileImageRepository;

    @Autowired
    protected GenreRepository genreRepository;

    @Autowired
    protected MemberPlayInclinationRepository memberPlayInclinationRepository;

    @Autowired
    protected ReviewLikeRepository reviewLikeRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected AdminInfoRepository adminInfoRepository;

    @Autowired
    protected AreaRepository areaRepository;

    @Autowired
    protected FranchiseRepository franchiseRepository;

    @Autowired
    protected ShopRepository shopRepository;

    @Autowired
    protected ReviewImageRepository reviewImageRepository;

    protected final String IMAGE_FILE2_CLASS_PATH = "/static/test/bbangduck.jpg";

    protected final String IMAGE_FILE_CLASS_PATH = "/static/test/puppy.jpg";

    protected final String ZIP_FILE_CLASS_PATH = "/static/test/category.zip";

    protected final String HTML_FILE_CLASS_PATH = "/static/test/category.html";

    @BeforeEach
    public void setUp() {
//        deleteAll();
    }

    @AfterEach
    void tearDown() {
        deleteAll();
    }

    private void deleteAll() {
        reviewLikeRepository.deleteAll();
        reviewImageRepository.deleteAll();
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        shopRepository.deleteAll();
        areaRepository.deleteAll();
        franchiseRepository.deleteAll();
        adminInfoRepository.deleteAll();
        memberPlayInclinationRepository.deleteAll();
        memberFriendRepository.deleteAll();
        memberRepository.deleteAll();
    }

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
    }

    @BeforeEach
    public void genreSetUp() {
        Genre horror = Genre.builder()
                .code("HR1")
                .name("공포")
                .build();

        Genre reasoning = Genre.builder()
                .code("RSN1")
                .name("추리")
                .build();

        Genre romance = Genre.builder()
                .code("RMC1")
                .name("로멘스")
                .build();

        Genre crime = Genre.builder()
                .code("CRI1")
                .name("범죄")
                .build();

        Genre adventure = Genre.builder()
                .code("ADVT1")
                .name("모험")
                .build();

        if (genreRepository.findByCode(horror.getCode()).isEmpty()) {
            genreRepository.save(horror);
        }

        if (genreRepository.findByCode(reasoning.getCode()).isEmpty()) {
            genreRepository.save(reasoning);
        }

        if (genreRepository.findByCode(romance.getCode()).isEmpty()) {
            genreRepository.save(romance);
        }

        if (genreRepository.findByCode(crime.getCode()).isEmpty()) {
            genreRepository.save(crime);
        }

        if (genreRepository.findByCode(adventure.getCode()).isEmpty()) {
            genreRepository.save(adventure);
        }

    }

    protected MemberSocialSignUpRequestDto createMemberSignUpRequestDto() {
        return MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("test")
                .socialId("123213")
                .socialType(SocialType.KAKAO)
                .build();
    }


    protected Member createRequestStateFriendToMember(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
        memberSignUpRequestDto.setEmail("notFriend@email.com");
        memberSignUpRequestDto.setNickname("NotFriend");
        memberSignUpRequestDto.setSocialId("333311211");
        Long notFriendId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        Member signUpMember = memberService.getMember(signUpId);
        Member notFriendMember = memberService.getMember(notFriendId);
        MemberFriend memberFriend = MemberFriend.builder()
                .member(signUpMember)
                .friend(notFriendMember)
                .state(MemberFriendState.REQUEST)
                .build();
        MemberFriend savedMemberFriend = memberFriendRepository.save(memberFriend);
        return savedMemberFriend.getFriend();
    }


    protected List<Long> createFriendToMember(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
        Member signUpMember = memberService.getMember(signUpId);
        List<Long> friendIds = new ArrayList<>();
        for (int i = 100; i < 105; i++) {
            memberSignUpRequestDto.setEmail("test" + i + "@email.com");
            memberSignUpRequestDto.setNickname("test" + i);
            memberSignUpRequestDto.setSocialId("33333" + i);
            Long friendMemberId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
            Member friendMember = memberService.getMember(friendMemberId);

            MemberFriend memberFriend = MemberFriend.builder()
                    .member(signUpMember)
                    .friend(friendMember)
                    .state(MemberFriendState.ALLOW)
                    .build();

            MemberFriend savedMemberFriend = memberFriendRepository.save(memberFriend);
            Member savedFriend = savedMemberFriend.getFriend();
            friendIds.add(savedFriend.getId());
        }
        return friendIds;
    }

    protected ReviewCreateDto createReviewCreateDto(List<FileStorage> storedFiles, List<Long> friendIds) {
        List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
        storedFiles.forEach(storedFile -> reviewImageDtoList.add(new ReviewImageDto(storedFile.getId(), storedFile.getFileName())));

        return ReviewCreateDto.builder()
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .build();
    }

    protected List<ReviewImageRequestDto> createReviewImageRequestDtos() throws IOException {
        MockMultipartFile files1 = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId1 = fileStorageService.uploadImageFile(files1);
        FileStorage storedFile1 = fileStorageService.getStoredFile(uploadImageFileId1);

        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadImageFileId2 = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadImageFileId2);

        List<ReviewImageRequestDto> reviewImageRequestDtos = new ArrayList<>();
        reviewImageRequestDtos.add(new ReviewImageRequestDto(storedFile1.getId(), storedFile1.getFileName()));
        reviewImageRequestDtos.add(new ReviewImageRequestDto(storedFile2.getId(), storedFile2.getFileName()));
        return reviewImageRequestDtos;
    }

    protected List<String> createGenreCodes() {
        List<String> genreCodes = new ArrayList<>();
        genreCodes.add("RSN1");
        genreCodes.add("RMC1");
        return genreCodes;
    }

    protected Theme createThemeSample() {

        Member member = createAdminMemberSample();

        AdminInfo adminInfo = createAdminInfoSample(member);

        Franchise franchise = createFranchiseSample(adminInfo);

        Area area = createAreaSample();

        Shop shop = createShopSample(franchise, area);

        Theme theme = Theme.builder()
                .shop(shop)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        Genre rsn1 = genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new);
        theme.addGenre(rsn1);

        return themeRepository.save(theme);
    }

    protected Member createAdminMemberSample() {
        Member member = Member.builder()
                .email("hong@email.com")
                .password("hong")
                .nickname("hong")
                .description("hong")
                .roomEscapeRecordsOpenYN(true)
                .refreshInfo(RefreshInfo.init(securityJwtProperties.getRefreshTokenExpiredDate()))
                .roles(Set.of(MemberRole.ADMIN))
                .build();

        memberRepository.save(member);
        return member;
    }

    protected AdminInfo createAdminInfoSample(Member member) {
        AdminInfo adminInfo = AdminInfo.builder()
                .owner("홍길동")
                .telephone("010-1234-1234")
                .companyNum("1199372787")
                .companyName("활빈당")
                .member(member)
                .deleteYN(false)
                .build();

        adminInfoRepository.save(adminInfo);
        return adminInfo;
    }

    protected Franchise createFranchiseSample(AdminInfo adminInfo) {
        Franchise franchise = Franchise.builder()
                .adminInfo(adminInfo)
                .name("빵덕")
                .owner("빵덕 사장")
                .ownerTelephone("8301382190798")
                .deleteYN(false)
                .build();

        return franchiseRepository.save(franchise);
    }

    protected Shop createShopSample(Franchise franchise, Area area) {
        Shop shop = Shop.builder()
                .franchise(franchise)
                .name("빵덕 샵")
                .shopUrl(null)
                .shopInfo(null)
                .location(null)
                .address("서울 강남구 어딘가")
                .area(area)
                .deleteYN(false)
                .build();

        return shopRepository.save(shop);
    }

    protected Area createAreaSample() {
        Area area = Area.builder()
                .code("GN1")
                .name("강남")
                .build();

        return areaRepository.save(area);
    }

    protected Theme createNotRegisterGenreTheme() {
        Member adminMemberSample = createAdminMemberSample();
        AdminInfo adminInfoSample = createAdminInfoSample(adminMemberSample);
        Franchise franchiseSample = createFranchiseSample(adminInfoSample);
        Area areaSample = createAreaSample();
        Shop shopSample = createShopSample(franchiseSample, areaSample);

        Theme theme = Theme.builder()
                .shop(shopSample)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        return themeRepository.save(theme);
    }

    protected ReviewCreateRequestDto createReviewCreateRequestDto(List<Long> friendIds) {
        return ReviewCreateRequestDto.builder()
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .build();
    }

    protected ReviewSurveyCreateRequestDto createReviewSurveyCreateRequestDto(List<String> genreCodes) {
        return ReviewSurveyCreateRequestDto.builder()
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_BAD)
                .build();
    }

    protected ReviewSurveyUpdateRequestDto createReviewSurveyUpdateRequestDto(List<String> newGenreCodes) {
        return ReviewSurveyUpdateRequestDto.builder()
                .genreCodes(newGenreCodes)
                .perceivedDifficulty(Difficulty.VERY_DIFFICULT)
                .perceivedHorrorGrade(HorrorGrade.VERY_HORROR)
                .perceivedActivity(Activity.VERY_ACTIVITY)
                .scenarioSatisfaction(Satisfaction.VERY_GOOD)
                .interiorSatisfaction(Satisfaction.VERY_GOOD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_GOOD)
                .build();
    }

    protected ReviewUpdateRequestDto createDetailReviewUpdateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return ReviewUpdateRequestDto.builder()
                .reviewType(ReviewType.DETAIL)
                .clearYN(false)
                .clearTime(null)
                .hintUsageCount(3)
                .rating(8)
                .friendIds(friendIds)
                .reviewImages(reviewImageRequestDtos)
                .comment("new comment")
                .build();
    }

    protected ReviewUpdateRequestDto createSimpleReviewUpdateRequestDto(List<Long> friendIds) {
        return ReviewUpdateRequestDto.builder()
                .reviewType(ReviewType.BASE)
                .clearYN(false)
                .clearTime(null)
                .hintUsageCount(3)
                .rating(8)
                .friendIds(friendIds)
                .reviewImages(null)
                .comment(null)
                .build();
    }

    protected Theme createDeletedThemeSample() {
        Member member = createAdminMemberSample();

        AdminInfo adminInfo = createAdminInfoSample(member);

        Franchise franchise = createFranchiseSample(adminInfo);

        Area area = createAreaSample();

        Shop shop = createShopSample(franchise, area);

        Theme theme = Theme.builder()
                .shop(shop)
                .name("이방인")
                .introduction("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeople(NumberOfPeople.FIVE)
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(true)
                .build();

        Genre rsn1 = genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new);
        theme.addGenre(rsn1);

        return themeRepository.save(theme);

    }

    protected ReviewDetailCreateRequestDto createReviewDetailCreateRequestDto(List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return ReviewDetailCreateRequestDto.builder()
                .reviewImages(reviewImageRequestDtos)
                .comment("test review detail comment")
                .build();
    }

    protected ReviewDetailAndSurveyCreateDtoRequestDto createReviewDetailAndSurveyCreateDtoRequestDto(List<ReviewImageRequestDto> reviewImageRequestDtos, List<String> genreCodes) {
        return ReviewDetailAndSurveyCreateDtoRequestDto.builder()
                .reviewImages(reviewImageRequestDtos)
                .comment("test review detail comment")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_BAD)
                .build();
    }

}

package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseTest;
import bbangduck.bd.bbangduck.domain.admin.entity.AdminInfo;
import bbangduck.bd.bbangduck.domain.admin.repository.AdminInfoRepository;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.follow.entity.Follow;
import bbangduck.bd.bbangduck.domain.follow.entity.FollowStatus;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowQueryRepository;
import bbangduck.bd.bbangduck.domain.follow.repository.FollowRepository;
import bbangduck.bd.bbangduck.domain.genre.Genre;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.enbeded.RefreshInfo;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRole;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.*;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.dto.service.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewImageRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewLikeRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewQueryRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.review.service.ReviewApplicationService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.domain.shop.entity.Area;
import bbangduck.bd.bbangduck.domain.shop.entity.Franchise;
import bbangduck.bd.bbangduck.domain.shop.entity.Shop;
import bbangduck.bd.bbangduck.domain.shop.repository.AreaRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.FranchiseRepository;
import bbangduck.bd.bbangduck.domain.shop.repository.ShopRepository;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemePlayMemberRepository;
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
    protected ThemePlayMemberRepository themePlayMemberRepository;

    @Autowired
    protected MemberQueryRepository memberQueryRepository;

    @Autowired
    protected ReviewLikeService reviewLikeService;

    @Autowired
    protected FollowQueryRepository followQueryRepository;

    @Autowired
    protected ReviewProperties reviewProperties;

    @Autowired
    protected ReviewQueryRepository reviewQueryRepository;

    @Autowired
    protected MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    @Autowired
    protected FollowRepository followRepository;

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

    @Autowired
    protected ReviewApplicationService reviewApplicationService;

    @Autowired
    protected ThemeAnalysisRepository themeAnalysisRepository;

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
        themePlayMemberRepository.deleteAll();
        themeAnalysisRepository.deleteAll();
        themeRepository.deleteAll();
        shopRepository.deleteAll();
        areaRepository.deleteAll();
        franchiseRepository.deleteAll();
        adminInfoRepository.deleteAll();
        memberPlayInclinationRepository.deleteAll();
        followRepository.deleteAll();
        memberRepository.deleteAll();
    }

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
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
        Follow follow = Follow.builder()
                .followingMember(signUpMember)
                .followedMember(notFriendMember)
                .build();
        Follow savedFollow = followRepository.save(follow);
        return savedFollow.getFollowedMember();
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

            Follow follow1 = Follow.builder()
                    .followingMember(signUpMember)
                    .followedMember(friendMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow follow2 = Follow.builder()
                    .followingMember(friendMember)
                    .followedMember(signUpMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow savedFollow = followRepository.save(follow1);
            followRepository.save(follow2);
            Member savedFriend = savedFollow.getFollowedMember();
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
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
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

    protected List<Genre> createPerceivedThemeGenres() {
        return List.of(Genre.ACTION, Genre.CRIME);
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
                .genre(Genre.ACTION)
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .deleteYN(false)
                .build();

        return themeRepository.save(theme);
    }

    protected Member createAdminMemberSample() {
        Member member = Member.builder()
                .email("hong@email.com")
                .password("hong")
                .nickname("hong")
                .description("hong")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.OPEN)
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
                .location(null)
                .address("서울 강남구 어딘가")
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
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .totalRating(0L)
                .totalEvaluatedCount(0L)
                .build();

        return themeRepository.save(theme);
    }

    protected ReviewCreateRequestDto createReviewCreateRequestDto(List<Long> friendIds) {
        return ReviewCreateRequestDto.builder()
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                .rating(6)
                .friendIds(friendIds)
                .build();
    }

    protected ReviewSurveyCreateRequestDto createReviewSurveyCreateRequestDto(List<Genre> perceivedThemeGenres) {
        return ReviewSurveyCreateRequestDto.builder()
                .perceivedThemeGenres(perceivedThemeGenres)
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
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
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
                .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
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
                .genre(Genre.ACTION)
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(true)
                .build();

        return themeRepository.save(theme);

    }

    protected ReviewDetailCreateRequestDto createReviewDetailCreateRequestDto(List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return ReviewDetailCreateRequestDto.builder()
                .reviewImages(reviewImageRequestDtos)
                .comment("test review detail comment")
                .build();
    }

    protected ReviewDetailAndSurveyCreateDtoRequestDto createReviewDetailAndSurveyCreateDtoRequestDto(List<ReviewImageRequestDto> reviewImageRequestDtos, List<Genre> perceivedThemeGenres) {
        return ReviewDetailAndSurveyCreateDtoRequestDto.builder()
                .reviewImages(reviewImageRequestDtos)
                .comment("test review detail comment")
                .perceivedThemeGenres(perceivedThemeGenres)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.GOOD)
                .interiorSatisfaction(Satisfaction.BAD)
                .problemConfigurationSatisfaction(Satisfaction.VERY_BAD)
                .build();
    }

}

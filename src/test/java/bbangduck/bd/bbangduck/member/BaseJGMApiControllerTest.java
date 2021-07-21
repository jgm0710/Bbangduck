package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
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
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationQueryRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.controller.ReviewValidator;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.*;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewHintUsageCount;
import bbangduck.bd.bbangduck.domain.review.enumerate.ReviewType;
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
import bbangduck.bd.bbangduck.domain.theme.entity.ThemeImage;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeAnalysisRepository;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
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
import java.util.Random;
import java.util.Set;

@Disabled
public class BaseJGMApiControllerTest extends BaseControllerTest {

    @Autowired
    protected ReviewValidator reviewValidator;

    @Autowired
    protected ReviewQueryRepository reviewQueryRepository;

    @Autowired
    protected MemberPlayInclinationQueryRepository memberPlayInclinationQueryRepository;

    @Autowired
    protected FollowQueryRepository followQueryRepository;

    @Autowired
    protected ReviewProperties reviewProperties;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected ReviewLikeService reviewLikeService;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected ReviewLikeRepository reviewLikeRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected SecurityJwtProperties securityJwtProperties;

    @Autowired
    protected FileStorageService fileStorageService;

    @Autowired
    protected AdminInfoRepository adminInfoRepository;

    @Autowired
    protected FileStorageProperties fileStorageProperties;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected ThemeRepository themeRepository;

    @Autowired
    protected MemberPlayInclinationRepository memberPlayInclinationRepository;

    @Autowired
    protected FollowRepository followRepository;

    @Autowired
    protected AreaRepository areaRepository;

    @Autowired
    protected FranchiseRepository franchiseRepository;

    @Autowired
    protected ShopRepository shopRepository;

    @Autowired
    protected ReviewApplicationService reviewApplicationService;

    @Autowired
    protected ThemeAnalysisRepository themeAnalysisRepository;

    protected static int REFRESH_TOKEN_EXPIRED_DATE;

    protected static String JWT_TOKEN_HEADER_DESCRIPTION = "리소스 접근 시 회원 인증을 위해 필요한 JWT 토큰 인증 헤더";

    protected static String STATUS_DESCRIPTION = "요청에 대해 HttpStatus 외에 별도로 응답 상태를 구분하기 위한 상태 값";

    protected static String MESSAGE_DESCRIPTION = "요청에 따른 응답에 대한 간단한 Message";

    protected final String IMAGE_FILE_CLASS_PATH = "/static/test/puppy.jpg";

    protected final String ZIP_FILE_CLASS_PATH = "/static/test/category.zip";

    protected final String HTML_FILE_CLASS_PATH = "/static/test/category.html";

    protected final String IMAGE_FILE2_CLASS_PATH = "/static/test/bbangduck.jpg";

    protected String OBJECT_NAME_DESCRIPTION = "예외가 발생한 객체의 이름";

    protected String CODE_DESCRIPTION = "예외 코드";

    protected String DEFAULT_MESSAGE_DESCRIPTION = "발생한 예외에 대한 메세지";

    protected String FIELD_DESCRIPTION = "예외가 발생한 필드의 이름";


    @BeforeEach
    public void setUp() {
        REFRESH_TOKEN_EXPIRED_DATE = securityJwtProperties.getRefreshTokenExpiredDate();
        deleteAll();
    }

    protected void deleteAll() {
        reviewLikeRepository.deleteAll();
        reviewRepository.deleteAll();
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

    @AfterEach
    void tearDown() {
        deleteAll();
    }

    protected MockMultipartFile createMockMultipartFile(String paramName, String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        String filename = classPathResource.getFilename();
        String contentType = URLConnection.guessContentTypeFromName(filename);

        return new MockMultipartFile(paramName, filename, contentType, classPathResource.getInputStream());
    }

    protected MemberSocialSignUpRequestDto createMemberSocialSignUpRequestDto() {
        return MemberSocialSignUpRequestDto.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .socialType(SocialType.KAKAO)
                .socialId("3123213")
                .build();
    }

    protected Member createOneWayFollowMember(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
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


    protected List<Long> createTwoWayFollowMembers(MemberSocialSignUpRequestDto memberSignUpRequestDto, Long signUpId) {
        Member followingMember = memberService.getMember(signUpId);
        List<Long> followedMemberIds = new ArrayList<>();
        for (int i = 100; i < 105; i++) {
            memberSignUpRequestDto.setEmail("test" + i + "@email.com");
            memberSignUpRequestDto.setNickname("test" + i);
            memberSignUpRequestDto.setSocialId("33333" + i);
            Long friendMemberId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
            Member followedMember = memberService.getMember(friendMemberId);

            Follow follow1 = Follow.builder()
                    .followingMember(followingMember)
                    .followedMember(followedMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow follow2 = Follow.builder()
                    .followingMember(followedMember)
                    .followedMember(followingMember)
                    .status(FollowStatus.TWO_WAY_FOLLOW)
                    .build();

            Follow savedFollow = followRepository.save(follow1);
            followRepository.save(follow2);

            Member savedFollowedMember = savedFollow.getFollowedMember();
            followedMemberIds.add(savedFollowedMember.getId());
        }
        return followedMemberIds;
    }

    protected List<Genre> createPerceivedThemeGenres() {
        return List.of(Genre.ACTION, Genre.COMEDY, Genre.DRAMA);
    }

    protected Theme createThemeSample() throws IOException {
        Member member = createAdminMemberSample();

        AdminInfo adminInfo = createAdminInfoSample(member);

        Franchise franchise = createFranchiseSample(adminInfo);

        Area area = createAreaSample();

        Shop shop = createShopSample(franchise, area);

        Theme theme = Theme.builder()
                .shop(shop)
                .name("이방인")
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .totalEvaluatedCount(0L)
                .totalRating(0L)
                .genre(Genre.ACTION)
                .build();

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadImageFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadImageFileId);
        ThemeImage themeImage = ThemeImage.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();
        theme.setThemeImage(themeImage);

        return themeRepository.save(theme);
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
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(true)
                .genre(Genre.ACTION)
                .build();

        return themeRepository.save(theme);

    }

    protected Member createAdminMemberSample() {
        Member member = Member.builder()
                .email("hong@email.com")
                .password("hong")
                .nickname("hong")
                .description("hong")
                .roomEscapeRecodesOpenStatus(MemberRoomEscapeRecodesOpenStatus.CLOSE)
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
        Theme theme = Theme.builder()
                .shop(null)
                .name("이방인")
                .description("\" Loading...80%\n" +
                        "분명 시험이 끝난 기념으로 술을 마시고 있었는데...여긴 어디지!? \"")
                .numberOfPeoples(List.of(NumberOfPeople.FIVE))
                .difficulty(Difficulty.NORMAL)
                .activity(Activity.LITTLE_ACTIVITY)
                .playTime(LocalTime.of(1, 0))
                .deleteYN(false)
                .build();

        return themeRepository.save(theme);
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

    protected ReviewCreateRequestDto createReviewCreateRequestDto(List<Long> friendIds) {
        return ReviewCreateRequestDto.builder()
//                .reviewType(ReviewType.BASE)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(ReviewHintUsageCount.ONE)
                .rating(3)
                .friendIds(friendIds)
                .build();
    }

//    protected ReviewCreateRequestDto createDetailReviewCreateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos) {
//        return ReviewCreateRequestDto.builder()
//                .reviewType(ReviewType.DETAIL)
//                .clearYN(true)
//                .clearTime(LocalTime.of(0, 45, 11))
//                .hintUsageCount(ReviewHintUsageCount.ONE)
//                .rating(6)
//                .friendIds(friendIds)
//                .reviewImages(reviewImageRequestDtos)
//                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
//                .build();
//    }

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
                .rating(4)
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
                .rating(4)
                .friendIds(friendIds)
                .reviewImages(null)
                .comment(null)
                .build();
    }

    protected String createMacroComment() {
        return "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro \n" +
                "this is macro ";
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

    protected void createReviewSampleList(Long memberId, List<Long> friendIds, Long themeId) throws IOException {
        List<Genre> perceivedThemeGenres = createPerceivedThemeGenres();
        for (int i = 0; i < 30; i++) {
            boolean clearYN = true;
            LocalTime clearTime = LocalTime.of(0, new Random().nextInt(15) + 30, new Random().nextInt(59));
            if (i % 2 == 0) {
                clearYN = false;
                clearTime = null;
            }

            ReviewCreateRequestDto reviewCreateRequestDto = ReviewCreateRequestDto.builder()
                    .clearYN(clearYN)
                    .clearTime(clearTime)
                    .hintUsageCount(ReviewHintUsageCount.THREE_OR_MORE)
                    .rating(new Random().nextInt(8) + 2)
                    .friendIds(friendIds)
                    .build();

            Long reviewId = reviewApplicationService.createReview(memberId, themeId, reviewCreateRequestDto.toServiceDto());

            if (i % 2 == 0) {
                List<ReviewImageRequestDto> reviewImageRequestDtos = createReviewImageRequestDtos();
                ReviewDetailCreateRequestDto reviewDetailCreateRequestDto = createReviewDetailCreateRequestDto(reviewImageRequestDtos);
                reviewApplicationService.addDetailToReview(reviewId, memberId,reviewDetailCreateRequestDto.toServiceDto());
            }

            if (i % 4 == 0) {
                ReviewSurveyCreateRequestDto reviewSurveyCreateRequestDto = createReviewSurveyCreateRequestDto(perceivedThemeGenres);
                reviewApplicationService.addSurveyToReview(reviewId, memberId, reviewSurveyCreateRequestDto.toServiceDto());
            }

            if (i % 3 == 0) {
                reviewService.deleteReview(reviewId);
            }
        }
    }
}

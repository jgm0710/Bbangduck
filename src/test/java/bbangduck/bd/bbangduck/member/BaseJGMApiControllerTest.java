package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.common.BaseControllerTest;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.service.FileStorageService;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.repository.GenreRepository;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberFriend;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.MemberFriendState;
import bbangduck.bd.bbangduck.domain.member.entity.enumerate.SocialType;
import bbangduck.bd.bbangduck.domain.member.repository.MemberFriendRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberPlayInclinationRepository;
import bbangduck.bd.bbangduck.domain.member.repository.MemberRepository;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.model.emumerate.*;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.review.controller.dto.ReviewImageRequestDto;
import bbangduck.bd.bbangduck.domain.review.entity.enumerate.ReviewType;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewLikeRepository;
import bbangduck.bd.bbangduck.domain.review.repository.ReviewRepository;
import bbangduck.bd.bbangduck.domain.review.service.ReviewLikeService;
import bbangduck.bd.bbangduck.domain.review.service.ReviewService;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewCreateDto;
import bbangduck.bd.bbangduck.domain.review.service.dto.ReviewImageDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.domain.theme.repository.ThemeRepository;
import bbangduck.bd.bbangduck.global.config.properties.FileStorageProperties;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.URLConnection;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Disabled
public class BaseJGMApiControllerTest extends BaseControllerTest {

    @Autowired
    protected ReviewLikeService reviewLikeService;

    @Autowired
    protected ReviewService reviewService;

    @Autowired
    protected ReviewLikeRepository reviewLikeRepository;

    @Autowired
    protected GenreRepository genreRepository;

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
    protected MemberFriendRepository memberFriendRepository;

    protected final List<String> REVIEW_TYPE_ENUM_LIST = Stream.of(ReviewType.values()).map(Enum::name).collect(Collectors.toList());
    protected final List<String> DIFFICULTY_ENUM_LIST = Stream.of(Difficulty.values()).map(Enum::name).collect(Collectors.toList());
    protected final List<String> HORROR_GRADE_ENUM_LIST = Stream.of(HorrorGrade.values()).map(Enum::name).collect(Collectors.toList());
    protected final List<String> ACTIVITY_ENUM_LIST = Stream.of(Activity.values()).map(Enum::name).collect(Collectors.toList());
    protected final List<String> SATISFACTION_ENUM_LIST = Stream.of(Satisfaction.values()).map(Enum::name).collect(Collectors.toList());

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

        reviewLikeRepository.deleteAll();
        reviewRepository.deleteAll();
        themeRepository.deleteAll();
        memberPlayInclinationRepository.deleteAll();
        memberFriendRepository.deleteAll();
        memberRepository.deleteAll();
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

    protected ReviewCreateDto createReviewCreateDto(List<FileStorage> storedFiles, List<Long> friendIds, List<String> genreCodes) {
        List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
        storedFiles.forEach(storedFile -> reviewImageDtoList.add(new ReviewImageDto(storedFile.getId(), storedFile.getFileName())));

        return ReviewCreateDto.builder()
                .reviewType(ReviewType.DEEP)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageDtoList)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.NORMAL)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.BAD)
                .build();
    }

    protected List<String> createGenreCodes() {
        List<String> genreCodes = new ArrayList<>();
        genreCodes.add("RSN1");
        genreCodes.add("RMC1");
        return genreCodes;
    }

    protected Theme createTheme() {
        Theme theme = Theme.builder()
                .shop(null)
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

    protected Theme createNotRegisterGenreTheme() {
        Theme theme = Theme.builder()
                .shop(null)
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


    protected ReviewCreateRequestDto createDeepReviewCreateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos, List<String> genreCodes) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.DEEP)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageRequestDtos)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .genreCodes(genreCodes)
                .perceivedDifficulty(Difficulty.EASY)
                .perceivedHorrorGrade(HorrorGrade.LITTLE_HORROR)
                .perceivedActivity(Activity.NORMAL)
                .scenarioSatisfaction(Satisfaction.NORMAL)
                .interiorSatisfaction(Satisfaction.GOOD)
                .problemConfigurationSatisfaction(Satisfaction.BAD)
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

    protected ReviewCreateRequestDto createSimpleReviewCreateRequestDto(List<Long> friendIds) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.SIMPLE)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .build();
    }

    protected ReviewCreateRequestDto createDetailReviewCreateRequestDto(List<Long> friendIds, List<ReviewImageRequestDto> reviewImageRequestDtos) {
        return ReviewCreateRequestDto.builder()
                .reviewType(ReviewType.DETAIL)
                .clearYN(true)
                .clearTime(LocalTime.of(0, 45, 11))
                .hintUsageCount(1)
                .rating(6)
                .friendIds(friendIds)
                .reviewImages(reviewImageRequestDtos)
                .comment("2인. 입장전에 해주신 설명에대한 믿음으로 함정에빠져버림..\n 일반모드로 하실분들은 2인이 최적입니다.")
                .build();
    }

}

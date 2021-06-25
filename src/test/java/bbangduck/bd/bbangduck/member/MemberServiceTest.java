package bbangduck.bd.bbangduck.member;

import bbangduck.bd.bbangduck.domain.auth.dto.controller.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.file.entity.FileStorage;
import bbangduck.bd.bbangduck.domain.file.exception.StoredFileNotFoundException;
import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.domain.genre.exception.GenreNotFoundException;
import bbangduck.bd.bbangduck.domain.member.dto.service.MemberProfileImageDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.entity.MemberPlayInclination;
import bbangduck.bd.bbangduck.domain.member.entity.MemberProfileImage;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberRoomEscapeRecodesOpenStatus;
import bbangduck.bd.bbangduck.domain.member.enumerate.MemberSearchKeywordType;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNicknameDuplicateException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberNotFoundException;
import bbangduck.bd.bbangduck.domain.member.exception.MemberProfileImageNotFoundException;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.domain.review.dto.controller.request.ReviewCreateRequestDto;
import bbangduck.bd.bbangduck.domain.theme.entity.Theme;
import bbangduck.bd.bbangduck.global.config.properties.MemberProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("회원(프로필 정보) 관련 Service 로직 테스트")
class MemberServiceTest extends BaseJGMServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberProperties memberProperties;

    @Test
    @DisplayName("회원 조회 테스트")
    @Transactional
    public void getMemberTest() {
        //given
        Member member = Member.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .build();

        Member save = memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        Member findMember = memberService.getMember(save.getId());
        System.out.println("findMember = " + findMember.toString());

        //then
        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getEmail(), member.getEmail());
        assertEquals(findMember.getNickname(), member.getNickname());
    }

    @Test
    @DisplayName("회원 조회 시 회원을 찾을 수 없는 경우")
    public void getMember_NotFound() {
        //given

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(10000L));
    }

    @Test
    @DisplayName("회원 프로필 이미지 변경 - 기존에 프로필 이미지가 없었을 경우")
    public void updateProfileImage_ProfileImageNotExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getProfileImage(), "처음 회원가입 한 회원의 프로필 이미지는 null 이어야 한다.");

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = MemberProfileImageDto.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        //then
        Member findMember = memberService.getMember(signUpId);

        assertNotNull(findMember.getProfileImage(), "프로필 이미지 변경 이후 프로필 이미지는 null 이 아니어야 한다.");
        MemberProfileImage memberProfileImage = findMember.getProfileImage();
        assertEquals(storedFile.getId(), memberProfileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), memberProfileImage.getFileName());

    }

    @Test
    @DisplayName("회원 프로필 이미지 변경 - 기존 프로필 이미지가 있었을 경우")
    @Transactional
    public void updateProfileImage_ProfileImageExist() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getProfileImage(), "처음 회원가입 한 회원의 프로필 이미지는 null 이어야 한다.");

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = MemberProfileImageDto.builder()
                .fileStorageId(storedFile.getId())
                .fileName(storedFile.getFileName())
                .build();

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        em.flush();
        em.clear();
        //then
        Member findMember = memberService.getMember(signUpId);

        assertNotNull(findMember.getProfileImage(), "프로필 이미지 변경 이후 프로필 이미지는 null 이 아니어야 한다.");
        MemberProfileImage memberProfileImage = findMember.getProfileImage();
        assertEquals(storedFile.getId(), memberProfileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), memberProfileImage.getFileName());

        em.flush();
        em.clear();

        //given
        MockMultipartFile files2 = createMockMultipartFile("files", IMAGE_FILE2_CLASS_PATH);
        Long uploadedFileId2 = fileStorageService.uploadImageFile(files2);
        FileStorage storedFile2 = fileStorageService.getStoredFile(uploadedFileId2);

        assertNotEquals(storedFile.getId(), storedFile2.getId());

        MemberProfileImageDto memberProfileImageDto2 = new MemberProfileImageDto(storedFile2.getId(), storedFile2.getFileName());

        //when
        memberService.updateProfileImage(signUpId, memberProfileImageDto2);
        em.flush();
        em.clear();

        //then
        assertThrows(StoredFileNotFoundException.class, () -> fileStorageService.getStoredFile(storedFile.getId()));

        Member findMember2 = memberService.getMember(signUpId);
        MemberProfileImage memberProfileImage2 = findMember2.getProfileImage();

        assertEquals(memberProfileImage.getId(), memberProfileImage2.getId());
        assertNotEquals(memberProfileImage.getFileStorageId(), memberProfileImage2.getFileStorageId());
        assertNotEquals(memberProfileImage.getFileName(), memberProfileImage2.getFileName());

        assertEquals(storedFile2.getId(), memberProfileImage2.getFileStorageId());
        assertEquals(storedFile2.getFileName(), memberProfileImage2.getFileName());

    }

    @Test
    @DisplayName("프로필 이미지 수정 시 회원을 찾을 수 없는 경우")
    public void updateProfileImage_MemberNotFound() throws Exception {
        //given
        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadedFileId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadedFileId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateProfileImage(10000L, memberProfileImageDto));

    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제")
    public void deleteProfileImage() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when
        memberService.deleteProfileImage(signUpId);

        //then
        Member findMember = memberService.getMember(signUpId);

        assertNull(findMember.getProfileImage(), "프로필 이미지 삭제 이후 회원의 프로필 이미지는 null 이어야 한다.");
        assertTrue(memberProfileImageRepository.findById(profileImageId).isEmpty());


    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제 - 회원을 찾을 수 없는 경우")
    public void deleteProfileImage_MemberNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.deleteProfileImage(10000L));


    }

    @Test
    @DisplayName("회원 프로필 이미지 삭제 - 회원 프로필 이미지를 찾을 수 없는 경우")
    public void deleteProfileImage_MemberProfileImageNotFound() throws Exception {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        MockMultipartFile files = createMockMultipartFile("files", IMAGE_FILE_CLASS_PATH);
        Long uploadId = fileStorageService.uploadImageFile(files);
        FileStorage storedFile = fileStorageService.getStoredFile(uploadId);

        MemberProfileImageDto memberProfileImageDto = new MemberProfileImageDto(storedFile.getId(), storedFile.getFileName());

        memberService.updateProfileImage(signUpId, memberProfileImageDto);

        Member savedMember = memberService.getMember(signUpId);
        MemberProfileImage profileImage = savedMember.getProfileImage();
        assertNotNull(profileImage);
        assertEquals(storedFile.getId(), profileImage.getFileStorageId());
        assertEquals(storedFile.getFileName(), profileImage.getFileName());

        Long profileImageId = profileImage.getId();

        //when
        memberService.deleteProfileImage(signUpId);

        //then
        assertThrows(MemberProfileImageNotFoundException.class, () -> memberService.deleteProfileImage(signUpId));

    }

    @Test
    @DisplayName("회원 닉네임 변경")
    public void updateNickname() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when
        String newNickname = "updateNewNickname";
        memberService.updateNickname(signUpId, newNickname);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertEquals(newNickname, findMember.getNickname());

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 회원을 찾을 수 없는 경우")
    public void updateNickname_MemberNotFound() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "updateNewNickname";
        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateNickname(10000L, newNickname));

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 닉네임을 변경하지 않은 경우")
    public void updateNickname_NicknameNotUpdated() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId1 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "test2";
        memberSignUpRequestDto.setEmail("test2@email.com");
        memberSignUpRequestDto.setNickname(newNickname);
        memberSignUpRequestDto.setSocialId("3322113");
        Long signUpId2 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when
        memberService.updateNickname(signUpId2, newNickname);

        //then
        Member findMember = memberService.getMember(signUpId2);
        assertEquals(newNickname, findMember.getNickname());

    }

    @Test
    @DisplayName("회원 닉네임 변경 - 다른 회원의 닉네임과 중복되는 경우")
    public void updateNickname_NicknameDuplicate() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId1 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        String newNickname = "test2";
        memberSignUpRequestDto.setEmail("test2@email.com");
        memberSignUpRequestDto.setNickname(newNickname);
        memberSignUpRequestDto.setSocialId("3322113");
        Long signUpId2 = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());

        //when

        //then
        assertThrows(MemberNicknameDuplicateException.class, () -> memberService.updateNickname(signUpId1, newNickname));

    }

    @Test
    @DisplayName("회원 자기소개 변경")
    public void updateDescription() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getDescription(), "회원가입 직후 회원의 자기소개는 null 이어야 한다.");

        String newDescription = "새로운 자기 소개";

        //when
        memberService.updateDescription(signUpId, newDescription);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertEquals(newDescription, findMember.getDescription());

    }

    @Test
    @DisplayName("회원 자기소개 변경 - 회원을 찾을 수 없는 경우")
    public void updateDescription_MemberNotFound() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertNull(savedMember.getDescription(), "회원가입 직후 회원의 자기소개는 null 이어야 한다.");

        String newDescription = "새로운 자기 소개";

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateDescription(10000L, newDescription));

    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - true to false")
    public void toggleRoomEscapeRecodesOpenYN_TrueToFalse() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertEquals(MemberRoomEscapeRecodesOpenStatus.OPEN, savedMember.getRoomEscapeRecodesOpenStatus());

        //when
        memberService.updateRoomEscapeRecodesOpenStatus(signUpId, MemberRoomEscapeRecodesOpenStatus.CLOSE);

        //then
        Member findMember = memberService.getMember(signUpId);
        assertEquals(MemberRoomEscapeRecodesOpenStatus.CLOSE, findMember.getRoomEscapeRecodesOpenStatus());

    }

    @Test
    @DisplayName("회원 방탈출 기록 공개 여부 변경 - 회원을 찾을 수 없는 경우")
    public void toggleRoomEscapeRecodesOpenYN_MemberNotFound() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(signUpId);
        assertEquals(MemberRoomEscapeRecodesOpenStatus.OPEN, savedMember.getRoomEscapeRecodesOpenStatus());

        //when

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.updateRoomEscapeRecodesOpenStatus(10000L, MemberRoomEscapeRecodesOpenStatus.CLOSE));

    }

    @Test
    @DisplayName("회원의 방탈출 성향 상위 N 개 목록 조회")
    public void getMemberPlayInclinationTopN() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member member = memberService.getMember(signUpId);

        memberSignUpRequestDto.setEmail("member2@email.colm");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("318209381290");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member member2 = memberService.getMember(member2Id);

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);

        List<Genre> genres = new ArrayList<>();
        genres.add(genreRepository.findByCode("HR1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("RMC1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("CRI1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("ADVT1").orElseThrow(GenreNotFoundException::new));

        genres.forEach(genre -> {
            MemberPlayInclination memberPlayInclination = MemberPlayInclination.builder()
                    .member(member)
                    .genre(genre)
                    .playCount(new Random().nextInt(8) + 1)
                    .build();
            memberPlayInclinationRepository.save(memberPlayInclination);

            MemberPlayInclination member2PlayInclination = MemberPlayInclination.builder()
                    .member(member2)
                    .genre(genre)
                    .playCount(new Random().nextInt(8) + 1)
                    .build();
            memberPlayInclinationRepository.save(member2PlayInclination);
        });

        //when
        System.out.println("======================================================================================================");
        List<MemberPlayInclination> memberPlayInclinationTopN = memberService.getMemberPlayInclinationTopN(member.getId());
        System.out.println("======================================================================================================");

        //then
        memberPlayInclinationTopN.forEach(memberPlayInclination -> {
            Member memberPlayInclinationMember = memberPlayInclination.getMember();
            Genre memberPlayInclinationGenre = memberPlayInclination.getGenre();
            System.out.println("memberPlayInclinationMember = " + memberPlayInclinationMember);
            System.out.println("memberPlayInclinationGenre = " + memberPlayInclinationGenre);
            System.out.println("memberPlayInclination.getPlayCount() = " + memberPlayInclination.getPlayCount());

            assertEquals(member.getId(), memberPlayInclinationMember.getId(), "회원 1의 플레이 성향만 조회되어야 한다.");
        });

        for (int i = 0; i < memberPlayInclinationTopN.size()-1; i++) {
            MemberPlayInclination nowPlayInclination = memberPlayInclinationTopN.get(i);
            MemberPlayInclination nextPlayInclination = memberPlayInclinationTopN.get(i + 1);

            assertTrue(nowPlayInclination.getPlayCount() >= nextPlayInclination.getPlayCount(), "플레이 수 가 많은 성향부터 내림차순으로 정렬되어 있어야 한다.");
        }

        assertEquals(memberProperties.getPlayInclinationTopLimit(), memberPlayInclinationTopN.size(), "조회된 회원 성향의 개수는 지정된 개수가 나와야 한다.");
    }

    @Test
    @DisplayName("회원의 전체 방탈출 성향 조회")
    public void getMemberPlayInclinations() {
        //given
        MemberSocialSignUpRequestDto memberSignUpRequestDto = createMemberSignUpRequestDto();
        Long signUpId = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member member = memberService.getMember(signUpId);

        memberSignUpRequestDto.setEmail("member2@email.colm");
        memberSignUpRequestDto.setNickname("member2");
        memberSignUpRequestDto.setSocialId("318209381290");
        Long member2Id = authenticationService.signUp(memberSignUpRequestDto.toServiceDto());
        Member member2 = memberService.getMember(member2Id);

        Theme themeSample = createThemeSample();

        ReviewCreateRequestDto reviewCreateRequestDto = createReviewCreateRequestDto(null);

        List<Genre> genres = new ArrayList<>();
        genres.add(genreRepository.findByCode("HR1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("RMC1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("RSN1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("CRI1").orElseThrow(GenreNotFoundException::new));
        genres.add(genreRepository.findByCode("ADVT1").orElseThrow(GenreNotFoundException::new));

        genres.forEach(genre -> {
            MemberPlayInclination memberPlayInclination = MemberPlayInclination.builder()
                    .member(member)
                    .genre(genre)
                    .playCount(new Random().nextInt(8) + 1)
                    .build();
            memberPlayInclinationRepository.save(memberPlayInclination);

            MemberPlayInclination member2PlayInclination = MemberPlayInclination.builder()
                    .member(member2)
                    .genre(genre)
                    .playCount(new Random().nextInt(8) + 1)
                    .build();
            memberPlayInclinationRepository.save(member2PlayInclination);
        });

        //when
        System.out.println("======================================================================================================");
        List<MemberPlayInclination> memberPlayInclinations = memberService.getMemberPlayInclinations(member.getId());
        System.out.println("======================================================================================================");

        //then
        memberPlayInclinations.forEach(memberPlayInclination -> {
            Member memberPlayInclinationMember = memberPlayInclination.getMember();
            Genre memberPlayInclinationGenre = memberPlayInclination.getGenre();
            System.out.println("memberPlayInclinationMember = " + memberPlayInclinationMember);
            System.out.println("memberPlayInclinationGenre = " + memberPlayInclinationGenre);
            System.out.println("memberPlayInclination.getPlayCount() = " + memberPlayInclination.getPlayCount());

            assertEquals(member.getId(), memberPlayInclinationMember.getId(), "회원 1의 플레이 성향만 조회되어야 한다.");
        });

        for (int i = 0; i < memberPlayInclinations.size()-1; i++) {
            MemberPlayInclination nowPlayInclination = memberPlayInclinations.get(i);
            MemberPlayInclination nextPlayInclination = memberPlayInclinations.get(i + 1);

            assertTrue(nowPlayInclination.getPlayCount() >= nextPlayInclination.getPlayCount(), "플레이 수 가 많은 성향부터 내림차순으로 정렬되어 있어야 한다.");
        }


    }

    @Test
    @DisplayName("회원 검색 - 회원을 찾을 수 없는 경우")
    public void searchMember_MemberNotFound() {
        //given
        MemberSearchKeywordType memberSearchKeywordType = MemberSearchKeywordType.NICKNAME;
        String keyword = "anonymous";

        //when

        //then
//        Optional<Member> bySearchTypeAndKeyword = memberQueryRepository.findBySearchTypeAndKeyword(memberSearchKeywordType, keyword);
//        assertTrue(bySearchTypeAndKeyword.isPresent());
        assertThrows(MemberNotFoundException.class, () -> memberService.searchMember(memberSearchKeywordType, keyword));

    }

}
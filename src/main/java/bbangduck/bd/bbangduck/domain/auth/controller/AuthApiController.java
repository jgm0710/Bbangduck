package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.auth.dto.controller.*;
import bbangduck.bd.bbangduck.domain.auth.dto.service.TokenDto;
import bbangduck.bd.bbangduck.domain.auth.exception.SignOutDifferentMemberException;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationApplicationService;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.controller.MemberApiController;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.CheckIfEmailIsAvailableRequestDto;
import bbangduck.bd.bbangduck.domain.member.dto.controller.request.MemberCheckIfNicknameIsAvailableRequestDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static bbangduck.bd.bbangduck.global.common.ThrowUtils.hasErrorsThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원 인증에 대한 요청을 담당하는 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthApiController {

    private final AuthenticationApplicationService authenticationApplicationService;

    private final AuthenticationService authenticationService;

    private final MemberService memberService;



    @PostMapping(value = "/social/sign-up")
    public ResponseEntity<MemberSignUpResponseDto> signUp(
            @RequestBody @Valid MemberSocialSignUpRequestDto memberSocialSignUpRequestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
        Long savedMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(savedMemberId);
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        MemberSignUpResponseDto memberSignUpResponseDto = MemberSignUpResponseDto.convert(savedMember, tokenDto);
        URI uri = linkTo(MemberApiController.class).slash(savedMemberId).toUri();

        return ResponseEntity.created(uri).body(memberSignUpResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
            @RequestBody @Valid OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.REFRESH_NOT_VALID, errors);

        String refreshToken = onlyRefreshTokenRequestDto.getRefreshToken();
        TokenDto tokenDto = authenticationService.refresh(refreshToken);
        TokenResponseDto tokenResponseDto = TokenResponseDto.convert(tokenDto);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @DeleteMapping("/{memberId}/withdrawal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> withdrawal(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        authenticationApplicationService.withdrawal(currentMember.getId(), memberId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{memberId}/sign-out")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> signOut(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        if (!currentMember.getId().equals(memberId)) {
            throw new SignOutDifferentMemberException();
        }

        authenticationService.signOut(memberId);

        return ResponseEntity.noContent().build();
    }

    // TODO: 2021-07-03 get mapping 으로 변경
    @PostMapping("/emails/check-availabilities")
    public ResponseEntity<AvailableResponseDto> checkIfEmailIsAvailable(
            @RequestBody @Valid CheckIfEmailIsAvailableRequestDto requestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.CHECK_IF_EMAIL_IS_AVAILABLE_NOT_VALID, errors);

        boolean result = authenticationService.checkIfEmailIsAvailable(requestDto.getEmail());

        return ResponseEntity.ok(new AvailableResponseDto(result));
    }

    // TODO: 2021-07-03 get mapping 으로 변경
    @PostMapping("/nicknames/check-availabilities")
    public ResponseEntity<AvailableResponseDto> checkIfNicknameIsAvailable(
            @RequestBody @Valid MemberCheckIfNicknameIsAvailableRequestDto requestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.CHECK_IF_NICKNAME_IS_AVAILABLE_NOT_VALID, errors);

        boolean result = authenticationService.checkIfNicknameIsAvailable(requestDto.getNickname());

        return ResponseEntity.ok(new AvailableResponseDto(result));
    }

    // TODO: 2021-05-02 자체 로그인 기능 구현 시 로그인 요청 처리 메서드 등록
    // TODO: 2021-05-02 회원 활동 금지 기능 구현



}

package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.CurrentUser;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSignUpResponseDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.MemberSocialSignUpRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.OnlyRefreshTokenRequestDto;
import bbangduck.bd.bbangduck.domain.auth.controller.dto.TokenResponseDto;
import bbangduck.bd.bbangduck.domain.auth.exception.SignOutDifferentMemberException;
import bbangduck.bd.bbangduck.domain.auth.exception.WithdrawalDifferentMemberException;
import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.auth.service.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.controller.MemberApiController;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
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

    private final AuthenticationService authenticationService;

    private final MemberService memberService;

    @PostMapping(value = "/social/sign-up")
    public ResponseEntity<ResponseDto<MemberSignUpResponseDto>> signUp(
            @RequestBody @Valid MemberSocialSignUpRequestDto memberSocialSignUpRequestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
        Long savedMemberId = authenticationService.signUp(memberSocialSignUpRequestDto.toServiceDto());
        Member savedMember = memberService.getMember(savedMemberId);
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        MemberSignUpResponseDto memberSignUpResponseDto = MemberSignUpResponseDto.convert(savedMember, tokenDto);
        URI uri = linkTo(MemberApiController.class).slash(savedMemberId).toUri();

        return ResponseEntity.created(uri).body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_SUCCESS, memberSignUpResponseDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<TokenResponseDto>> refresh(
            @RequestBody @Valid OnlyRefreshTokenRequestDto onlyRefreshTokenRequestDto,
            Errors errors
    ) {
        hasErrorsThrow(ResponseStatus.REFRESH_NOT_VALID, errors);

        String refreshToken = onlyRefreshTokenRequestDto.getRefreshToken();
        TokenDto tokenDto = authenticationService.refresh(refreshToken);
        TokenResponseDto tokenResponseDto = TokenResponseDto.convert(tokenDto);

        log.info("Refresh sign in success");
        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.REFRESH_SIGN_IN_SUCCESS, tokenResponseDto));
    }

    @DeleteMapping("/{memberId}/withdrawal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> withdrawal(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        if (!currentMember.getId().equals(memberId)) {
            throw new WithdrawalDifferentMemberException();
        }

        authenticationService.withdrawal(memberId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.WITHDRAWAL_SUCCESS, null));
    }


    @GetMapping("/{memberId}/sign-out")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseDto<Object>> signOut(
            @PathVariable Long memberId,
            @CurrentUser Member currentMember
    ) {
        if (!currentMember.getId().equals(memberId)) {
            throw new SignOutDifferentMemberException();
        }

        authenticationService.signOut(memberId);

        return ResponseEntity.ok(new ResponseDto<>(ResponseStatus.SIGN_OUT_SUCCESS, null));
    }

    // TODO: 2021-05-02 자체 로그인 기능 구현 시 로그인 요청 처리 메서드 등록
    // TODO: 2021-05-02 회원 활동 금지 기능 구현



}

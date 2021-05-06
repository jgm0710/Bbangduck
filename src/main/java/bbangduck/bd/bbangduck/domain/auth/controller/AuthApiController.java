package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.controller.MemberApiController;
import bbangduck.bd.bbangduck.domain.member.controller.MemberValidator;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpResponseDto;
import bbangduck.bd.bbangduck.domain.auth.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.config.properties.SecurityJwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

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

    private final SecurityJwtProperties securityJwtProperties;

    private final MemberValidator memberValidator;

    @PostMapping(value = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<MemberSignUpResponseDto>> signUp(
            @RequestBody @Valid MemberSignUpDto memberSignUpDto,
            Errors errors
    ) {
        memberValidator.validateSignUp(memberSignUpDto, errors);
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.signUp(securityJwtProperties.getRefreshTokenExpiredDate()));
        Member savedMember = memberService.getMember(savedMemberId);
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        MemberSignUpResponseDto memberSignUpResponseDto = MemberSignUpResponseDto.convert(savedMember, tokenDto);
        URI uri = linkTo(MemberApiController.class).slash(savedMemberId).toUri();
        log.info("회원가입 완료!!");
        log.info("회원가입 기입 정보 : {}", memberSignUpDto);

        return ResponseEntity.created(uri).body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_SUCCESS, memberSignUpResponseDto));
    }

    // TODO: 2021-05-02 자체 로그인 기능 구현 시 로그인 요청 처리 메서드 등록
    // TODO: 2021-05-02 회원 탈퇴 기능 구현
    // TODO: 2021-05-02 회원 활동 금지 기능 구현
    // TODO: 2021-05-02 로그이웃 기능 구현 
}

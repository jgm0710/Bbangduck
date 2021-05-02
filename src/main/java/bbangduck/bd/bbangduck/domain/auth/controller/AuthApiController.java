package bbangduck.bd.bbangduck.domain.auth.controller;

import bbangduck.bd.bbangduck.domain.auth.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.controller.MemberApiController;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpResponseDto;
import bbangduck.bd.bbangduck.domain.member.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.ResponseDto;
import bbangduck.bd.bbangduck.global.config.properties.JwtSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class AuthApiController {

    private final AuthenticationService authenticationService;

    private final MemberService memberService;

    private final JwtSecurityProperties jwtSecurityProperties;


    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<MemberSignUpResponseDto>> signUp(
            @RequestBody @Valid MemberSignUpDto memberSignUpDto
    ) {
        Long savedMemberId = authenticationService.signUp(memberSignUpDto.signUp(jwtSecurityProperties.getRefreshTokenExpiredDate()));
        Member savedMember = memberService.getMember(savedMemberId);
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        MemberSignUpResponseDto memberSignUpResponseDto = MemberSignUpResponseDto.convert(savedMember, tokenDto);
        URI uri = linkTo(MemberApiController.class).slash(savedMemberId).toUri();

        return ResponseEntity.created(uri).body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_SUCCESS, memberSignUpResponseDto));
    }
}

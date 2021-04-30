package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpResponseDto;
import bbangduck.bd.bbangduck.domain.member.dto.TokenDto;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.member.service.AuthenticationService;
import bbangduck.bd.bbangduck.domain.member.service.MemberService;
import bbangduck.bd.bbangduck.global.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationService authenticationService;

    private final MemberService memberService;


    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDto<MemberSignUpResponseDto>> signUp(
            @RequestBody @Valid MemberSignUpDto memberSignUpDto
    ) {
        Long savedMemberId = authenticationService.signUp(memberSignUpDto);
        Member savedMember = memberService.getMember(savedMemberId);
        TokenDto tokenDto = authenticationService.signIn(savedMemberId);
        MemberSignUpResponseDto memberSignUpResponseDto = MemberSignUpResponseDto.init(savedMember, tokenDto);

        URI uri = linkTo(MemberApiController.class).slash(savedMemberId).toUri();
        return ResponseEntity.created(uri).body(new ResponseDto<>(MemberResponseStatus.MEMBER_SIGN_UP_SUCCESS, memberSignUpResponseDto));
    }
}

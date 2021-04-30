package bbangduck.bd.bbangduck.api.member;

import bbangduck.bd.bbangduck.common.ResponseDto;
import bbangduck.bd.bbangduck.common.ResponseStatus;
import bbangduck.bd.bbangduck.member.AuthenticationService;
import bbangduck.bd.bbangduck.member.Member;
import bbangduck.bd.bbangduck.member.MemberService;
import bbangduck.bd.bbangduck.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.member.dto.MemberSignUpResponseDto;
import bbangduck.bd.bbangduck.member.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        return ResponseEntity.created(uri).body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_SUCCESS, memberSignUpResponseDto));
    }
}

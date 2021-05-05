package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.domain.member.entity.SocialType;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ValidationHasErrorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원과 관련된 요청 시 요청 Body 를 통해 들어오는 값에 대한 유효성 검증을
 * 기본 Validation 외에도 진행해야 할 경우를 위해 구현한 Custom Validator
 */
@Component
public class MemberValidator {

    public void validateSignUp(MemberSignUpDto memberSignUpDto, Errors errors) {
        String socialId = memberSignUpDto.getSocialId();
        String password = memberSignUpDto.getPassword();
        SocialType socialType = memberSignUpDto.getSocialType();

        if (socialIdExists(socialId) && socialTypeExists(socialType)) {
            if (passwordExists(password)) {
                errors.reject("NoPasswordRequired", "소셜 회원가입의 경우 비밀번호가 필요하지 않습니다.");
            }
        } else if (!socialIdExists(socialId) && !socialTypeExists(socialType)) {
            if (!passwordExists(password)) {
                errors.reject("BlankPassword", "소셜 회원가입이 아닌 경우 비밀번호를 입력해 주세요.");
            }
        } else {
            if (socialTypeExists(socialType)) {
                if (!socialIdExists(socialId)) {
                    errors.reject("WrongSocialInfo", "소셜 회원가입 시 필요한 소셜 정보를 모두 입력해 주세요.");
                }
            } else {
                if (socialIdExists(socialId)) {
                    errors.reject("WrongSocialInfo", "소셜 회원가입 시 필요한 소셜 정보를 모두 입력해 주세요.");
                }
            }
        }

        if (errors.hasErrors()) {
            throw new ValidationHasErrorException(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
        }
    }

    private boolean socialTypeExists(SocialType socialType) {
        return socialType != null;
    }

    private boolean passwordExists(String password) {
        return password != null && !password.isBlank();
    }

    private boolean socialIdExists(String socialId) {
        return socialId != null && !socialId.isBlank();
    }
}

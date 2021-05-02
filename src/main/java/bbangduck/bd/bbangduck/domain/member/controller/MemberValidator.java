package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.member.dto.MemberSignUpDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.exception.ValidationHasErrorException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class MemberValidator {

    public void validateSignUp(MemberSignUpDto memberSignUpDto, Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationHasErrorException(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
        }

        if (!memberSignUpDto.getSocialId().isBlank() && memberSignUpDto.getSocialType() != null) {
            if (memberSignUpDto.getPassword().isBlank()) {
                errors.reject("BlankPassword", "소셜 회원가입이 아닌 경우 비밀번호를 입력해 주세요.");
                throw new ValidationHasErrorException(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
            }
        } else {
            if (memberSignUpDto.getSocialType() != null) {
                if (memberSignUpDto.getSocialId().isBlank()) {
                    errors.reject("WrongSocialInfo", "소셜 회원가입 시 필요한 소셜 정보를 모두 입력해 주세요.");
                    throw new ValidationHasErrorException(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
                }
            } else {
                if (!memberSignUpDto.getSocialId().isBlank()) {
                    errors.reject("WrongSocialInfo", "소셜 회원가입 시 필요한 소셜 정보를 모두 입력해 주세요.");
                    throw new ValidationHasErrorException(ResponseStatus.MEMBER_SIGN_UP_NOT_VALID, errors);
                }
            }
        }
    }
}

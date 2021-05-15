package bbangduck.bd.bbangduck.domain.member.controller;

import bbangduck.bd.bbangduck.domain.member.controller.dto.MemberUpdateProfileRequestDto;
import bbangduck.bd.bbangduck.global.common.ResponseStatus;
import bbangduck.bd.bbangduck.global.common.ThrowUtils;
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

    public void validateUpdateProfile(MemberUpdateProfileRequestDto memberUpdateProfileRequestDto, Errors errors) {
        Long profileImageId = memberUpdateProfileRequestDto.getProfileImageId();
        String profileImageName = memberUpdateProfileRequestDto.getProfileImageName();

        boolean profileImageIdExists = profileImageIdExists(profileImageId);
        boolean profileImageNameExists = profileImageNameExists(profileImageName);

        if (profileImageIdExists) {
            if (!profileImageNameExists) {
                errors.reject("WrongProfileImageInfo", "이미지 파일 정보를 모두 기입해 주세요.");
            }
        } else {
            if (profileImageNameExists) {
                errors.reject("WrongProfileImageInfo", "이미지 파일 정보를 모두 기입해 주세요.");
            }
        }

        ThrowUtils.hasErrorsThrow(ResponseStatus.UPDATE_PROFILE_NOT_VALID, errors);
    }

    private boolean profileImageIdExists(Long profileImageId) {
        return profileImageId != null;
    }

    private boolean profileImageNameExists(String profileImageName) {
        return profileImageName != null && !profileImageName.isBlank();
    }
}

package bbangduck.bd.bbangduck.global.common.controller.exception.handler;

import bbangduck.bd.bbangduck.domain.member.controller.status.MemberResponseStatus;
import bbangduck.bd.bbangduck.global.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ValidationExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity processValidationError(MethodArgumentNotValidException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        log.error("Validation Error 발생!");
        allErrors.forEach(objectError -> log.error(allErrors.toString()));
        return ResponseEntity.badRequest().body(new ResponseDto<>(MemberResponseStatus.MEMBER_SIGN_UP_VALID_ERROR, allErrors));
    }
}

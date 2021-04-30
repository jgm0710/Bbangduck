package bbangduck.bd.bbangduck.api.common;

import bbangduck.bd.bbangduck.common.ResponseDto;
import bbangduck.bd.bbangduck.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ValidationExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity processValidationError(MethodArgumentNotValidException exception) {
        log.error("Validation Error 발생!");
        BindingResult bindingResult = exception.getBindingResult();
        return ResponseEntity.badRequest().body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_VALID_ERROR, bindingResult.getAllErrors()));
    }
}

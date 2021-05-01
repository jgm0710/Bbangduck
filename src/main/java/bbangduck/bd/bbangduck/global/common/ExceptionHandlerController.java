package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.domain.auth.exception.SocialAuthFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static bbangduck.bd.bbangduck.global.common.ModelAndViewObjectName.*;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(SocialAuthFailException.class)
    public ModelAndView socialAuthFailExceptionHandling(SocialAuthFailException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(exception.getViewName());
        modelAndView.addObject(STATUS, exception.getStatus());
        modelAndView.addObject(MESSAGE, exception.getMessage());
        modelAndView.addObject(DATA, exception.getSocialUserInfoDto());
        log.info(exception.toString());

        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ObjectError>>> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        log.error("Validation Error 발생!");
        allErrors.forEach(objectError -> log.error(allErrors.toString()));
        return ResponseEntity.badRequest().body(new ResponseDto<>(ResponseStatus.MEMBER_SIGN_UP_VALID_ERROR, allErrors));
    }
}

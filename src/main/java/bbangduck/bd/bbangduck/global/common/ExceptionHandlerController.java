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

/**
 * 작성자 : 정구민 <br><br>
 *
 * 모든 Service 게층에서 발생하는 Exception 을 한 곳에서 처리하기 위한 ExceptionHandler
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerController {

    /**
     * 소셜 인증 실패와 관련된 예외를 처리하기 위한 ExceptionHandler
     */
    @ExceptionHandler(SocialAuthFailException.class)
    public ModelAndView socialAuthFailExceptionHandling(SocialAuthFailException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(exception.getViewName());
        modelAndView.addObject(STATUS, exception.getStatus());
        modelAndView.addObject(MESSAGE, exception.getMessage());
        modelAndView.addObject(DATA, exception.getBody());
        log.info(exception.toString());

        return modelAndView;
    }

    /**
     * 기본적으로 제공되는 Spring Validation 의 Errors 를 통해 발생하는 Validation Exception 을 처리하기 위한 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ObjectError>>> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        log.error("Validation Error 발생!");
        allErrors.forEach(objectError -> log.error(allErrors.toString()));
        return ResponseEntity.badRequest().body(new ResponseDto<>(ResponseStatus.VALIDATION_ERROR, allErrors));
    }

    // TODO: 2021-05-02 Custom Validation Error Handler 추가
}

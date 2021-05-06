package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.domain.auth.exception.SocialAuthFailException;
import bbangduck.bd.bbangduck.global.common.exception.BadRequestException;
import bbangduck.bd.bbangduck.global.common.exception.ConflictException;
import bbangduck.bd.bbangduck.global.common.exception.NotFoundException;
import bbangduck.bd.bbangduck.global.common.exception.ValidationHasErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static bbangduck.bd.bbangduck.global.common.ModelAndViewObjectName.*;

/**
 * 작성자 : 정구민 <br><br>
 * <p>
 * 모든 Service 게층에서 발생하는 Exception 을 한 곳에서 처리하기 위한 ExceptionHandler
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerController {

    private ObjectError objectError;

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

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseDto<Object>> conflictExceptionHandling(ConflictException exception) {
        int exceptionStatus = exception.getStatus();
        String exceptionMessage = exception.getMessage();
        log.error("ConflictException 발생!!");
        log.error(exceptionMessage);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDto<>(exceptionStatus, null, exceptionMessage));
    }

    // TODO: 2021-05-03 BadRequest Exception Handler 를 사용하는 테스트 코드 작성
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto<Object>> badRequestExceptionHandling(BadRequestException exception) {
        int exceptionStatus = exception.getStatus();
        String exceptionMessage = exception.getMessage();
        log.error("BadRequestException 발생!!");
        log.error(exceptionMessage);

        return ResponseEntity.badRequest().body(new ResponseDto<>(exceptionStatus, null, exceptionMessage));
    }

    // TODO: 2021-05-03 NotFound Exception Handler 를 사용하는 테스트 코드 작성
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> NotFoundExceptionHandling(NotFoundException exception) {
        int exceptionStatus = exception.getStatus();
        String exceptionMessage = exception.getMessage();
        log.error("NotFoundException 발생!!");
        log.error(exceptionMessage);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto<>(exceptionStatus, null, exceptionMessage));
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

    /**
     * Errors 를 통해 발생하는 Validation Exception 을 처리하기 위한 ExceptionHandler
     */
    @ExceptionHandler(ValidationHasErrorException.class)
    public ResponseEntity<ResponseDto<List<ErrorsResponseDto>>> validationHasErrorExceptionHandling(ValidationHasErrorException exception) {
        Errors errors = exception.getErrors();
        List<ObjectError> globalErrors = errors.getGlobalErrors();
        List<FieldError> fieldErrors = errors.getFieldErrors();

        List<ErrorsResponseDto> errorsResponseDtos = new ArrayList<>();

        globalErrors.forEach(globalError -> {
            ErrorsResponseDto errorsResponseDto = ErrorsResponseDto.builder()
                    .objectName(globalError.getObjectName())
                    .code(globalError.getCode())
                    .defaultMessage(globalError.getDefaultMessage())
                    .field(null)
                    .build();
            errorsResponseDtos.add(errorsResponseDto);
        });

        fieldErrors.forEach(fieldError -> {
            ErrorsResponseDto errorsResponseDto = ErrorsResponseDto.builder()
                    .objectName(fieldError.getObjectName())
                    .code(fieldError.getCode())
                    .defaultMessage(fieldError.getDefaultMessage())
                    .field(fieldError.getField())
                    .build();
            errorsResponseDtos.add(errorsResponseDto);
        });

        log.error("Validation Error 발생!!");

        return ResponseEntity.badRequest().body(new ResponseDto<>(exception.getStatus(), errorsResponseDtos, exception.getMessage()));
    }

    // TODO: 2021-05-02 BindingResult 처리 ExceptionHandler 추가
}

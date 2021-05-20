package bbangduck.bd.bbangduck.global.common;

import bbangduck.bd.bbangduck.domain.auth.exception.SocialAuthFailException;
import bbangduck.bd.bbangduck.global.common.exception.*;
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
 *
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
    public ModelAndView socialAuthFailExceptionHandling(SocialAuthFailException ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(ex.getViewName());
        modelAndView.addObject(STATUS, ex.getStatus());
        modelAndView.addObject(MESSAGE, ex.getMessage());
        modelAndView.addObject(DATA, ex.getBody());
        log.info(ex.toString());

        return modelAndView;
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseDto<Object>> conflictExceptionHandling(ConflictException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Conflict exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDto<>(status, null, message));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto<Object>> badRequestExceptionHandling(BadRequestException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Bad request exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);

        return ResponseEntity.badRequest().body(new ResponseDto<>(status, null, message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> notFoundExceptionHandling(NotFoundException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Not found exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);
        log.error(message);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto<>(status, null, message));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ResponseDto<Object>> internalServerErrorExceptionHandling(InternalServerErrorException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Internal server error exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);
        log.error(message);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto<>(status, null, message));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseDto<Object>> unauthorizedExceptionHandling(UnauthorizedException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Unauthorized exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);
        log.error(message);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto<>(status, null, message));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseDto<Object>> forbiddenExceptionHandling(ForbiddenException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.error("Forbidden exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);
        log.error(message);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseDto<>(status, null, message));
    }

    /**
     * 기본적으로 제공되는 Spring Validation 의 Errors 를 통해 발생하는 Validation Exception 을 처리하기 위한 ExceptionHandler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ObjectError>>> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException ex) {
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        log.error("Validation error occurs.");
        allErrors.forEach(objectError -> log.error(allErrors.toString()));
        return ResponseEntity.badRequest().body(new ResponseDto<>(ResponseStatus.VALIDATION_ERROR, allErrors));
    }

    /**
     * Errors 를 통해 발생하는 Validation Exception 을 처리하기 위한 ExceptionHandler
     */
    @ExceptionHandler(ValidationHasErrorException.class)
    public ResponseEntity<ResponseDto<List<ErrorsResponseDto>>> validationHasErrorExceptionHandling(ValidationHasErrorException ex) {
        int status = ex.getStatus();
        String message = ex.getMessage();
        Errors errors = ex.getErrors();

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

        log.error("Validation has error exception occurs.");
        log.error("Response status : {}, Message : {}", status, message);

        return ResponseEntity.badRequest().body(new ResponseDto<>(status, errorsResponseDtos, message));
    }

    // TODO: 2021-05-02 BindingResult 처리 ExceptionHandler 추가
}

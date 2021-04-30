package bbangduck.bd.bbangduck.domain.member.controller.exception.handler;

import bbangduck.bd.bbangduck.global.security.social.common.exception.SocialAuthFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import static bbangduck.bd.bbangduck.global.common.util.ModelAndViewAttributeName.*;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class SocialAuthExceptionHandlerViewController {

    @ExceptionHandler(SocialAuthFailException.class)
    public ModelAndView socialAuthFailExceptionHandler(SocialAuthFailException exception) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(exception.getViewName());
        modelAndView.addObject(STATUS.getAttributeName(), exception.getStatus());
        modelAndView.addObject(MESSAGE.getAttributeName(), exception.getMessage());
        modelAndView.addObject(DATA.getAttributeName(), exception.getSocialUserInfoDto());
        log.info(exception.toString());

        return modelAndView;
    }

}

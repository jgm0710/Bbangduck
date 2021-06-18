package bbangduck.bd.bbangduck.global.aop;

import bbangduck.bd.bbangduck.domain.auth.AccountAdapter;
import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

@Component
@Aspect
@Slf4j
public class RequestLoggingAspect {
    @Pointcut("within(bbangduck.bd.bbangduck.domain.*.controller..*)") // 이런 패턴이 실행될 경우 수행
    public void loggerPointCut() {
    }

    @Around("loggerPointCut()")
    public Object methodLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest(); // request 정보를 가져온다.
        Member accountMember = getAccountMember();

        long start = System.currentTimeMillis();

        String controllerName = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        Map<String, Object> params = new HashMap<>();

        try {
            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("params", getParams(request));
            params.put("log_time", new Date());
            params.put("request_uri", request.getRequestURI());
            params.put("http_method", request.getMethod());
            if (isNotNull(accountMember)) {
                params.put("authenticated_by", "[MemberId = " + accountMember.getId() + ", MemberNickname = " + accountMember.getNickname() + "]");
            }
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        } finally {
            long end = System.currentTimeMillis();
            params.put("request_processing_time", (end - start) + " ms");
            log.info("RequestInfo : {}", params); // param에 담긴 정보들을 한번에 로깅한다.
        }

        return result;

    }

    private Member getAccountMember() {
        try {
            AccountAdapter accountAdapter = (AccountAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return accountAdapter.getMember();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * request 에 담긴 정보를 JSONObject 형태로 반환한다.
     *
     * @param request
     * @return
     */
    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }
}


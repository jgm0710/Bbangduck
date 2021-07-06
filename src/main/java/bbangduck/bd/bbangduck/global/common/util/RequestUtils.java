package bbangduck.bd.bbangduck.global.common.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Stream;

/**
 * HttpServletRequest 에서 값을 꺼내오는 등의 동작을 구현한 Util Class
 *
 * @author jgm
 */
public class RequestUtils {

    public static MultiValueMap<String, String> getParametersFromRequest(HttpServletRequest request) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            Stream.of(values).forEach(value -> params.add(key, value));
        }
        return params;
    }
}

package bbangduck.bd.bbangduck.global.common;

import java.util.List;

/**
 * 작성자 : 정구민 <br><br>
 *
 * null check 를 위해 구현한 Util Class
 */
public class NullCheckUtils {

    public static boolean existsString(String st) {
        return st != null && !st.isBlank();
    }

    public static <T> boolean existsList(List<T> list) {
        return list != null && !list.isEmpty();
    }

    public static <T> boolean isNotNull(T object) {
        return object != null;
    }

    public static <T> boolean isNull(T object) {
        return object == null;
    }
}

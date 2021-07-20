package bbangduck.bd.bbangduck.global.common;

/**
 * Enum request, response 값들을 문서화 하기 위해 EnumType 들은 해당 interface 상속
 * <p>
 * /src/test 의 EnumDocument, EnumViewController, EnumDocumentationTest 에 의해
 * 문서화
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
public interface EnumType {
    String name();

    String getDescription();
}

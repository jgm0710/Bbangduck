package bbangduck.bd.bbangduck.api.document.utils;

import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * - type : src/test/resources/org.springframework.restdocs.templates 하위에 있는 snippet 지정
 * <p>
 * - attributes : key, value 쌍으로 된 속성 값 지정 -> key:"title", value:"{string value}" 를 통해 스니펫 이름 지정
 * <p>
 * - subsectionExtractor : type 으로 지정된 스니펫을 사용하여 {type value}-{subsectionExtractor}.snippet 의 형태로 generated-snippets 생성
 * <p>
 * - ignoreUndocumentedFields : 문서화되지 않은 필드 무시 여부 지정
 * <p>
 * - descriptors : FieldDescriptor type 의 parameter, fieldWithPath("{fileName}").description("{description}") 으로 된 값의 배열 기입
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
public class CustomResponseFieldsSnippet extends AbstractFieldsSnippet {
    @Builder
    public CustomResponseFieldsSnippet(
            String type,
            List<FieldDescriptor> descriptors,
            Map<String, Object> attributes,
            boolean ignoreUndocumentedFields,
            PayloadSubsectionExtractor<?> subsectionExtractor
    ) {
        super(type, descriptors, attributes, ignoreUndocumentedFields, subsectionExtractor);
    }

    @Override
    protected MediaType getContentType(Operation operation) {
        return operation.getResponse().getHeaders().getContentType();
    }

    @Override
    protected byte[] getContent(Operation operation) throws IOException {
        return operation.getResponse().getContent();
    }
}

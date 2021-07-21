package bbangduck.bd.bbangduck.api.document.utils;

/**
 * src/docs/asciidoc/docinfo.html 을 통해 docUrl.text 에 해당하는 링크를 클릭 시 모달창이 뜨도록 하기 위해
 * 링크를 걸어주는 로직을 구현
 * <p>
 * src/docs/asciidoc/common/ 하위에 생성한 docUrl.pageId 값에 해당하는 문서를 link 하도록 구현
 * <p>
 * {@link bbangduck.bd.bbangduck.api.document.enumerate.EnumDocumentationTest} 를 통해 생성된 snippets 를 사용하는 adoc 파일에 대한 링크를 제공
 *
 * @author Gumin Jeong
 * @since 2021-07-20
 */
public interface DocumentLinkGenerator {
    static String generateLinkCode(DocUrl docUrl) {
        return String.format("link:common/%s.html[%s %s,role=\"popup\"]", docUrl.getPageId(), docUrl.getText(), "코드");
    }

    static String generateText(DocUrl docUrl) {
        return String.format("%s %s", docUrl.getText(), "코드명");
    }
}

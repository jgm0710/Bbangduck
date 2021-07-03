package bbangduck.bd.bbangduck.global.common;

/**
 * {@link PaginationResultResponseDto}, {@link SliceResultResponseDto} 등에서
 * Entity List 를 Dto List 로 변환하기 위한 method 를 지정하기 위해 Functional Interface 구현
 *
 * @author jgm
 */
public interface Convertor<R, S> {
    public S convert(R content);
}

package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 목록 조회 응답 Body Data 포멧
 * <p>
 * TotalResultsCount 조회 쿼리의 사용이 리소스 소모에 부담되는 경우
 * nextPage 가 존재하는지 여부만 표현
 *
 * @author jgm
 * @author kjs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SliceResultResponseDto<T> {
    private List<T> contents;
    private int nowPageNum;
    private int requestAmount;
    private boolean hasNextPage;

    public <R> SliceResultResponseDto<R> convert(Convertor<T, R> convertor) {
        List<R> results = contents.stream().map(convertor::convert).collect(Collectors.toList());
        return new SliceResultResponseDto<>(results, nowPageNum, requestAmount, hasNextPage);
    }
}

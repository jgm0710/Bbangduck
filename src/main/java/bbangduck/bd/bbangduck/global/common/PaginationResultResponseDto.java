package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 목록 조회 응답 Body Data 포멧
 * <p>
 * TotalResultsCount 룰 포함
 *
 * @author jgm
 * @author kjs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResultResponseDto<T> {
    private List<T> contents;
    private int nowPageNum;
    private int requestAmount;
    private long totalResultsCount;

    public <R> PaginationResultResponseDto<R> convert(Convertor<T, R> convertor) {
        List<R> result = contents.stream().map(convertor::convert).collect(Collectors.toList());
        return new PaginationResultResponseDto<>(result, nowPageNum, requestAmount, totalResultsCount);
    }
}

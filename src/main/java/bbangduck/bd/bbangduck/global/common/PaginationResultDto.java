package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Application Service 계층에서 페이징 응답 처리가 필요한 데이터를 API 단으로 옮기기 위해 공통적으로 사용할 Dto
 *
 * @author Gumin Jeong
 * @since 2021-07-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResultDto <T> {
    private List<T> contents;
    private long totalResultsCount;
}

package bbangduck.bd.bbangduck.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 목록 조회 시 페이징에 대한 정보도 같이 응답하기 위해 사용하는 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponseDto<T> {

    private T list;

    private int pageNum;

    private int amount;

    private long totalPageCount;

    private String prevPage;

    private String nextPage;

}

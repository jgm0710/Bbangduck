package bbangduck.bd.bbangduck.domain.review.dto.controller.response;

import bbangduck.bd.bbangduck.global.common.CriteriaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static bbangduck.bd.bbangduck.global.common.NullCheckUtils.isNotNull;

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

    private int nowPageNum;

    private int amount;

    private long totalPagesCount;

    private String prevPageUrl;

    private String nextPageUrl;

    // TODO: 2021-06-30 구현
    public static <T> PaginationResponseDto convert(T list, CriteriaDto criteriaDto, long totalResultsCount, String path, MultiValueMap<String, String> params) {
        long totalPagesCount = calculateTotalPagesCount(totalResultsCount, criteriaDto.getAmount());

        return PaginationResponseDto.builder()
                .list(list)
                .nowPageNum(criteriaDto.getPageNum())
                .amount(criteriaDto.getAmount())
                .totalPagesCount(totalPagesCount)
                .prevPageUrl(createPrevPageUrl(path, criteriaDto, totalPagesCount, params))
                .nextPageUrl(createNextPageUrl(path, criteriaDto, totalPagesCount, params))
                .build();
    }

    private static String createNextPageUrl(String path, CriteriaDto criteriaDto, long totalPagesCount, MultiValueMap<String, String> params) {
        Integer nextPageNum = getNextPageNum(totalPagesCount, criteriaDto.getPageNum());
        return createUrl(path, nextPageNum, criteriaDto.getAmount(), params);
    }

    private static String createPrevPageUrl(String path, CriteriaDto criteriaDto, long totalPagesCount, MultiValueMap<String, String> params) {
        Integer prevPageNum = getPrevPageNum(totalPagesCount, criteriaDto.getPageNum());
        return createUrl(path, prevPageNum, criteriaDto.getAmount(), params);
    }

    private static String createUrl(String path, Integer pageNum, int amount, MultiValueMap<String, String> params) {
        if (isNotNull(pageNum)) {
            params.set("pageNum", String.valueOf(pageNum));
            params.set("amount", String.valueOf(amount));

            return UriComponentsBuilder.fromPath(path)
                    .queryParams(params)
                    .toUriString();
        }
        return null;
    }

    private static Integer getNextPageNum(long totalPagesCount, int pageNum) {
        int nextPageNum = pageNum + 1;
        if (nextPageNum <= totalPagesCount) {
            return nextPageNum;
        }
        return null;
    }

    private static Integer getPrevPageNum(long totalPagesCount, int pageNum) {
        int prevPageNum = pageNum - 1;
        if (prevPageNum >= 1 && prevPageNum <= totalPagesCount) {
            return prevPageNum;
        }
        return null;
    }

    private static long calculateTotalPagesCount(long totalResultsCount, int amount) {
        long totalPagesCount = totalResultsCount / amount;
        if (totalResultsCount % amount != 0) {
            totalPagesCount++;
        }
        return totalPagesCount;
    }
}

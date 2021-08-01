package bbangduck.bd.bbangduck.global.common.util;

/**
 * 목록 조회 쿼리에 사용되는 Dto 에 대한 Interface
 *
 * @author Gumin Jeong
 * @since 2021-07-22
 */
public interface PagingQuery {
    long getPageNum();
    int getAmount();
    default long getOffset() {
        return (getPageNum() - 1) * getAmount();
    }
}

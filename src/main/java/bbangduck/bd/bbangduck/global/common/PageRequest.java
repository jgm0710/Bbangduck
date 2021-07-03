package bbangduck.bd.bbangduck.global.common;

/**
 * 페이징 조회 요청을 하는 경우 pageNum, amount 필요
 * -> 해당 interface 사용하여 Dto 구현
 *
 * @author jgm
 * @author kjs
 */
public interface PageRequest {
  long getPageNum();
  int getAmount();
}

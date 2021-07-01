package bbangduck.bd.bbangduck.global.common;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 목록 조회 요청 시 필요한 페이징 기준 정보를 담기 위한 Dto
 */
public class CriteriaDto {

    @Min(value = 1, message = "페이지 번호는 1보다 작을 수 없습니다.")
    private Integer pageNum;

    @Range(min = 1, max = 500, message = "조회 가능 수량은 1~500 까지 입니다.")
    private Integer amount;

    public CriteriaDto() {
        this.pageNum = 1;
        this.amount = 15;
    }

    public CriteriaDto(Integer pageNum, Integer amount) {
        this.pageNum = pageNum;
        this.amount = amount;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getOffset() {
        return (pageNum - 1) * amount;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}

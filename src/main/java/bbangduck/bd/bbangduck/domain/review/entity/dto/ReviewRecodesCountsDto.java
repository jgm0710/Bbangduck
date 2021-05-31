package bbangduck.bd.bbangduck.domain.review.entity.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 방탈출 리뷰 작성 개수를 조회하기 위해 구현한 Dto Class
 * 회원의 전체 리뷰 개수, 회원의 방탈출 성공 리뷰 개수, 회원의 방탈출 실패 리뷰 개수 를 담는다.
 */
public class ReviewRecodesCountsDto {

    private int totalRecodesCount;

    private int successRecodesCount;

    private int failRecodesCount;

    public ReviewRecodesCountsDto() {
        this.totalRecodesCount = 0;
        this.successRecodesCount = 0;
        this.failRecodesCount = 0;
    }

    @Builder
    public ReviewRecodesCountsDto(int totalRecodesCount, int successRecodesCount, int failRecodesCount) {
        this.totalRecodesCount = totalRecodesCount;
        this.successRecodesCount = successRecodesCount;
        this.failRecodesCount = failRecodesCount;
    }

    public int getNextRecodeNumber() {
        return this.totalRecodesCount + 1;
    }

    public int getTotalRecodesCount() {
        return totalRecodesCount;
    }

    public int getSuccessRecodesCount() {
        return successRecodesCount;
    }

    public int getFailRecodesCount() {
        return failRecodesCount;
    }

    @Override
    public String toString() {
        return "ReviewRecodesCountsDto{" +
                "totalRecodesCount=" + totalRecodesCount +
                ", successRecodesCount=" + successRecodesCount +
                ", failRecodesCount=" + failRecodesCount +
                '}';
    }
}

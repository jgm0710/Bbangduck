package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰에 대한 좋아요를 관리하는 DB 조작을 위해 구현한 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @CreationTimestamp
    private LocalDateTime registerTimes;

    @Builder
    public ReviewLike(Long id, Member member, Review review) {
        this.id = id;
        this.member = member;
        this.review = review;
    }

    public static ReviewLike init(Member member, Review review) {
        return ReviewLike.builder()
                .member(member)
                .review(review)
                .build();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Review getReview() {
        return review;
    }

    public LocalDateTime getRegisterTimes() {
        return registerTimes;
    }
}

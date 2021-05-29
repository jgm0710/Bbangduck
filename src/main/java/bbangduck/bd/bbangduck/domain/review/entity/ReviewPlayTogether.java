package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "play_together")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPlayTogether {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "play_together_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreationTimestamp
    private LocalDateTime registerTimes;

    @Builder
    public ReviewPlayTogether(Long id, Review review, Member member) {
        this.id = id;
        this.review = review;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public Review getReview() {
        return review;
    }

    public Member getMember() {
        return member;
    }

    public LocalDateTime getRegisterTimes() {
        return registerTimes;
    }
}

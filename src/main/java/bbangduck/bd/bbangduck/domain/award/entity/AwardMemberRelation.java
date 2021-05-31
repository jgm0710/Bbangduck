package bbangduck.bd.bbangduck.domain.award.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "award_member_rel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AwardMemberRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_member_rel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_id")
    private Award award;

    @CreationTimestamp
    private LocalDateTime registerTimes;
}

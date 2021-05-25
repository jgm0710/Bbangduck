package bbangduck.bd.bbangduck.domain.member.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_search_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "member_search_type")
    @Enumerated(EnumType.STRING)
    private MemberSearchType searchType;

    private String searchKeyword;

    @CreationTimestamp
    private LocalDate searchDate;

    @CreationTimestamp
    private LocalDateTime searchTimes;
}

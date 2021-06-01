package bbangduck.bd.bbangduck.domain.search.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table
@Builder
public class MemberSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_search_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private MemberSearchType searchType;

    private String searchKeyword;

    @CreationTimestamp
    private LocalDate searchDate;

    @CreationTimestamp
    private LocalDateTime searchTimes;

    public static MemberSearch toEntity(MemberSearchDto memberSearchDto, Member member) {
        return MemberSearch.builder()
                .id(memberSearchDto.getId())
                .member(member)
                .searchKeyword(memberSearchDto.getSearchKeyword())
                .searchType(memberSearchDto.getSearchType())
                .build();
    }

}

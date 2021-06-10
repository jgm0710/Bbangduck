package bbangduck.bd.bbangduck.domain.search.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.search.dto.MemberSearchDto;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

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

    @CreatedDate
    //    @CreationTimestamp
//    @Temporal(TemporalType.DATE) // only Date or Calendar
//    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
//    @Column(columnDefinition = "DATE")
    private LocalDate searchDate;

    @CreationTimestamp
    private LocalDateTime searchTimes;

    public static MemberSearch toEntity(MemberSearchDto memberSearchDto, Member member) {
        return MemberSearch.builder()
                .id(memberSearchDto.getId())
                .member(member)
                .searchKeyword(memberSearchDto.getSearchKeyword())
                .searchType(memberSearchDto.getSearchType())
                .searchDate(memberSearchDto.getSearchDate())
                .build();
    }

    @Override
    public String toString() {
        return "MemberSearch{" +
                "id=" + id +
                ", searchType=" + searchType +
                ", searchKeyword='" + searchKeyword + '\'' +
                ", searchDate=" + searchDate +
                ", searchTimes=" + searchTimes +
                '}';
    }
}

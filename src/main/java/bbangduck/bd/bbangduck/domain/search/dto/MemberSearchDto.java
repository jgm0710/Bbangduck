package bbangduck.bd.bbangduck.domain.search.dto;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import bbangduck.bd.bbangduck.domain.search.entity.MemberSearch;
import bbangduck.bd.bbangduck.domain.search.entity.enumerate.MemberSearchType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Project : bbangduck
 * Create by IntelliJ IDEA
 * User: otrodevym
 * Date: 2021/6/1/0001
 * Time: 오후 1:59:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MemberSearchDto {

    private Long id;

    private Long memberId;

    private MemberSearchType searchType;

    private String searchKeyword;


    private LocalDate searchDate;

    public static MemberSearchDto of(MemberSearch memberSearch) {
        return MemberSearchDto.builder()
                .id(memberSearch.getId())
                .searchType(memberSearch.getSearchType())
                .searchKeyword(memberSearch.getSearchKeyword())
                .memberId(memberSearch.getMember().getId())
                .searchDate(memberSearch.getSearchDate())
                .build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    @Builder
    public static class MemberSearchTopMonthDto {
//        qMemberSearch.searchKeyword.count().as("count"),
//        qMemberSearch.searchKeyword,
//        qMemberSearch.searchType,
//        qMemberSearch.searchDate
        private Long count;

        private String searchKeyword;

        private MemberSearchType searchType;

        private LocalDate searchDate;

    }




}

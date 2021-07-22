package bbangduck.bd.bbangduck.domain.theme.entity;

import bbangduck.bd.bbangduck.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 테마를 플레이한 회원에 대한 내역을 기록하기 위한 Entity
 *
 * @author Gumin Jeong
 * @since 2021-07-22
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemePlayMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime lastPlayDateTime;

    private long reviewLikeCount;

    @Builder
    public ThemePlayMember(Long id, Theme theme, Member member, LocalDateTime lastPlayDateTime, long reviewLikeCount) {
        this.id = id;
        this.theme = theme;
        this.member = member;
        this.lastPlayDateTime = lastPlayDateTime;
        this.reviewLikeCount = reviewLikeCount;
    }

    public static ThemePlayMember init(Theme theme, Member member) {
        return ThemePlayMember.builder()
                .theme(theme)
                .member(member)
                .lastPlayDateTime(LocalDateTime.now())
                .reviewLikeCount(0)
                .build();
    }

    public Member getMember() {
        return member;
    }

    public Theme getTheme() {
        return theme;
    }

    public LocalDateTime getLastPlayDateTime() {
        return lastPlayDateTime;
    }

    public long getReviewLikeCount() {
        return reviewLikeCount;
    }

    public void refreshLastPlayDateTime() {
        this.lastPlayDateTime = LocalDateTime.now();
    }

    public void increaseReviewLikeCount() {
        this.reviewLikeCount++;
    }

    @Override
    public String toString() {
        return "ThemePlayMember{" +
                "id=" + id +
//                ", theme=" + theme +
//                ", member=" + member +
                ", lastPlayDateTime=" + lastPlayDateTime +
                ", reviewLikeCount=" + reviewLikeCount +
                '}';
    }
}

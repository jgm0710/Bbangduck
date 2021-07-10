package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 회원의 방탈출 게임 플레이 성향 (선호 장르) 정보를 담을 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPlayInclination extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_play_inclination_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    private Long playCount;

    @Builder
    public MemberPlayInclination(Long id, Member member, Genre genre, Long playCount) {
        this.id = id;
        this.member = member;
        this.genre = genre;
        this.playCount = playCount;
    }

    public static MemberPlayInclination init(Member member, Genre genre) {
        return MemberPlayInclination.builder()
                .member(member)
                .genre(genre)
                .playCount(0L)
                .build();
    }

    public void increasePlayCount() {
        this.playCount++;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Genre getGenre() {
        return genre;
    }

    public Long getPlayCount() {
        return playCount;
    }

    @Override
    public String toString() {
        return "MemberPlayInclination{" +
                "id=" + id +
                ", member=" + member +
                ", genre=" + genre +
                ", playCount=" + playCount +
                '}';
    }
}

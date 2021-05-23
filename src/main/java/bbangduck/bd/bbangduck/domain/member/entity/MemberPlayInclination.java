package bbangduck.bd.bbangduck.domain.member.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.jdo.annotations.Join;
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

    private String genreCode;

    private int playCount;

    public void increasePlayCount() {
        this.playCount++;
    }
}

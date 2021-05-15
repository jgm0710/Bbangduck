package bbangduck.bd.bbangduck.domain.review.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.jdo.annotations.Join;
import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 리뷰 작성 시 도전한 테마에 대한 체감 장르 정보를 담을 Entity
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPerceivedThemeGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_perceived_theme_genre_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    private String genreCode;

}

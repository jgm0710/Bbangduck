package bbangduck.bd.bbangduck.domain.review.entity;

import bbangduck.bd.bbangduck.domain.genre.entity.Genre;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.jdo.annotations.Join;
import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @CreationTimestamp
    private LocalDateTime registerTimes;

    @Builder
    public ReviewPerceivedThemeGenre(Long id, Review review, Genre genre) {
        this.id = id;
        this.review = review;
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public Review getReview() {
        return review;
    }

    public Genre getGenre() {
        return genre;
    }

    public LocalDateTime getRegisterTimes() {
        return registerTimes;
    }
}

package bbangduck.bd.bbangduck.domain.theme.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 작성자 : 정구민 <br><br>
 *
 * 관계 매핑을 위해 임시 구현
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long id;

    private String genreCode;

    public Long getId() {
        return id;
    }

    public String getGenreCode() {
        return genreCode;
    }
}

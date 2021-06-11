package bbangduck.bd.bbangduck.domain.shop.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long id;

    @Column(name = "area_code")
    private String code;

    @Column(name = "area_name")
    private String name;

    @Builder
    public Area(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}

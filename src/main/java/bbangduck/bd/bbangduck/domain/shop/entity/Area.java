package bbangduck.bd.bbangduck.domain.shop.entity;

import javax.persistence.*;

// TODO: 2021-05-25 clear
@Entity
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_id")
    private Long id;

    @Column(name = "area_code")
    private String code;

    @Column(name = "area_name")
    private String name;
}

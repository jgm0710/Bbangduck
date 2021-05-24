package bbangduck.bd.bbangduck.domain.award.entity;

import bbangduck.bd.bbangduck.global.common.BaseEntityDateTime;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Award extends BaseEntityDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "award_group")
    private AwardGroup group;

    @Column(name = "award_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "award_condition")
    private AwardCondition condition;
}

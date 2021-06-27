package bbangduck.bd.bbangduck.domain.shop.entity.embeded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {

    // 위도
    @Column(name = "lat")
    @Getter
    private Double latitude;

    // 경도
    @Column(name = "lon")
    @Getter
    private Double longitude;
}

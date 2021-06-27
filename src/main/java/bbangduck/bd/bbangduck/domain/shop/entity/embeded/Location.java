package bbangduck.bd.bbangduck.domain.shop.entity.embeded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
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

    @Builder
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public static Location of(Double latitude, Double longitude) {
        return Location.builder()
            .latitude(latitude)
            .longitude(longitude)
            .build();
    }
}

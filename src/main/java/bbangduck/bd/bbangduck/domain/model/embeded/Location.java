package bbangduck.bd.bbangduck.domain.model.embeded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {

    @Column(name = "lat")
    private Double latitude;

    @Column(name = "lon")
    private Double longitude;
}

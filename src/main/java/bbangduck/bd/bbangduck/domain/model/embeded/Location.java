package bbangduck.bd.bbangduck.domain.model.embeded;

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

    @Column(name = "lat")
    private Float latitude;

    @Column(name = "lon")
    private Float longitude;
}

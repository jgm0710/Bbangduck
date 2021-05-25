package bbangduck.bd.bbangduck.domain.model.embeded;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {

    @Column(name = "lat")
    private Float latitude;

    @Column(name = "lon")
    private Float longitude;
}

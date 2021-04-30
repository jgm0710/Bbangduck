package bbangduck.bd.bbangduck.global.common.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityDateTime {

    @CreationTimestamp
    private LocalDateTime registerDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}

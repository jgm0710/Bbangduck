package bbangduck.bd.bbangduck.global.common;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 작성자 : 정구민 <br><br>
 *
 * Entity 인스턴스 생성 시 기입될 Register Date,
 * Entity 인스턴스 수정 시 기입될 Update Date 를 상속을 통해 보다 쉽게 관리하기 Class
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityDateTime {

    @CreationTimestamp
    protected LocalDateTime registerTimes;

    @UpdateTimestamp
    protected LocalDateTime updateTimes;


    public LocalDateTime getRegisterTimes() {
        return registerTimes;
    }

    public LocalDateTime getUpdateTimes() {
        return updateTimes;
    }
}

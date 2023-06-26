
package com.viettel.base.cms.model;

import com.viettel.base.cms.dto.LockUserDTO;
import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.model.BaseFWModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(name = "lock_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LockUser extends BaseFWModel {

    @Id
    @Column(name = "lock_user_id", nullable = false)
    private Long lockUserId;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Column(name = "type_lock", nullable = true)
    private String typeLock;

    @Column(name = "value_wrong", nullable = true)
    private String valueWrong;

    @CreationTimestamp
    @Column(name = "request_time", nullable = true, updatable = false)
    private LocalDateTime requestTime;

    @Column(name = "update_by", nullable = true)
    private String updateBy;

    @Column(name = "status", nullable = true)
    private String status;

    @Override
    public BaseFWDTO toDTO() {
        LockUserDTO dto = new LockUserDTO(
                lockUserId,
                userName,
                typeLock,
                valueWrong,
                requestTime,
                updateBy,
                status
        );
        return dto;
    }
}

package com.viettel.base.cms.model;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.model.BaseFWModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "province")
// causes Lombok to generate toString(), equals(), hashCode(), getter() & setter(), and Required arguments constructor in one go.
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class Province extends BaseFWModel {
    @Id
    @Column(name = "pro_id", nullable = false)
    private Long proId;

    @Column(name = "pro_code", nullable = false)
    private String proCode;

    @Column(name = "pro_name", nullable = false)
    private String proName;

    @Column(name = "status", nullable = true)
    private Long status;

    @Column(name = "create_datetime", nullable = true)
    private LocalDateTime createDatetime;

    @Column(name = "create_by", nullable = true)
    private String createBy;

    @Column(name = "update_datetime", nullable = true)
    private LocalDateTime updateDatetime;

    @Column(name = "update_by", nullable = true)
    private String updateBy;
    @Override
    public BaseFWDTO toDTO() {
        return null;
    }
}

package com.viettel.base.cms.model;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.model.BaseFWModel;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "option_set_value")
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
public class OptionSetValue extends BaseFWModel {

    @Id
    @Column(name = "option_set_value_id", nullable = false)

    private Long optionSetValueId;

    @Column(name = "option_set_id", nullable = true)
    private Long optionSetId;

    @Column(name = "name", nullable = true)
    private String name;
    
    @Column(name = "value", nullable = true)
    private String value;

    @Column(name = "status", nullable = true)
    private Long status;

    @Column(name = "logo", nullable = true)
    private byte[] logo;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_datetime", nullable = true)
    private LocalDateTime createDatetime;

    @Column(name = "create_by", nullable = true)
    private String createBy;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_datetime", nullable = true)
    private LocalDateTime updateDatetime;

    @Column(name = "update_by", nullable = true)
    private String updateBy;
    
    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "language", nullable = true)
    private String language;

    @Override
    public BaseFWDTO toDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

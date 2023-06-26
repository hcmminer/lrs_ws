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
@Table(name = "action_log_detail")
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
public class ActionLogDetail extends BaseFWModel {

    @Id
    @Column(name = "action_log_detail_id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_log_detail_id_generator")
//    @SequenceGenerator(name = "action_log_detail_id_generator",
//            sequenceName = "action_log_detail_seq",
//            allocationSize = 1)
    private Long actionLogDetalId;

    @Column(name = "action_log_id", nullable = true)
    private Long actionLogId;

    @Column(name = "table_name", nullable = true)
    private String tableName;

    @Column(name = "row_id", nullable = true)
    private Long rowId;

    @Column(name = "col_name", nullable = true)
    private String colName;

    @Column(name = "old_value", nullable = true)
    private String oldValue;
    
    @Column(name = "new_value", nullable = true)
    private String newValue;
    
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_datetime", nullable = true)
    private LocalDateTime createDatetime;


    @Override
    public BaseFWDTO toDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

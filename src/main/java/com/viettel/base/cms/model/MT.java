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
@Table(name = "mt")
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
public class MT extends BaseFWModel {

    @Id
    @Column(name = "mt_id", nullable = false)
    private Long mtId;

    @Column(name = "msisdn", nullable = true)
    private String msisdn;

    @Column(name = "message", nullable = true)
    private String message;

    @Column(name = "otp", nullable = true)
    private String otp;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time", nullable = true)
    private LocalDateTime sentTime;

    @Column(name = "retry_num", nullable = true)
    private Long retryNum;

    @Column(name = "status", nullable = true)
    private Long status;

    @Column(name = "type", nullable = true)
    private Long type;

    @Column(name = "app_id", nullable = true)
    private String appId;

    @Column(name = "channel", nullable = true)
    private String channel;

    @Column(name = "isdn_tail", nullable = true)
    private String isdnTail;

    @Column(name = "isdn_subtail", nullable = true)
    private String isdnSubtail;
    
    @Column(name = "action_type", nullable = true)
    private Long actionType;
    
    @Column(name = "action_isdn", nullable = true)
    private String actionIsdn;


    @Override
    public BaseFWDTO toDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

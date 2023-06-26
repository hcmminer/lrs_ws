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
@Table(name = "user_action_log")
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
public class UserActionLog extends BaseFWModel {

    @Id
    @Column(name = "user_action_id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_action_id_generator")
//    @SequenceGenerator(name = "user_action_id_generator",
//            sequenceName = "user_action_log_seq",
//            allocationSize = 1)
    private Long userActionId;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "user_ip", nullable = true)
    private String userIp;

    @Column(name = "user_browser", nullable = true)
    private String userBrowser;

    @Column(name = "module_name", nullable = true)
    private String moduleName;

    @Column(name = "action_name", nullable = true)
    private String actionName;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date", nullable = true)
    private LocalDateTime logDate;

    @Column(name = "login_type", nullable = true)
    private Long loginType;
    
    @Column(name = "application_name", nullable = true)
    private String applicationName;
    

    @Override
    public BaseFWDTO toDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

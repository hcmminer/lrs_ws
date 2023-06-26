package com.viettel.base.cms.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "application_log")
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
public class ApplicationLog {

    @Id
    @Column(name = "application_log_id", nullable = false)
    private Long applicationLogId;

    @Column(name = "application_code", nullable = true)
    private String applicationCode;

    @Column(name = "service_code", nullable = true)
    private String serviceCode;

    @Column(name = "thread_id", nullable = true)
    private String threadId;

    @Column(name = "request_id", nullable = true)
    private String requestId;

    @Column(name = "session_id", nullable = true)
    private String sessionId;

    @Column(name = "ip_port_parent_node", nullable = true)
    private String ipPortParentNode;

    @Column(name = "ip_port_current_node", nullable = true)
    private String ipPortCurrentNode;

    @Column(name = "request_content", nullable = true)
    private String requestContent;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "duration", nullable = true)
    private Long duration;

    @Column(name = "error_code", nullable = true)
    private Long errorCode;

    @Column(name = "error_description", nullable = true)
    private String errorDescription;

    @Column(name = "transaction_status", nullable = true)
    private Long transactionStatus;

    @Column(name = "action_name", nullable = true)
    private String actionName;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Column(name = "account", nullable = true)
    private String account;

    @Column(name = "thread_name", nullable = true)
    private String threadName;

    @Column(name = "source_class", nullable = true)
    private String sourceClass;

    @Column(name = "source_line", nullable = true)
    private String sourceLine;
    
    @Column(name = "source_method", nullable = true)
    private String sourceMethod;

    @Column(name = "client_request_id", nullable = true)
    private String clientRequestId;
    
     @Column(name = "service_provider", nullable = true)
    private String serviceProvider;
     
     @Column(name = "client_ip", nullable = true)
    private String clientIp;

    @Column(name = "transaction_id", nullable = true)
    private String transactionId;
    
    @Column(name = "response_content", nullable = true)
    private String responseContent;

    @Column(name = "transaction_type", nullable = true)
    private String transactionType;
    
     @Column(name = "system", nullable = true)
    private String system;
     
     @Column(name = "action_type", nullable = true)
    private String actionType;

    @Column(name = "date_type", nullable = true)
    private String dateType;
    
    @Column(name = "num_records", nullable = true)
    private String numRecords;

    @Column(name = "vsa", nullable = true)
    private String vsa;
    
     @Column(name = "data_extend", nullable = true)
    private String dataExtend;

}

package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class ActionLogDTO {

    private Long actionLogId;


    private Long staffId;


    private String isdn;

    private LocalDateTime createDatetime;


    private String ip;


    private String description;

    private Long rowId;


    private String createBy;


    private String createDatetimeStr;

    private String statusName;
}

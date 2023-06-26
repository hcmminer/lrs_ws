package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
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
public class StaffDTO {
    private Long staffId;
    private String staffCode;
    private String userName;
    private Long deptId;
    private String provinceCode;
    private String provinceName;
    private String telNumber;
    private String email;
    private String note;
    private Long status;
    private String roleCode;
    private LocalDateTime lastLoginDate;
}

package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "option_set")
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class OptionSetV1 {
    @Id
    @Column(name = "option_set_id")
    private Long optionSetId;

    @Column(name = "option_set_code",nullable = true)
    private String optionSetCode;

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

    @Column(name = "description", nullable = true)
    private String description;


}

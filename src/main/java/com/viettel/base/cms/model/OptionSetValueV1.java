package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "option_set_value")
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
//@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
//@AllArgsConstructor
@Component
public class OptionSetValueV1 {
    @Id
    @Column(name = "option_set_value_id")
    private Long optionSetValueId;

    @Column(name = "option_set_id")
    private Long optionSetId;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @Column(name = "status")
    private Long status;

    @Column(name = "create_datetime")
    private LocalDateTime createDatetime;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "description")
    private String description;

    @Column(name = "language")
    private String language;

    @Column(name = "name_vi")
    private String nameVi;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_la")
    private String nameLa;


}

package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "province")
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
public class ProvinceV1Model {
    @Id
    @Column(name = "pro_id")
    private Long proId;

    @Column(name = "pro_code")
    private String proCode;

    @Column(name = "pro_name")
    private String proName;

    @Column(name = "status")
    private String status;

    @Column(name = "create_datetime")
    private LocalDateTime createDatetime;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "update_by")
    private String updateBy;

}

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
@Table(name = "staff")
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
public class Staff extends BaseFWModel {

    @Id
    @Column(name = "staff_id", nullable = false, updatable = false)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staff_id_generator")
//    @SequenceGenerator(name = "staff_id_generator",
//            sequenceName = "staff_seq",
//            allocationSize = 1)
    private Long staffId;

    @Column(name = "staff_code", nullable = true)
    private String staffCode;

    @Column(name = "staff_name", nullable = true)
    private String staffName;
    
    @Column(name = "dept_id", nullable = true)
    private Long deptId;

    @Column(name = "province_code", nullable = true, updatable = false)
    private String provinceCode;

    @Column(name = "province_name", nullable = true, updatable = false)
    private String provinceName;

    @Column(name = "tel_number", nullable = true)
    private String telNumber;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "note", nullable = true, updatable = false)
    private String note;

    @Column(name = "status", nullable = true)
    private Long status;

    @Column(name = "role_code", nullable = true)
    private String roleCode;

    @Column(name = "last_login_date", nullable = true)
    private LocalDateTime lastLoginDate;

    @Override
    public BaseFWDTO toDTO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

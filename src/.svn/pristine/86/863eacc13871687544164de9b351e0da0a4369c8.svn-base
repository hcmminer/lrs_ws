package com.viettel.base.cms.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "systems_info")
// causes Lombok to implement the Builder design pattern for the Pojo class.
@Data
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class SystemsInfo  {

    @Id
    @Column(name = "system_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_id_generator")
    @SequenceGenerator(name = "system_id_generator",
            sequenceName = "systems_info_seq",
            allocationSize = 1)
    private Long systemId;

    @Column(name = "system_name", nullable = true)
    private String systemName;

    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "description", nullable = true)
    private String description;
    
    @Column(name = "status", nullable = false)
    private String status;

}

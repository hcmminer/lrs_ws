package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "province_district")
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
public class District {
    @Id
    @Column(name = "dist_id", nullable = false)
    private Long distId;
    @Column(name = "pro_id", nullable = false)
    private Long proId;
    @Column(name = "dist_name", nullable = false)
    private String distName;
    @Column(name = "center_point", nullable = true)
    private String centerPoint;
    @Column(name = "def_zoom", nullable = true)
    private Long defZoom;
}

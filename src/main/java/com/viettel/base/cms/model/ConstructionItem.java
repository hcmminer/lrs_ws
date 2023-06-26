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
import java.time.LocalDateTime;

@Entity
@Table(name = "construction_item")
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
public class ConstructionItem {
    @Id
    @Column(name = "construction_item_id", nullable = false)
    private Long constructionItemId;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "step", nullable = true)
    private Long step;

    @Column(name = "created_date", nullable = true)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = true)
    private String createdBy;

    @Column(name = "last_modified_date", nullable = true)
    private LocalDateTime lastModifiedDate;

    @Column(name = "last_modified_by", nullable = true)
    private String lastModifiedBy;

    @Column(name = "status", nullable = true)
    private Long status;

    @Column(name = "type", nullable = true)
    private Long type;
}

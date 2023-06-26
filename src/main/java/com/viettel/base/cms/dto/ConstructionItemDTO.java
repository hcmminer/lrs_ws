package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class ConstructionItemDTO {
    private Long constructionItemId;
    private String constructionItemName;
    private Long step;
    private Long status;
    private Long chosen;
    private LocalDateTime createDatetime;
    private String createBy;
    private LocalDateTime lastModifiedDatetime;
    private String lastModifiedBy;
    private Long type;
    private List<ConstructionItemDTO> lstConstructionItem2;
}

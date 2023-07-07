package com.viettel.base.cms.dto;

import com.viettel.base.cms.model.Construction;
import com.viettel.base.cms.model.ConstructionDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
public class CommonInputDTO {

    private String userName;
    private String password;
    private String constructionTypeId;
    private OptionSetValueDTO optionSetValueDTO;
    private ConstructionTypeDTO constructionTypeDTO;
    private ConstructionDTO constructionDTO;
    private ProvinceDTO provinceDTO;
    private BTSStationDTO btsStationDTO;
    private ConstructionDetailDTO constructionDetailDTO;
    private List<ConstructionDetailDTO> listConstructionDetail;
    private List<BTSStationDTO> btsStationDTOList;
    private String stationCode;
    private Long imageId;
    private Long fileId;
    private String language;
    private String filePath;
    private String rejectReason;
    private String description;
    private Long constructionId;
    private Long constructionItemId;
    private List<ImageDTO> imageDTOList;
    private String appCode;
    private Long provinceId;
    private DistrictDTO districtDTO;
    private CommuneDTO communeDTO;
    private DataParams dataParams;
}

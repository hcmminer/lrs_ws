package com.viettel.base.cms.dto;

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
public class ComboBoxData {
    private List<ConstructionTypeDTO> constructionTypeDTOList;
    private List<OptionSetValueDTO> listConstructionStatus;
    private List<OptionSetValueDTO> stationTypeList;
    private List<OptionSetValueDTO> columnTypeList;
    private List<OptionSetValueDTO> positionTypeList;
    private List<ProvinceDTO> provinceDTOList;
    private List<OptionSetValueDTO> listBTSStationStatus;
    private List<OptionSetValueDTO> listApprovedStationStatus;
    private List<OptionSetValueDTO> listYesOrNo;

}

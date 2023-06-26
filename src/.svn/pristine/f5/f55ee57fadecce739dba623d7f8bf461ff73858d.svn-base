package com.viettel.base.cms.dto;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.vfw5.base.utils.StringUtils;
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
public class ConstructionDTO {
    private Long constructionId;
    private String constructionCode;
    private String constructionName;
    private Long positionType;
    private String positionTypeName;
    private Long stationType;
    private String stationTypeName;
    private Long columnType;
    private String columnTypeName;
    private String columnHeight;
    private String constructionLong;
    private String constructionLat;
    private String provinceCode;
    private Long district;
    private String districtName;
    private String village;
    private Long constructionType;
    private String constructionTypeName;
    private String network;
    private String vendor;
    private String band;
    private String antenHeight;
    private String azimuth;
    private String tilt;
    private Long sector;
    private String trxMode;
    private String startPoint;
    private String endPoint;
    private String cableRoute;
    private String distanceCable;
    private Long columnNumber;
    private String note;
    private String decisionDeploy;
    private LocalDateTime startDate;
    private String startBy;
    private LocalDateTime completeDate;
    private String completeBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private Long status;
    private String statusName;
    private List<String> listConstructionItemName;
    private String fileContent;
    private List<ConstructionDetailDTO> listConstructionDetailDTO;
    private List<ConstructionItemDTO> listConstructionItemDTO;
    private List<Long> listConstructionItemId;
    private String provinceName;
    private String startDateStr;
    private String completeDateStr;
    private String createdDateStr;
    private String lastModifiedDateStr;
    private Long itemCount;

    public void setStartDate(LocalDateTime startDate) {
        if (!StringUtils.isStringNullOrEmpty(startDate)) {
            this.startDateStr = FunctionUtils.localDateTimeToString(startDate, Constant.DATETIME_PATTERN_FOR_START_DATE);
            this.startDate = startDate;
        }
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        if (!StringUtils.isStringNullOrEmpty(createdDate)) {
            this.createdDateStr = FunctionUtils.localDateTimeToString(createdDate, Constant.DATETIME_PATTERN);
            this.createdDate = createdDate;
        }
    }

    public void setCompleteDate(LocalDateTime completeDate) {
        if (!StringUtils.isStringNullOrEmpty(completeDate)) {
            this.completeDateStr = FunctionUtils.localDateTimeToString(completeDate, Constant.DATETIME_PATTERN);
            this.completeDate = completeDate;
        }
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        if (!StringUtils.isStringNullOrEmpty(lastModifiedDate)) {
            this.lastModifiedDateStr = FunctionUtils.localDateTimeToString(lastModifiedDate, Constant.DATETIME_PATTERN);
            this.lastModifiedDate = lastModifiedDate;
        }
    }
}

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
public class ConstructionDetailDTO {
    private Long constructionDetailId;
    private Long constructionId;
    private Long constructionItemId;
    private LocalDateTime startDate;
    private String startBy;
    private LocalDateTime acceptanceDate;
    private String acceptanceBy;
    private LocalDateTime firstApprovedDate;
    private String firstApprovedBy;
    private LocalDateTime firstRejectDate;
    private String firstRejectBy;
    private String firstRejectReason;
    private LocalDateTime secondApprovedDate;
    private String secondApprovedBy;
    private LocalDateTime secondRejectDate;
    private String secondRejectBy;
    private String secondRejectReason;
    private LocalDateTime thirdApprovedDate;
    private String thirdApprovedBy;
    private LocalDateTime thirdRejectDate;
    private String thirdRejectBy;
    private String thirdRejectReason;
    private LocalDateTime createdDate;
    private String createdBy;
    private String imagePath;
    private Long status;
    private List<ImageDTO> listImageDTO;
    private Long approveType;
    private String name;
    private String statusName;
    private String rejectReason;
    private LocalDateTime rejectDate;
    private String rejectBy;
    private String isRequested;
    private String firstApprovedDateStr;
    private String startDateStr;
    private String acceptanceDateStr;
    private String firstRejectDateStr;
    private String secondApprovedDateStr;
    private String secondRejectDateStr;
    private String thirdApprovedDateStr;
    private String thirdRejectDateStr;
    private String createdDateStr;
    private String rejectDateStr;

    private String completeDateStr;

    private Long step;

    private String statusNameSuper;
    private Long parentId;
    private LocalDateTime completeDate;
    private String completeBy;

    private List<ConstructionDetailDTO> listItemDetailDTO;

    private List<String> listConstructionItemName;

    public void setStartDate(LocalDateTime startDate) {
        if (!StringUtils.isStringNullOrEmpty(startDate)) {
            this.startDateStr = FunctionUtils.localDateTimeToString(startDate, Constant.DATETIME_PATTERN_FOR_START_DATE);
            this.startDate = startDate;
        }
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        if (!StringUtils.isStringNullOrEmpty(acceptanceDate)) {
            this.acceptanceDateStr = FunctionUtils.localDateTimeToString(acceptanceDate, Constant.DATETIME_PATTERN);
            this.acceptanceDate = acceptanceDate;
        }
    }

    public void setFirstApprovedDate(LocalDateTime firstApprovedDate) {
        if (!StringUtils.isStringNullOrEmpty(firstApprovedDate)) {
            this.firstApprovedDateStr = FunctionUtils.localDateTimeToString(firstApprovedDate, Constant.DATETIME_PATTERN);
            this.firstApprovedDate = firstApprovedDate;
        }
    }

    public void setFirstRejectDate(LocalDateTime firstRejectDate) {
        if (!StringUtils.isStringNullOrEmpty(firstRejectDate)) {
            this.firstRejectDateStr = FunctionUtils.localDateTimeToString(firstRejectDate, Constant.DATETIME_PATTERN);
            this.firstRejectDate = firstRejectDate;
        }
    }

    public void setSecondApprovedDate(LocalDateTime secondApprovedDate) {
        if (!StringUtils.isStringNullOrEmpty(secondApprovedDate)) {
            this.secondApprovedDateStr = FunctionUtils.localDateTimeToString(secondApprovedDate, Constant.DATETIME_PATTERN);
            this.secondApprovedDate = secondApprovedDate;
        }
    }

    public void setSecondRejectDate(LocalDateTime secondRejectDate) {
        if (!StringUtils.isStringNullOrEmpty(secondRejectDate)) {
            this.secondRejectDateStr = FunctionUtils.localDateTimeToString(secondRejectDate, Constant.DATETIME_PATTERN);
            this.secondRejectDate = secondRejectDate;
        }
    }

    public void setThirdApprovedDate(LocalDateTime thirdApprovedDate) {
        if (!StringUtils.isStringNullOrEmpty(thirdApprovedDate)) {
            this.thirdApprovedDateStr = FunctionUtils.localDateTimeToString(thirdApprovedDate, Constant.DATETIME_PATTERN);
            this.thirdApprovedDate = thirdApprovedDate;
        }
    }

    public void setThirdRejectDate(LocalDateTime thirdRejectDate) {
        if (!StringUtils.isStringNullOrEmpty(thirdRejectDate)) {
            this.thirdRejectDateStr = FunctionUtils.localDateTimeToString(thirdRejectDate, Constant.DATETIME_PATTERN);
            this.thirdRejectDate = thirdRejectDate;
        }
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        if (!StringUtils.isStringNullOrEmpty(createdDate)) {
            this.createdDateStr = FunctionUtils.localDateTimeToString(createdDate, Constant.DATETIME_PATTERN);
            this.createdDate = createdDate;
        }
    }

    public void setRejectDate(LocalDateTime rejectDate) {
        if (!StringUtils.isStringNullOrEmpty(rejectDate)) {
            this.rejectDateStr = FunctionUtils.localDateTimeToString(rejectDate, Constant.DATETIME_PATTERN);
            this.rejectDate = rejectDate;
        }
    }

    public void setCompleteDate(LocalDateTime completeDate) {
        if (!StringUtils.isStringNullOrEmpty(completeDate)) {
            this.completeDateStr = FunctionUtils.localDateTimeToString(completeDate, Constant.DATETIME_PATTERN);
            this.completeDate = completeDate;
        }
    }

}

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
@Table(name = "construction_detail_his")
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
public class ConstructionDetailHis {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "deleted_date", nullable = false)
    private LocalDateTime deletedDate;
    @Column(name = "deleted_by", nullable = false)
    private String deletedBy;
    @Column(name = "construction_detail_id", nullable = false)
    private Long constructionDetailId;
    @Column(name = "construction_id", nullable = false, updatable = false)
    private Long constructionId;
    @Column(name = "construction_item_id", nullable = false, updatable = false)
    private Long constructionItemId;
    @Column(name = "start_date", nullable = true)
    private LocalDateTime startDate;
    @Column(name = "start_by", nullable = true)
    private String startBy;
    @Column(name = "acceptance_date", nullable = true)
    private LocalDateTime acceptanceDate;
    @Column(name = "acceptance_by", nullable = true)
    private String acceptanceBy;
    @Column(name = "first_approved_date", nullable = true)
    private LocalDateTime firstApprovedDate;
    @Column(name = "first_approved_by", nullable = true)
    private String firstApprovedBy;
    @Column(name = "first_reject_date", nullable = true)
    private LocalDateTime firstRejectDate;
    @Column(name = "first_reject_by", nullable = true)
    private String firstRejectBy;
    @Column(name = "first_reject_reason", nullable = true)
    private String firstRejectReason;
    @Column(name = "second_approved_date", nullable = true)
    private LocalDateTime secondApprovedDate;
    @Column(name = "second_approved_by", nullable = true)
    private String secondApprovedBy;
    @Column(name = "second_reject_date", nullable = true)
    private LocalDateTime secondRejectDate;
    @Column(name = "second_reject_by", nullable = true)
    private String secondRejectBy;
    @Column(name = "second_reject_reason", nullable = true)
    private String secondRejectReason;
    @Column(name = "third_approved_date", nullable = true)
    private LocalDateTime thirdApprovedDate;
    @Column(name = "third_approved_by", nullable = true)
    private String thirdApprovedBy;
    @Column(name = "third_reject_date", nullable = true)
    private LocalDateTime thirdRejectDate;
    @Column(name = "third_reject_by", nullable = true)
    private String thirdRejectBy;
    @Column(name = "third_reject_reason", nullable = true)
    private String thirdRejectReason;
    @Column(name = "created_date", nullable = true, updatable = false)
    private LocalDateTime createdDate;
    @Column(name = "created_by", nullable = true, updatable = false)
    private String createdBy;
    @Column(name = "image_path", nullable = true)
    private String imagePath;
    @Column(name = "status", nullable = true)
    private Long status;
}

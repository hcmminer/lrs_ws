package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DataParams {
    private int currentPage;
    private int pageLimit;
    private int startRow;
    private int endRow;
    private int pageTotal;
    private int recordTotal;

}
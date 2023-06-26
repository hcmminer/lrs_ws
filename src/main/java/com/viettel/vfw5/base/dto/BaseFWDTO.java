/*
 * Copyright (C) 2012 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.vfw5.base.dto;

import com.viettel.vfw5.base.model.BaseFWModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author thieulq
 * @version 1.0
 * @since May 2012
 */
public abstract class BaseFWDTO<TBO extends BaseFWModel> implements BaseFWDTOInterface<TBO> {


  @Override
  public int compareTo(BaseFWDTOInterface o) {
    return catchName().compareTo(o.catchName());
  }

  public Set convertListDTOtoModel(List<? extends BaseFWDTO> listDTO) {

    Set<BaseFWModel> lstModel = new HashSet<>();
    if (listDTO != null) {
      for (BaseFWDTO dto : listDTO) {
        lstModel.add(dto.toModel()
        );
      }
    }
    return lstModel;
  }

}

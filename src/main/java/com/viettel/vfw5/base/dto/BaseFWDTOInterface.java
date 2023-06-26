/*
* Copyright (C) 2011 Viettel Telecom. All rights reserved.
* VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package com.viettel.vfw5.base.dto;

import com.viettel.vfw5.base.model.BaseFWModel;
import java.util.Locale;

/**
*
* @author thieulq
* @version 1.0
* @since since_text
*/
public interface BaseFWDTOInterface<TModel extends BaseFWModel> extends Comparable<BaseFWDTOInterface> {
    TModel toModel();
    String catchName();
//    long getChangedTime();
//    void setChangedTime(long changedTime);
}
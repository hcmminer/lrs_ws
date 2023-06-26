package com.viettel.vfw5.base.model;

import com.viettel.vfw5.base.dto.BaseFWDTO;

/**
*
* @author thieulq
* @version 1.0
* @since May 2012
*/
public interface BaseFwModelInterface<TDTO extends BaseFWDTO> extends java.io.Serializable {
    public TDTO toDTO();
}

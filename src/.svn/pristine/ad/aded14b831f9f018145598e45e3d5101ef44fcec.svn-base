package com.viettel.vfw5.base.service;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.model.BaseFWModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;

/**
 *
 * @author thieulq
 * @version 1.0
 * @since May 2012
 */
public interface BaseFWServiceInterface<TDTO extends BaseFWDTO, TModel extends BaseFWModel> {

    public List<TDTO> convertListModeltoDTO(List<TModel> listModel);

    public List<TModel> convertListDTOtoModel(List<TDTO> listDTO);

   
}

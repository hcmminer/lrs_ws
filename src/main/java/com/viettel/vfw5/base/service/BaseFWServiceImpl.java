package com.viettel.vfw5.base.service;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.model.BaseFWModel;

import java.util.*;

public class BaseFWServiceImpl<TDTO extends BaseFWDTO, TModel extends BaseFWModel>
        implements BaseFWServiceInterface<TDTO, TModel> {

    @Override
    public List convertListModeltoDTO(List<TModel> listModel) {
        List<BaseFWDTO> lstForm = new ArrayList<BaseFWDTO>();
        if (listModel != null) {
            for (TModel model : listModel) {
                lstForm.add(model.toDTO());
            }
        }
        return lstForm;
    }

    @Override
    public List convertListDTOtoModel(List<TDTO> listDTO) {

        List<BaseFWModel> lstModel = new ArrayList<BaseFWModel>();
        if (listDTO != null) {
            for (TDTO dto : listDTO) {
                lstModel.add(dto.toModel());
            }
        }
        return lstModel;
    }
}

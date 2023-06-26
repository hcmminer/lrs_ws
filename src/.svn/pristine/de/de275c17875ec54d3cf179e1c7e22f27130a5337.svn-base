
package com.viettel.vfw5.base.model;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author thieulq
 * @version 1.0
 * @since May 2012
 */
public abstract class BaseFWModel implements BaseFwModelInterface {

    private transient String colId = "ID";
    private transient String colName = "NAME";
    private transient String[] uniqueColumn = new String[0];

    public String getModelName() {
        return this.getClass().getSimpleName();
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String[] getUniqueColumn() {
        return uniqueColumn;
    }

    public void setUniqueColumn(String[] uniqueColumn) {
        this.uniqueColumn = uniqueColumn;
    }

    public List convertListModeltoDTO(Set<? extends BaseFWModel> setModel) {
        List<BaseFWDTO> lstModel = new ArrayList<BaseFWDTO>();
        if (setModel != null) {
            for (BaseFWModel item : setModel) {
                lstModel.add(item.toDTO());
            }
        }
        return lstModel;
    }
}

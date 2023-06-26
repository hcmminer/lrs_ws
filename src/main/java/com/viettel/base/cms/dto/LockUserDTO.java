
package com.viettel.base.cms.dto;

import com.viettel.base.cms.model.LockUser;
import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.utils.StringUtils;

import java.time.LocalDateTime;


public class LockUserDTO extends BaseFWDTO<LockUser> {

    private Long lockUserId;
    private String userName;
    private String typeLock;
    private String valueWrong;
    private LocalDateTime requestTime;
    private String updateBy;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLockUserId() {
        return lockUserId;
    }

    public void setLockUserId(Long lockUserId) {
        this.lockUserId = lockUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTypeLock() {
        return typeLock;
    }

    public void setTypeLock(String typeLock) {
        this.typeLock = typeLock;
    }

    public String getValueWrong() {
        return valueWrong;
    }

    public void setValueWrong(String valueWrong) {
        this.valueWrong = valueWrong;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LockUserDTO() {
    }

    public LockUserDTO(Long lockUserId, String userName, String typeLock, String valueWrong, LocalDateTime requestTime, String updateBy, String status) {
        this.lockUserId = lockUserId;
        this.userName = userName;
        this.typeLock = typeLock;
        this.valueWrong = valueWrong;
        this.requestTime = requestTime;
        this.updateBy = updateBy;
        this.status = status;
    }


    @Override
    public LockUser toModel() {
        try {
            LockUser model = new LockUser(
                    !StringUtils.validString(lockUserId) ? null : lockUserId,
                    userName,
                    typeLock,
                    !StringUtils.validString(valueWrong) ? null : valueWrong,
                    !StringUtils.validString(requestTime) ? null : requestTime,
                    !StringUtils.validString(status) ? null : status,
                    !StringUtils.validString(updateBy) ? null : updateBy
            );
            return model;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String catchName() {
        return String.valueOf(lockUserId);
    }

}

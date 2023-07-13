/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.common;

import java.io.FilenameFilter;

/**
 * @author ADMIN
 */
public class Constant {

    public static final String PACKAGE_MODEL_UPOINT = "com.viettel.base.cms.model";
    public static final String UNIT_NAME_ENTITIES_CMS = "UNIT_CMS";
    public static final String PACKAGE_REPO_UPOINT = "com.viettel.base.cms.repo";
    public static final String SYSTEM = "CMS";
    public static final Long ACTIVE = 1L;
    public static final Long QRTIME_CHECK = 60000L;
    public static final Long TIME_CHECK_OTP = 60000L;
    public static final Long TIME_CHECK_LOCK_USER = 1800000L;
    public static final Long UPOINT_OTP_CONFIRM_MERCHANT = 50000L;
    public static final Long UPOINT_OTP_CONFIRM_CUSTOMER = 50000L;
    public static final Long TIMEOUT_OTP = 180000L;
    public static final Long LOGIN_COUNT = 3L;
    public static final int SPAM_TIME = 30;
    public static final int COUNT_LOGIN = 3;
    public static final int COUNT_OTP = 5;
    public static final int TIME_LOCK_USER = 1800000;
    /*
    HieuNT
     */
    public static final String DATETIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final String DATETIME_PATTERN_FOR_START_DATE = "dd-MM-yyyy";
    public static final String DATETIME_PATTERN_DMY = "dd/MM/yyyy HH:mm:ss";

    public final class ACTION {

        public final static int CREATE_CONSTRUCTION = 1;
        public final static int UPDATE_CONSTRUCTION = 2;
        public final static int CHECK_CONSTRUCTION = 3;
        public final static int CHECK_CONSTRUCTION_STARTDATE = 4;
        public final static int CHECK_CONSTRUCTION_ITEM_STARTDATE = 5;
        public final static int CREATE_CONSTRUCTION_BY_FILE = 6;

    }

    public final class CONSTRUCTION {

        public final static int STATUS_DELETE = 0;
        public final static int STATUS_CREATED = 1;
        public final static int STATUS_ASSIGNED = 2;
        public final static int STATUS_ACTIVE = 3;
        public final static int STATUS_COMPLETED = 4;
    }

    public final class CONSTRUCTION_DETAIL {

        public final static int STATUS_CREATED = 0;
        public final static int STATUS_ACTIVE = 1;
        public final static int STATUS_REQUESTED = 2;
        public final static int STATUS_ACCEPTED_1 = 3;
        public final static int STATUS_REJECTED_1 = 4;
        public final static int STATUS_ACCEPTED_2 = 5;
        public final static int STATUS_REJECTED_2 = 6;
        public final static int STATUS_ACCEPTED_3 = 7;
        public final static int STATUS_REJECTED_3 = 8;

        public final static int STATUS_COMPLETE = 9;
    }

    public final class CONSTRUCTION_ITEM_STATUS {

        public final static int NOT_CHOSEN = 0;
        public final static int CHOSEN = 1;
    }

    public final class STAFF_STATUS {

        public final static int INACTIVE = 0;
        public final static int ACTIVE = 1;
    }

    public final class IMAGE_STATUS {

        public final static int INACTIVE = 0;
        public final static int ACTIVE = 1;
    }

    public final class CMS_ROLES {

        public final static String CMS_CORP_STAFF = "CMS_CORP_STAFF";
        public final static String CMS_PROV_VICE_PRESIDENT = "CMS_PROV_VICE_PRESIDENT";
        public final static String CMS_PROV_INFA_STAFF = "CMS_PROV_INFA_STAFF";
        public final static String CMS_PROV_TECH_STAFF = "CMS_PROV_TECH_STAFF";
    }

    public final class BTS_ROLES {

        public final static String CMS_BTS_PNO_STAFF = "CMS_BTS_PNO_STAFF";
        public final static String CMS_BTS_TCCN_STAFF = "CMS_BTS_TCCN_STAFF";
        public final static String CMS_BTS_CN_STAFF = "CMS_BTS_CN_STAFF";
        public final static String CMS_BTS_CND_STAFF = "CMS_BTS_CND_STAFF";
        public final static String CMS_BTS_GRAND_TC_STAFF = "CMS_BTS_GRAND_TC_STAFF";

        public final static String CMS_BTS_NOC_STAFF = "CMS_BTS_NOC_STAFF";
    }

    public final class CMS_OPTION_SET {

        public final static String COLUMN_TYPE = "COLUMN_TYPE";
        public final static String POSITION_TYPE = "POSITION_TYPE";
        public final static String STATION_TYPE = "STATION_TYPE";
        public final static String CONSTRUCTION_STATUS = "CONSTRUCTION_STATUS";
        public final static String APPROVED_STATUS = "APPROVED_STATUS";
        public final static String STATION_STATUS = "STATION_STATUS";
        public final static String CONSTRUCTION_DETAIL_STATUS = "CONSTRUCTION_DETAIL_STATUS";
        public final static String YES_OR_NO = "YES_OR_NO";

        public final static String REJECT_REASON_IMAGE = "REJECT_REASON_IMAGE";

    }

    public final class SMS_CONFIG {
        public final static String SMS = "SMS";
        public final static String PGD_ASSIGN_COMPLETE = "PGD_ASSIGN_COMPLETE";
        public final static String KTT_START_MONITOR = "KTT_START_MONITOR";
        public final static String REQUEST_APPROVE = "REQUEST_APPROVE";
        public final static String APPROVE_SMS = "APPROVE_SMS";
        public final static String REJECT_SMS = "REJECT_SMS";
        public final static String ACCEPTANCE_SMS = "ACCEPTANCE_SMS";

        public final static String TCCN_UPDATE_COMPLETE = "TCCN_UPDATE_COMPLETE";

        public final static String CND_APPROVED_BTS = "CND_APPROVED_BTS";
        public final static String APP_ID = "SEND_SMS";
        public final static int SMS_TYPE = 3;
    }

    public final class APPROVE_TYPE {
        public final static int NOT_APPROVAL = 0;
        public final static int APPROVE = 1;
        public final static int REJECT = 2;
    }


    public final class APPROVED_VALUE {
        public final static String NOT_APPROVAL = "0";
        public final static String APPROVE = "1";
        public final static String REJECT = "2";
    }

    public final class STATUS_VALUE {
        public final static String WORKING = "1";
        public final static String TURN_OFF = "0";
    }

    public final class VALIDATE_CONSTRUCTION_STATUS {

        public final static int NOT_VALIDATE = 0;
        public final static int VALIDATE = 1;
    }

    public final class EXECUTION_RESULT {

        public final static int SUCCESS = 1;
        public final static int ERROR = 0;
    }

    public final class EXECUTION_ERROR {

        public final static String SUCCESS = "0";
        public final static String ERROR = "1";
        public final static String LOCK_USER = "501";
        public final static String END_TRANS = "204";
    }

    public final class EXECUTION_MESSAGE {

        public final static String OK = "OK";
        public final static String FAIL = "FAIL";
    }

    public final class RESPONSE {

        public final static String SUCCESS = "0";
        public final static String STATE_BLOCK = "0";
        public final static String STATE_NEW = "1";
        public final static String STATE_USED = "2";
        public final static String STATE_NOT_EXIT = "3";
        public final static String SUB_PRE = "1";
        public final static String SUB_POS = "2";
        public final static String SUB_HP = "3";
    }

    public final class MOCHA {

        public final static String USER = "GTS";
        public final static String PASSWORD = "GTS@#qaw2019";
        public final static String SERVICE_ID = "Upoint";

    }

    public final class UNIID {

        public final static String IP = "10.120.44.68";

    }

    public final class MESSAGE {

        public final static String otp = "OTP";
        public final static String message = "MESSAGE";
        public final static String CONTENT_SENDER = "UPOINT_EXCHANGE_SENDER";
        public final static String CONTENT_RECEIVER = "UPOINT_EXCHANGE_RECEIVER";
        public final static String CONTENT_TOPUP = "UPOINT_EXCHANGE_TOPUP";
        public final static String messageAddCtv = "Add new collaborators";
    }

    public final class OPTION_SET {

        public final static String terms = "TERM AND CONDITIONS";
        public final static String guideline = "GUIDELINE";
        public final static String guideline_CreatingAccounts = "Creating Accounts";
        public final static String guideline_ProductService = "Product Service";
        public final static String guideline_Upload_Photo = "Upload Photo";
        public final static String guideline_VerifyingOA = "Verifying OA";
        public final static String gender = "GENDER";
        public final static String paper_type = "PAPER_TYPE";
        public final static String upoint_otp_confirm_merchant = "UPOINT_OTP_CONFIRM_MERCHANT";
        public final static String upoint_otp_confirm_customer = "UPOINT_OTP_CONFIRM_CUSTOMER";
        public final static String upoint_otp_confirm_time = "UPOINT_OTP_CONFIRM_TIME";
        public final static String upoint_otp_expired = "UPOINT_OTP_EXPIRED";
        public final static String wrong_request = "WRONG_REQUEST";
        public final static String lock_user_wrong_otp = "LOCK_USER_WRONG_OTP";
        public final static String time_lock_user = "TIME_LOCK_USER";

        public final static String config_time = "CONFIG_TIME";
        public final static String config_count = "CONFIG_COUNT";
        public final static String otp_expired = "OTP_EXPIRED";
        public final static String lock_user = "LOCK_USER";
        public final static String time_spam = "TIME_SPAM";
        public final static String login_count = "LOGIN_COUNT";
        public final static String otp_count = "OTP_COUNT";

    }

    public final class TYPE_LOCK {

        public final static String login = "L";
        public final static String otp = "O";
    }

    public final static String APP_ID = "GSCT";

    public final class DEPT_CODE {
        public final static String PNO = "PNO";
        public final static String CND = "CND";
        public final static String TC = "TC";
        public final static String NOC = "NOC";
    }

    public final static int RETRY = 5;

    public static class FTP_CONFIG {
        public final static String PRE = "/u01/pms/tomcat9-8888-view-image/webapps";
    }
    public static int DEFAULT_CURRENT_PAGE = 1;
    public static int DEFAULT_PAGE_LIMIT = 10;

}

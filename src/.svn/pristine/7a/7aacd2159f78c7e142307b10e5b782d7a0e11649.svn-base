/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.vfw5.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author ADMIN
 */
@Component
public class DataUtils {

    @Autowired
    private static Environment env;

    private static String linkService = "";
    private static int timeout = 5000;

    public static Long getSequence(EntityManager cms, String sequenceName) {
        String sql = "select NEXTVAL(" + sequenceName + ")";
        Query query = cms.createNativeQuery(sql);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public static String genPasswordForCtv(String isdn) {
        int min = 10;
        int max = 99;
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        String pass = "UT" + isdn + String.valueOf(random_int);
        return pass;
    }

    public static String genOTP() {
        int min = 100000;
        int max = 999999;
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        String OTP = String.valueOf(random_int);
        return OTP;
    }

    public static String upointTransCode(String seq) {
//        Date now = Calendar.getInstance().getTime();
//        String strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
        String code = "UT" + seq;
        return code;
    }

    public static Date string2Date(String date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(DataUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static LocalDateTime stringToLocalDateTme(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
    }

    public static String getSystemDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    public static String getStringDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime = date.format(formatter).toString();
        return formattedDateTime;
    }

    public static String getStringDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = date.format(formatter).toString();
        return formattedDateTime;
    }

    public static Long getLong(Object obj) {
        if (obj != null) {
            return Long.parseLong(obj.toString());
        }
        return null;
    }

    public static String getString(Object obj) {
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    public static Double getDouble(Object obj) {
        if (obj != null) {
            return Double.parseDouble(obj.toString());
        }
        return null;
    }

    public static String getMsisdn(String isdn) {
        String msisdn = "";
        if (isdn.startsWith("(+856)")) {
            msisdn = isdn.replace("(+856)", "856");
        } else if (isdn.startsWith("+856")) {
            msisdn = isdn.replace("+856", "856");
        } else if (isdn.startsWith("20")) {
            msisdn = "856" + isdn;
        } else if (isdn.startsWith("0")) {
            msisdn = "856" + isdn.substring(1);
        } else if (isdn.startsWith("9")) {
            msisdn = "85620" + isdn;
        } else if (isdn.startsWith("7")) {
            msisdn = "85620" + isdn;
        } else if (isdn.startsWith("5")) {
            msisdn = "85620" + isdn;
        } else if (isdn.startsWith("2") && isdn.length() == 8) {
            msisdn = "85620" + isdn;
        } else {
            msisdn = isdn;
        }

        return msisdn;
    }

    public static String getIsdn(String isdn) {
        String msisdn = "";
        if (isdn.startsWith("(+856)")) {
            msisdn = isdn.replace("(+856)", "");
        } else if (isdn.startsWith("+856")) {
            msisdn = isdn.replace("+856", "");
        } else if (isdn.startsWith("856")) {
            msisdn = isdn.substring(3);
        } else if (isdn.startsWith("0")) {
            msisdn = isdn.substring(1);
        } else if (isdn.startsWith("9")) {
            msisdn = "20" + isdn;
        } else if (isdn.startsWith("7")) {
            msisdn = "20" + isdn;
        } else if (isdn.startsWith("5")) {
            msisdn = "20" + isdn;
        } else if (isdn.startsWith("2") && isdn.length() == 8) {
            msisdn = "20" + isdn;
        } else {
            msisdn = isdn;
        }
        return msisdn;
    }

    public static String createInQuery(List<String> objs) {
        String inQuery = "";
        if (objs != null && objs.size() > 0) {
            inQuery = " AND user_name IN (  " + String.join(" , ", objs) + ") ";
        }

        return inQuery;
    }

    public static String objectToJsonL(Object o) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(o);
    }

    // game voucher
    public static String genToken(String text) {
        String token = "";
        int min = 10;
        int max = 99;
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        token = "voucherGame" + text + random_int;
        return token;
    }

    public static String convertIdToLang(String LangId) {
        String lang = "";
        if (!StringUtils.isNullOrEmpty(LangId)) {
            switch (LangId) {
                case "57":
                    lang = "la";
                    break;
                case "84":
                    lang = "vi";
                    break;
                case "89":
                    lang = "en";
                    break;
                case "86":
                    lang = "zh";
                    break;
                default:
                    lang = "la";
            }
        }
        return lang;
    }

    public static byte[] getBytes(Object object) {
        try {
            byte[] returnByte = (byte[]) object;
            return returnByte;
        } catch (Exception e) {
            return null;
        }
    }
    // end game voucher
}

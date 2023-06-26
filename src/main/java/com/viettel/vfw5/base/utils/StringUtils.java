/*
 * Copyright (C) 2012 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.vfw5.base.utils;

import com.viettel.base.dto.custom.InputDTO;
import com.viettel.base.dto.custom.SubScriberDTO;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author binhnt22@viettel.com.vn
 * @since May 2012
 * @version 1.1
 */
public final class StringUtils {

    /**
     * alphabeUpCaseNumber.
     */
 private static String alphabeUpCaseNumber = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String mask = "0123456789_aAÃ¡Ã?Ã Ã€áº£áº¢áº¡áº Ã£ÃƒÃ¢Ã‚áº¥áº¤áº§áº¦áº©áº¨áº­áº¬áº«áºªÄƒÄ‚áº¯áº®áº±áº°áº³áº²áº·áº¶áºµáº´bBcCdDÄ‘Ä?eEÃ©Ã‰Ã¨Ãˆáº»áººáº¹áº¸áº½áº¼ÃªÃŠáº¿áº¾á»?á»€á»ƒá»‚á»‡á»†á»…á»„fFgGhHiIÃ­Ã?Ã¬ÃŒá»‰á»ˆá»‹IHÄ©Ä¨jJkKlLmMnNoOÃ³Ã“Ã²Ã’á»?á»Žá»?á»ŒÃµÃ•Ã´Ã”á»‘á»?á»“á»’á»•á»”á»™á»˜á»—á»–Æ¡Æ á»›á»šá»?á»œá»Ÿá»žá»£á»¢á»¡á» pPqQrRsStTuUÃºÃšÃ¹Ã™á»§á»¦á»¥á»¤Å©Å¨Æ°Æ¯á»©á»¨á»«á»ªá»­á»¬á»±á»°á»¯á»®vVwWxXyYÃ½Ã?á»³á»²á»·á»¶á»µá»´á»¹á»¸zZ";
    private static String maskEN = "0123456789_aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ";
    /**
     * ZERO.
     */
    private static final String ZERO = "0";
    private static final String c[] = {"<", ">"};
    private static final String expansion[] = {"&lt;", "&gt;"};

    public static String HTMLEncode(String s) {
        String sTemp = s;
        for (int j = 0; j < c.length; j++) {
            sTemp = sTemp.replace(c[j], expansion[j]);
        }
        return sTemp;
    }

    public static String HTMLDecode(String s) {
        String mine = s;
        for (int i = 0; i < c.length; i++) {
            mine.replaceAll(expansion[i], (c[i] + ""));
        }
        return mine;
    }

    /**
     * method compare two string
     *
     * @param str1 String
     * @param str2 String
     * @return boolean
     */
    public static boolean compareString(String str1, String str2) {
        String str1Temp = str1;
        String str2Temp = str2;
        if (str1Temp == null) {
            str1Temp = "";
        }
        if (str2Temp == null) {
            str2Temp = "";
        }

        if (str1Temp.equals(str2Temp)) {
            return true;
        }
        return false;
    }

    /**
     * method convert long to string
     *
     * @param lng Long
     * @return String
     * @throws abc Exception
     */
    public static String convertFromLongToString(Long lng) throws Exception {
        return Long.toString(lng);
    }

    /*
     * @todo: convert from Long array to String array
     */
    public static String[] convertFromLongToString(Long[] arrLong) throws Exception {
        String[] arrResult = new String[arrLong.length];
        for (int i = 0; i < arrLong.length; i++) {
            arrResult[i] = convertFromLongToString(arrLong[i]);
        }
        return arrResult;
    }

    /*
     * @todo: convert from String array to Long array
     */
    public static long[] convertFromStringToLong(String[] arrStr) throws Exception {
        long[] arrResult = new long[arrStr.length];
        for (int i = 0; i < arrStr.length; i++) {
            arrResult[i] = Long.parseLong(arrStr[i]);
        }
        return arrResult;
    }

    /*
     * @todo: convert from String value to Long value
     */
    public static long convertFromStringToLong(String value) throws Exception {
        return Long.parseLong(value);
    }


    /*
     * Check String that containt only AlphabeUpCase and Number Return True if
     * String was valid, false if String was not valid
     */
    public static boolean checkAlphabeUpCaseNumber(String value) {
        boolean result = true;
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (alphabeUpCaseNumber.indexOf(temp) == -1) {
                result = false;
                return result;
            }
        }
        return result;
    }

    public static boolean validString(Object temp) {
        if (temp == null || temp.toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    public static boolean maskVN(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (mask.indexOf(str.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean maskEN(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (maskEN.indexOf(str.charAt(i)) < 0) {
                return false;
            }
        }
        if (str.toLowerCase().charAt(0) < 'a' || str.toLowerCase().charAt(0) > 'z') {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String str) {
        if (str == null || !str.matches("[0-9]+$")) {
            return false;
        }
        return true;
    }

    public static String formatString(String str) {
        return " '" + str.trim().toLowerCase() + "'";
    }

    public static String formatLike(String str) {
        return "%" + str.trim().toLowerCase().replaceAll("_", "\\\\_") + "%";
    }

    public static String formatOrder(String str, String direction) {
        return " NLSSORT(" + str + ",'NLS_SORT=vietnamese') " + direction;
    }


    public static String formatFunction(String function, String str) {
        return " " + function + "(" + str + ") ";
    }

    public static String formatConstant(String str) {
        String str1 = "";
        int index = 0;
        String alphabe = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 1; i < str.length(); i++) {
            if (alphabe.indexOf(str.charAt(i)) > 0) {
                str1 = str1 + str.substring(index, i).toUpperCase() + "_";
                index = i;
            }
        }
        str1 = str1 + str.substring(index, str.length()).toUpperCase() + "_";
        return str1;
    }

    public static void main(String[] args) {
        System.out.println(formatConstant("raHivePartition"));
    }

    public static boolean isLong(String str) {
        try {
            Long.valueOf(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean containSpecialCharacteristic(String str) {
        if (str == null) {
            return false;
        }
        List lstSpecialCharacteristic = new ArrayList<String>();
        lstSpecialCharacteristic.add("!");
        lstSpecialCharacteristic.add("@");
        lstSpecialCharacteristic.add("#");
        lstSpecialCharacteristic.add("%");
        lstSpecialCharacteristic.add("^");
        lstSpecialCharacteristic.add("&");
        lstSpecialCharacteristic.add("*");
        lstSpecialCharacteristic.add("(");
        lstSpecialCharacteristic.add(")");
        lstSpecialCharacteristic.add(" ");
        for (int i = 0; i < lstSpecialCharacteristic.size(); i++) {
            if (str.contains(lstSpecialCharacteristic.get(i).toString())) {
                return true;
            }
        }
        return false;
    }

    public static String upperFirstChar(String input) {
        if (isNullOrEmpty(input)) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static boolean isNullOrEmpty(String obj1) {
        return (obj1 == null || "".equals(obj1.trim()));
    }
    
    public static boolean isLongNullOrEmpty(Long obj1) {
        return (obj1 == null || "0L".equals(obj1));
    }
    
    public static boolean isIntNullOrEmpty(int obj1) {
        return ("0".equals(obj1));
    }
    
   public static boolean isDoubleNullOrEmpty(Double obj1) {
        return (obj1 == null || "0D".equals(obj1));
    }

    //

    public static boolean isStringNullOrEmpty(Object obj1) {
        return obj1 == null || obj1.toString().trim().equals("");
    }

    
      public static boolean isNullOrEmpty(List<Object[]> obj1) {
        return obj1 == null || obj1.isEmpty();
    }

    public static String getIp(){
        InetAddress IP= null;
        try {
            IP = InetAddress.getLocalHost();
            return IP.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

  
}

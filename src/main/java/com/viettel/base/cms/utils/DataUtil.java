package com.viettel.base.cms.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DataUtil.class);

    public static boolean nonEmpty(String text) {
        return !nullOrEmpty(text);
    }

    public static boolean nonEmpty(Collection collection) {
        return !nullOrEmpty(collection);
    }

    public static boolean notNullOrEmpty(Collection objects) {
        return !nullOrEmpty(objects);
    }

    public static boolean nullOrEmpty(Collection objects) {
        return objects == null || objects.isEmpty();
    }

    public static boolean nullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean notNull(Object object) {
        return !nullObject(object);
    }

    public static boolean nullObject(Object object) {
        return object == null;
    }

    public static boolean nullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public static boolean nullOrZero(String value) {
        return value == null || "0".equals(value);
    }

    public static boolean nullOrZero(Integer value) {
        return (value == null || value.equals(0));
    }

    public static Double safeToDouble(Object obj1) {
        return safeToDouble(obj1, 0.0);
    }

    private static Pattern pattern;

    private static final String LAT_LONG_PATTERN = "^[0-9]+(\\.[0-9]+)?$";
    private static final String PHONE_PATTERN = "[0-9]{9,15}+$";
    private static final String NUMBER_PATTERN = "[0-9]+$";

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        Double result = defaultValue;
        if (obj1 != null) {
            try {
                result = Double.parseDouble(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
                throw ignored;
            }
        }

        return result;
    }

    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, 0L);
    }

    public static Long safeToLong(Object obj1, Long defaultValue) {
        Long result = defaultValue;
        if (obj1 != null) {
            if (obj1 instanceof BigDecimal) {
                return ((BigDecimal) obj1).longValue();
            }
            if (obj1 instanceof BigInteger) {
                return ((BigInteger) obj1).longValue();
            }
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ignored) {
                logger.error(ignored.getMessage(), ignored);
                throw ignored;
            }
        }

        return result;
    }

    public static boolean equalsObj(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) return false;
        return obj1.equals(obj2);
    }

    public static Integer parseToInt(String value, Integer defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return defaultVal;
        }
    }

    public static Integer parseToInt(String value) {
        return parseToInt(value, null);
    }

    public static Integer parseToInt(Object value) {
        return parseToInt(parseToString(value), null);
    }

    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public static Long parseToLong(Object value, Long defaultVal) {
        try {
            String str = parseToString(value);
            if (nullOrEmpty(str)) {
                return null;
            }
            return Long.parseLong(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return defaultVal;
        }
    }

    public static Long parseToLong(Object value) {
        return parseToLong(value, null);
    }

    public static Double parseToDouble(Object value) {
        return parseToDouble(value, null);
    }

    public static Double parseToDouble(Object value, Double defaultVal) {
        try {
            String str = parseToString(value);
            if (nullOrEmpty(str)) {
                return null;
            }
            return Double.parseDouble(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return defaultVal;
        }
    }

    public static String parseToString(Object value, String defaultVal) {
        try {
            return String.valueOf(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return defaultVal;
        }
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
    public static String parseToString(Object value) {
        return parseToString(value, "");
    }

    public static boolean matchByPattern(String value, String regex) {
        if (nullOrEmpty(regex) || nullOrEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static void throwIfFailed(boolean test, String message) throws Exception {
        if (!test) throw new Exception(message);
    }

    public static <X extends Throwable> void throwIfFailed(boolean test, Supplier<? extends X> exceptionSupplier) throws X {
        if (!test) throw exceptionSupplier.get();
    }

    public static void throwIf(boolean test, String message){
        if (test) throw new IllegalArgumentException(message);
    }


    public static <X extends Throwable> void throwIf(boolean test, Supplier<? extends X> exceptionSupplier) throws X {
        if (test) throw exceptionSupplier.get();
    }

    public static boolean nullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(CharSequence cs) {
        return nullOrEmpty(cs);
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> T deepCloneObject(T source) {
        try {
            if (source == null) {
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            T dto = (T) in.readObject();
            in.close();
            return dto;
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String objectToJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if (object == null) {
            return null;
        }
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    public static <T> T jsonToObject(String json, Class<T> classOutput) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (json == null || json.isEmpty()) {
            return null;
        }
        return mapper.readValue(json, classOutput);
    }

    private static boolean safeEqualString(String str1, String str2, boolean ignoreCase, boolean trimspace) {
        if (str1 == null || str2 == null) {
            return false;
        }

        if (trimspace) {
            str1 = str1.trim();
            str2 = str2.trim();
        }

        if (ignoreCase) {
            return str1.equalsIgnoreCase(str2);
        } else {
            return str1.equals(str2);
        }
    }

    public static boolean safeEqualIgnoreCaseString(String str1, String str2) {
        return safeEqualString(str1, str2, true, true);
    }

    public static boolean safeEqualString(String str1, String str2) {
        return safeEqualString(str1, str2, false, true);
    }

    public static boolean safeEqualIgnoreCaseWithoutTrimSpaceString(String str1, String str2) {
        return safeEqualString(str1, str2, true, false);
    }

    public static boolean safeEqualWithoutTrimSpaceString(String str1, String str2) {
        return safeEqualString(str1, str2, false, false);
    }


    public static List<String> split(String separate, String object) {
        return Optional.ofNullable(object)
                .map(x -> x.split(separate))
                .map(Arrays::asList)
                .orElseGet(ArrayList::new);
    }

    public static String firstNonEmpty(String... strings) {
        for (String string : strings) {
            if (!isNullOrEmpty(string)) {
                return string;
            }
        }
        return "";
    }

    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * Tra ve doi tuong default neu object la null, neu khong thi tra object
     *
     * @param object
     * @param defaultValueSupplier
     * @param <T>
     * @return
     */
    public static <T> T defaultIfNull(final T object, final Supplier<T> defaultValueSupplier) {
        return object != null ? object : defaultValueSupplier.get();
    }

    public static boolean safeEqual(Object obj1, Object obj2) {
        return ((obj1 != null) && (obj2 != null) && obj2.toString().equals(obj1.toString()));
    }

    public static Character safeToCharacter(Object value) {
        return safeToCharacter(value, '0');
    }

    public static Character safeToCharacter(Object value, Character defaulValue) {
        if (value == null) return defaulValue;
        return String.valueOf(value).charAt(0);
    }

    public static void assertTrue(boolean test, String message) throws Exception {
        if (!test) throw new IllegalArgumentException(message);
    }


    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static LocalDateTime parseToLocalDatetime(Object value) {
        if (value == null)
            return null;
        String tmp = parseToString(value, null);
        if (tmp == null)
            return null;

        try {
            LocalDateTime rtn = convertStringToLocalDateTime(tmp, "yyyy-MM-dd HH:mm:ss.S");
            return rtn;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static LocalDateTime convertStringToLocalDateTime(String value, String fomart) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fomart);
        LocalDateTime formatDateTime = LocalDateTime.parse(value, formatter);
        return formatDateTime;
    }

    public static BigInteger parseToBigint(Object value) {
        if (value == null)
            return null;
        String tmp = parseToString(value, null);
        if (tmp == null || "null".equalsIgnoreCase(tmp))
            return null;
        return new BigInteger(tmp);
    }

    public static Date parseToDate(Object value) {
        if (value == null)
            return null;
        String tmp = parseToString(value, null);
        if (tmp == null)
            return null;
        try {
            Date rtn = convertStringToTime(tmp, "yyyy-MM-dd HH:mm:ss");
            return rtn;
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static Date convertStringToTime(String date, String pattern) throws ParseException {
        if (date == null || "".equals(date.trim())) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.parse(date);

    }

    public static String localDateTimeToString(LocalDateTime value) {
        if (!notNull(value)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return value.format(formatter); // "1986-04-08 12:30"
    }

    public static String convertLocalDateTimeToString(LocalDateTime date, String format){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date);
    }

    public static class UsernameValidator {
        public UsernameValidator() {
            pattern = Pattern.compile(LAT_LONG_PATTERN);
        }

        public boolean validateNumberDouble(final String latLong) {
//            return pattern.matcher(latLong).matches();
            return latLong.matches(LAT_LONG_PATTERN);
        }
    }

    public static class PhoneValidator {
        public PhoneValidator() {
            pattern = Pattern.compile(PHONE_PATTERN);
        }

        public boolean validateNumber(final String latLong) {
            return pattern.matcher(latLong).matches();
        }
    }

    public static class NumberValidator {
        public NumberValidator() {
            pattern = Pattern.compile(NUMBER_PATTERN);
        }

        public boolean validateNumber(final String latLong) {
            return pattern.matcher(latLong).matches();
        }
    }

    public static class DateValidatorUsingDateFormat implements DateValidator {
        private String dateFormat;

        public DateValidatorUsingDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public boolean isValid(String dateStr) {
            DateFormat sdf = new SimpleDateFormat(this.dateFormat);
            sdf.setLenient(false);
            try {
                sdf.parse(dateStr);
            } catch (ParseException e) {
                return false;
            }
            return true;
        }
    }


    public static class DateValidatorUsingLocalDate implements DateValidator {
        private DateTimeFormatter dateFormatter;

        public DateValidatorUsingLocalDate(DateTimeFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
        }

        @Override
        public boolean isValid(String dateStr) {
            try {
                LocalDate.parse(dateStr, this.dateFormatter);
            } catch (DateTimeParseException e) {
                return false;
            }
            return true;
        }
    }


    public static boolean compareDate(String date1, String date2, String pattern) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date dateFormat1 = new Date();
        Date dateFormat2 = new Date();
        try {
        if (date1 == null || "".equals(date1.trim())) {
            return false;
        } else {
            dateFormat1 = dateFormat.parse(date1);
        }

        if (date2 == null || "".equals(date2.trim())) {
            return false;
        } else {
            dateFormat2 = dateFormat.parse(date2);
        }

        }catch (ParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return (dateFormat2.before(dateFormat1));
    }


}

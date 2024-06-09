package org.tuanit.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.tuanit.Main;
import org.tuanit.view.ExecutingStatus;
import org.tuanit.view.ToastMessage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class Helper {
    private static final ToastMessage toastMessage = new ToastMessage();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    private static final Random random = new Random();

    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    //public static void setTimeout(WebDriver driver, long time) {
    //    driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
    //}

    //public static void setTimeoutUntilElement(WebDriver driver, By by) {
    //    setTimeoutUntilElement(driver, by, null, null);
    //}
    //
    //public static void setTimeoutUntilElement(WebDriver driver, By by, Consumer<Exception> orElse, Runnable ifDone) {
    //    try {
    //        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(5));
    //        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    //        if (ifDone != null) {
    //            ifDone.run();
    //        }
    //    } catch (Exception e) {
    //        if (orElse != null)
    //            orElse.accept(e);
    //    }
    //}

    public static void notify(String mess) {
        toastMessage.notify(mess);
    }

    public static void log(String mess) {
        System.out.println(mess);
    }


    public static void log(String mess, String... params) {
        mess = mess.replace("{}", "%s");
        if (params != null && params.length > 0) {
            System.out.println(String.format(mess, params));
        } else {
            System.out.println(mess);
        }
    }

    public static String buildWithId(String id, String... value) {
        StringBuilder pattern = new StringBuilder("%-20s");
        id = StringUtils.isBlank(id) ? "" : id;
        for (String string : value) {
            pattern.append("|| %-20s");
        }
        LinkedList<String> strings = new LinkedList<>();
        strings.add(id);
        if (value.length > 0) {
            for (int i = 0; i < value.length; i++) {
                value[i] = StringUtils.isBlank(value[i]) ? "" : value[i];
            }
            strings.addAll(List.of(value));
        }
        return String.format(pattern.toString(), strings.toArray());
    }

    public static <T> List<List<T>> splitList(List<T> originalList, int numberOfParts) {
        List<List<T>> parts = new ArrayList<>();

        // Tính toán độ dài của mỗi phần
        int totalSize = originalList.size();
        int partSize = totalSize / numberOfParts;

        // Tách danh sách thành số lượng phần mong muốn
        int startIndex = 0;
        for (int i = 0; i < numberOfParts; i++) {
            int endIndex = startIndex + partSize;
            if (i == numberOfParts - 1) {
                endIndex = totalSize; // Đảm bảo phần cuối cùng lấy hết phần còn lại
            }

            List<T> part = originalList.subList(startIndex, endIndex);
            parts.add(part);

            startIndex = endIndex;
        }

        return parts;
    }

    public static String[] getWithId(String value) {
        String[] values = value.split("\\|\\|");
        if (values.length < 2) {
            return new String[]{values[0].trim(), ""};
        } else {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null) {
                    values[i] = values[i].trim();
                } else {
                    values[i] = "";
                }
            }
            return values;
        }
    }

    public static String convertUnixTimestampToDate(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Ho_Chi_Minh"));
        return zonedDateTime.format(formatter);
    }

    public static String convertUnixTimestampToDateMilli(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Ho_Chi_Minh"));
        return zonedDateTime.format(formatter);
    }

    public static String path() {
        try {
            CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            return jarFile.getParentFile().getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized void writeFile(Object o) {
        checkDirect();
        try {
            String filename = path() + "/toolbanhangdata/jsondata/" + o.getClass().getSimpleName() + ".json";
            String value = OBJECT_MAPPER.writeValueAsString(o);
            FileUtils.write(new File(filename), value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized <T> T getObject(Class<T> tClass) {
        checkDirect();
        String filename = path() + "/toolbanhangdata/jsondata/" + tClass.getSimpleName() + ".json";
        File file = new File(filename);
        try {
            if (file.exists()) {
                return OBJECT_MAPPER.readValue(file, tClass);
            } else return null;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                FileUtils.delete(file);
            } catch (Exception ex) {
            }
            return null;
        }
    }

    public static synchronized <T> Optional<T> getObjectOptionally(Class<T> tClass) {
        checkDirect();
        String filename = path() + "/toolbanhangdata/jsondata/" + tClass.getSimpleName() + ".json";
        File file = new File(filename);
        try {
            if (file.exists()) {
                return Optional.ofNullable(OBJECT_MAPPER.readValue(file, tClass));
            } else return Optional.empty();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                FileUtils.delete(file);
            } catch (Exception ex) {
            }
            return Optional.empty();
        }
    }

    public static long checkSleepingTime(String time) {
        try {
            return Long.parseLong(time) * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            Helper.notify("Nhập thời gian nghỉ sai, set thời gian mặc định 30s");
            return 30 * 1000;
        }
    }

    public static long checkSleepingTime(int time) {
        try {
            return time * 1000L;
        } catch (Exception e) {
            e.printStackTrace();
            Helper.notify("Nhập thời gian nghỉ sai, set thời gian mặc định 30s");
            return 30 * 1000;
        }
    }

    public static String getSelectedProfile(JComboBox<String> stringJComboBox) {
        return (String) stringJComboBox.getSelectedItem();
    }

    public static void checkDirect() {
        File file = new File(path() + "/toolbanhangdata");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static String hashMd5(String input) {
        return DigestUtils.md5Hex(input).toUpperCase();
    }

    public static boolean isLocal() {
        String active = Optional.ofNullable(System.getenv("TUANDO")).orElse("");
        return active.equals("local");
    }

    public static void alert(String mess, String... params) {
        String message = mess.length() > 150 ? "<html><body><p style='width: 400px;'>" + mess + "</p></body></html>" : mess;
        message = message.replace("{}", "%s");
        message = String.format(message, params);
        String finalMessage = message;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, finalMessage);
        });
    }


    public static String randomString(String text) {
        if (text == null)
            text = "";

        return text.chars().map(i -> new Random().nextDouble() > 0.5 ? Character.toUpperCase(i) : Character.toLowerCase(i)) // change the case
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public static long generateRandomNumber(int a, int b) {
        return random.nextInt(b - a + 1) + a;
    }

    public static void sleep(long sleepingTimes) {
        try {
            Thread.sleep(sleepingTimes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void forEachWrapper(Collection<T> tList, BiConsumer<T, IndexInfo> tConsumer) {
        forEachWrapper(tList, null, tConsumer);
    }

    public static <T> void forEachWrapper(Collection<T> tList, ExecutingStatus executingStatus, BiConsumer<T, IndexInfo> tConsumer) {
        //SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setCount(tList.size());
        int count = 0;
        int index = 1;
        for (T t : tList) {
            try {
                if (executingStatus != null) {
                    if (executingStatus.isBreak)
                        return;
                    executingStatus.executeWhenIsPausing();
                }
                indexInfo.setIndex(index);
                tConsumer.accept(t, indexInfo);
            } catch (Exception e) {
                e.printStackTrace();
                if (executingStatus != null) {
                    executingStatus.logs.addElement("Lỗi: " + e.getMessage());
                }
                Helper.sleepExecute(30, executingStatus);
            }
            //if (count == (settingModel.getCountToSleep() != null ? settingModel.getCountToSleep() : 10)) {
            //    count = 0;
            //    for (int i = settingModel.getSleepMinutes(); i > 0; i--) {
            //        if (executingStatus != null) {
            //            executingStatus.logs.addElement("Thông báo: Sau " + (settingModel.getCountToSleep() != null ? settingModel.getCountToSleep() : 10)
            //                    + " tin nhắn. Tạm dừng trong " + settingModel.getSleepMinutes() + " phút. Tiếp tục trong: " + i + " phút nữa.");
            //        }
            //        Helper.sleep(60000);
            //    }
            //}
            count++;
            indexInfo.setIndex(index++);
        }
    }

    public static String parseGender(Integer value) {
        return value == 0 ? "anh" : "chị";
    }

    public static String getIndexList(IndexInfo indexInfo) {
        return String.format("[%s/%s]: ", indexInfo.getIndex(), indexInfo.getCount());
    }

    public static void sleepExecute(long sleepingTimes, ExecutingStatus executingStatus) {
        sleepExecute(sleepingTimes, executingStatus, true);
    }

    public static void sleepExecute(long sleepingTimes, ExecutingStatus executingStatus, boolean isRandom) {
        if (isRandom)
            sleepingTimes = Helper.generateRandomNumber((int) sleepingTimes, (int) (sleepingTimes + 5));
        if (executingStatus != null) {
            executingStatus.logs.addElement("Đang tạm dừng: " + sleepingTimes + "s");
        }
        try {
            for (long i = 1; i <= sleepingTimes; i++) {
                if (executingStatus != null) {
                    if (executingStatus.isBreak) {
                        return;
                    }
                }
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Data
    public static class IndexInfo {
        private int index = 1;
        private int count;
    }
}

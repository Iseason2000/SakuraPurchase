package top.iseason.sakurapurchase.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        return sdf.format(date);
    }
}

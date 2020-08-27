package com.christophelai.idp_colistraking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Constant {
    public static final String SERVER = "http://192.168.100.19:8000";

    public static String getToday(String patern) {
        //yyyy-MM-dd
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(patern, Locale.getDefault());
        String todayDate = df.format(c);
        return todayDate;
    }
}

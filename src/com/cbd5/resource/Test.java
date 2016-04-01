package com.cbd5.resource;

import com.cbd5.PostgresCommon;

import net.sf.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
            String t = "42437.0";
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(42437);
            System.out.println(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

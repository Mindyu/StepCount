package com.mindyu.step.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimePickerUtil {

    public static void showTimeDialog(final Context context, final TextView textView) {
        final Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final DateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);

        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String remain_time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                Date date = null;
                try {
                    date = df.parse(remain_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (null != date) {
                    calendar.setTime(date);
                }
                textView.setText(df.format(date));
            }
        }, hour, minute, true).show();
    }

    public static void showDateDialog(final Context context, final TextView textView, String dateStr) {
        final Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH+1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (dateStr!=null && !"".equals(dateStr.trim())){
            String[] str = dateStr.split("-");
            if (str.length==3){
                year = Integer.valueOf(str[0]);
                month = Integer.valueOf(str[1]);
                day = Integer.valueOf(str[2]);
            }
        }

        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            }
        }, year, month-1, day).show();
    }
}

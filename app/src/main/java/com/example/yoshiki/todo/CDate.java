package com.example.yoshiki.todo;

import android.database.Cursor;
import android.database.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yoshiki on 2015/07/20.
 */
public class CDate extends Date {
    /**
     * String型からDate型へ変換.
     *
     * @param format strSrcのフォーマット
     * @param strSrc 変換文字列
     * @return Date 変換後
     */
    public static Date StringToDate(
        SimpleDateFormat format,
        String strSrc)
    {
        Date date = null;
        try {
            date = format.parse(strSrc);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    /**
     * Date型からString型へ変換.
     *
     * @param format strSrcのフォーマット
     * @param date 変換日付
     * @return String 変換後
     */
    public static String DateToString(
            SimpleDateFormat format,
            Date date)
    {
        return format.format(date);
    }

    /**
     * 2つの日付の日数を取得.
     *
     * @param from この日付から
     * @param to   この日付までの日数
     * @return long 日数
     */
    public static long DiiffDate(
            Date from,
            Date to)
    {
        long nFrom = from.getTime();
        long nTo   = to.getTime();
        return (nTo - nFrom) / (1000 * 60 * 60 * 24);
    }

    /**
     * 2つの日付の日数を取得.
     * @param format strFrom,strToのフォーマット
     * @param strFrom この日付から
     * @param strTo   この日付までの日数
     * @return long 日数
     */
    public static long DiiffDate(
            SimpleDateFormat format,
            String strFrom,
            String strTo)
    {
        Date from = StringToDate(format, strFrom);
        Date to   = StringToDate(format, strTo);
        return DiiffDate(from, to);
    }

    /**
     * 今日の日付を取得.
     * @return String 今日の日付（yyyy/MM/dd/HH:mm:ss）
     */
    public static String GetTodaysDate()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}

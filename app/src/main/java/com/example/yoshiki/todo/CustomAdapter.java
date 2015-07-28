package com.example.yoshiki.todo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yoshiki on 2015/07/19.
 */
/*
public class CustomAdapter extends SimpleCursorAdapter {
    private TextView mDate;
    private TextView mTitle;


    public CustomAdapter(Context context, int layout, Cursor c,
                         String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.todo_row, null, true);

        mDate = (TextView) v.findViewById(R.id.date);
        mTitle = (TextView) v.findViewById(R.id.title);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // SQLiteのテーブルの"sample_row"という列のデータを取得してセット

        // cursorからDateを取得し、String⇒long型へ変換
        String strDate = cursor.getString(cursor.getColumnIndex(TodoDbAdapter.STR_KEY_DATE));
        Date dateTo = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateTo = format.parse(strDate);
        } catch (ParseException e) {}
        long nDateTo = dateTo.getTime();

        // 今日の日付を取得し、String⇒long型へ返還
        Date dateFrom = new Date(System.currentTimeMillis());
        long nDateToday = dateFrom.getTime();

        // 差分の日付を算出
        long nDateDiff = nDateTo - nDateToday;

        // 日付の差が7以下の場合は背景を黄色
        if( nDateDiff <= 7){
            mDate.setBackgroundColor(Color.parseColor("#FFD700"));
        }
        // 日付の差がマイナスの場合は背景を赤
        else if (nDateDiff < 0) {
            mDate.setBackgroundColor(Color.parseColor("#FF4500"));
        }
        // 上記以外は青
        else {
            mDate.setBackgroundColor(Color.parseColor("#1E90FF"));
        }
        mDate.setText(strDate);
    }
}
*/
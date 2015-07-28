package com.example.yoshiki.todo;

/**
 * Created by Yoshiki on 2015/07/13.
 */
public class CState {
    // 状態
    public static final int N_ACTIVITY_CREATE  = 0;    // Todoアイテム作成
    public static final int N_ACTIVITY_EDIT    = 1;    // Todoアイテム編集
    public static final int N_ACTIVITY_DATE    = 2;    // Todo実施期限設定

    // Todo実施期限 色分けに使うキー
    public static final int N_DATE_YELLOW = 7;
    public static final int N_DATE_RED    = 2;
}

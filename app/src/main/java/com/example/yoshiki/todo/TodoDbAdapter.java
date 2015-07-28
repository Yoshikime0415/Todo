package com.example.yoshiki.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Matrix;
import android.util.Log;

/**
 * DBアクセスクラス
 * @author 清兼
 */
public class    TodoDbAdapter {
    /**
     * クラス名定義
     */
    private static final String STR_CLASS_NAME = "TodoDbAdapter";

    /**
     * DB情報定義
     */
    private static final String STR_DATABASE_NAME   = "todo";         // DB名
    private static final String STR_DATABASE_TABLE  = "todoItem";     // テーブル名
    private static final int    N_DATABASE_VERSION  = 1;              // バージョン

    /**
     * Column定義
     */
    public static final String STR_KEY_PRIMARY      = "_id";            // Primary Key
    public static final String STR_KEY_STATUS       = "status";         // 状態
    public static final String STR_KEY_REGISTRATION = "registration";   // 登録日
    public static final String STR_KEY_TITLE        = "title";          // タイトル
    public static final String STR_KEY_BODY         = "body";           // 内容
    public static final String STR_KEY_LIMIT        = "time_limit";     // 期限
    public static final String STR_KEY_COMPLETE     = "complete";       // 完了日 清）sql now()
    public static final String STR_KEY_DELTE        = "deletion_date";  // 削除日

    /**
     * Todoアイテムの状態定義
     */
    public static final String STR_STATUS_OPEN     = "open";      // 未完了
    public static final String STR_STATUS_COMPLETE = "complete";  // 完了済み
    public static final String STR_STATUS_DELETE   = "delete";    // 削除済み

    /**
     * TABLE Create用構文定義
     *  Column：
     *      integer         _id
     *      text(not null)  state
     *      text(not null)  registration
     *      text(not null)  title
     *      text(not null)  body
     *      text(not null)  limit
     *      text            complete
     *      text            delete
     */
    private static final String DATABASE_CREATE =
            "create table TodoItem (_id integer primary key autoincrement, " +
                    "status text not null,"       +
                    "registration text not null," +
                    "title text not null,"        +
                    "body text not null,"         +
                    "time_limit text not null,"   +
                    "complete text,"              +
                    "deletion_date text);";

    /**
     * TABLE Drop用構文定義
     *  テーブル名：todoItem
     */
    private static final String DATABASE_DROP = "DROP TABLE IF EXISTS todoItem";

    /**
     * メンバ変数定義
     */
    private DatabaseHelper mDbHelper;       // DatabaseHelperインスタンス
    private SQLiteDatabase mDb;             // DBハンドリング用のインスタンス
    private final Context mCtx;             // Contextインスタンス

    /**
     * コンストラクタ
     *
     * @param ctx   コンテキスト
     */
    public TodoDbAdapter(Context ctx)
    {
        // コンテキストを保存
        this.mCtx = ctx;
    }

    /**
     * DBを生成し、インスタンスを保存.
     *
     * @return TodoDbAdapterインスタンス
     * @throws SQLException
     */
    public TodoDbAdapter open() throws SQLException
    {
        // DatabaseHelperのインスタンス生成
        mDbHelper = new DatabaseHelper(mCtx);

        // データベースハンドリング用のインスタンスを取得
        mDb = mDbHelper.getWritableDatabase();

        // TodoDbAdapterのインスタンスをリターン
        return this;
    }

    /**
     * DBのclose.
     *
     */
    public void close()
    {
        mDbHelper.close();
    }


    /**
     * TodoアイテムをDBへ新規追加する.
     *
     * @param strStatus 状態
     * @param strTitle  タイトル
     * @param strBody   内容
     * @param strLimit  期限
     * @return _id or -1 if failed
     */
    public long createTodoItem(
            String strStatus,
            String strTitle,
            String strBody,
            String strLimit
            )
    {
        // 今日の日付を登録日とする
        String strRegistration = CDate.GetTodaysDate();

        // インスタンス生成し、DBに保存するColumnを設定
        ContentValues src = new ContentValues();
        src.put(STR_KEY_STATUS      , strStatus);
        src.put(STR_KEY_REGISTRATION, strRegistration);
        src.put(STR_KEY_TITLE       , strTitle);
        src.put(STR_KEY_BODY        , strBody);
        src.put(STR_KEY_LIMIT       , strLimit);
        src.put(STR_KEY_COMPLETE    , "");  // 新規追加時は完了日は無し
        src.put(STR_KEY_DELTE       , "");  // 新規追加時は削除日は無し

        // DBへ保存
        return mDb.insert(
                STR_DATABASE_TABLE,     // テーブル名
                null,                   // null値の格納が許可されていないカラムに代わりに利用される値
                src);                   // DBへ保存する情報
    }

    /**
     * DBからTodoアイテム情報を全て取得.
     * コール元でmoveToFirstし、取得有無を判定すること.
     *
     * @return 取得した全TodoアイテムのDBカーソル.
     */
    public Cursor fetchAllTodoItems()
    {
        // 取得するColumnを指定
        String[] strTarget = { STR_KEY_PRIMARY, STR_KEY_STATUS, STR_KEY_REGISTRATION, STR_KEY_TITLE,
                               STR_KEY_BODY, STR_KEY_LIMIT, STR_KEY_COMPLETE};
        // 全Todoアイテム取得
        return mDb.query(
                STR_DATABASE_TABLE,     // テーブル名
                strTarget,              // 取得対象のColumn
                null, null,             // 取得するレコードの条件
                null, null, null);      // groupby, Having, orderby, limit句
    }

    /**
     * DBからTodoアイテム情報を全て取得（Status指定）.
     * コール元でmoveToFirstし、取得有無を判定すること.
     *
     * @param strStatus 取得対象のState
     * @return Cursor   取得したTodoアイテムのDBカーソル.
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchAllTodoItemsByStatus(
            String strStatus) throws SQLException
    {
        // 取得するColumnを指定
        String[] strTarget = { STR_KEY_PRIMARY, STR_KEY_STATUS, STR_KEY_REGISTRATION, STR_KEY_TITLE,
                STR_KEY_BODY, STR_KEY_LIMIT, STR_KEY_COMPLETE};

        // Stateが合致するTodoアイテムを取得
        return  mDb.query(
                    STR_DATABASE_TABLE,         // テーブル名
                    strTarget,                  // 取得対象のColumn
                    "status like ?",            // 取得するレコードの条件
                    new String[]{strStatus},    // 取得するレコードの条件
                    null, null, null, null);    // groupby, Having, orderby, limit句
    }

    /**
     * DBからTodoアイテム情報を全て取得（PrimaryKey指定）.
     * コール元でmoveToFirstし、取得有無を判定すること.
     *
     * @param  nPrimaryKey 取得対象のPrimaryKey
     * @return Cursor   取得したTodoアイテムのDBカーソル.
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchToDoItemByPrimaryKey(
            long nPrimaryKey) throws SQLException
    {
        // 取得するColumnを指定
        String[] strTarget = { STR_KEY_PRIMARY, STR_KEY_STATUS, STR_KEY_REGISTRATION, STR_KEY_TITLE,
                STR_KEY_BODY, STR_KEY_LIMIT, STR_KEY_COMPLETE};

        // PrimaryKeyで指定されたTodoアイテムを取得
        return mDb.query(
                STR_DATABASE_TABLE,                  // テーブル名
                strTarget,                           // 取得対象のColumn
                STR_KEY_PRIMARY + "=" + nPrimaryKey, // 取得するレコードの条件
                null, null, null, null, null);       // groupby, Having, orderby, limit句
    }

    /**
     * PrimaryKeyListで指定されたTodoアイテムをDBから削除.
     *
     * @param  nPrimaryKeyList 削除対象のPrimaryKeyのリスト
     */
    public void deleteTodoItem(long nPrimaryKeyList[])
    {
        // 配列のサイズを取得
        long nArraySize = nPrimaryKeyList.length;

        // DBから削除
        for( int nIndex = 0; nIndex < nArraySize; nIndex++ )
        {
            // Todoアイテムの削除
            mDb.delete(
                STR_DATABASE_TABLE,                               // テーブル名
                STR_KEY_PRIMARY + "=" + nPrimaryKeyList[nIndex],  // 削除の対象となるレコード（WHERE句）
                null);                                            // 不明
        }
    }

    /**
     * Todoアイテムを編集した場合に、DBの内容をアップデートする.
     *
     * @param nPrimaryKey アップデート対象のPrimaryKey
     * @param strTitle    タイトル
     * @param strBody     内容
     * @param strLimit    期限
     */
    public long updateTodoItem(
            long   nPrimaryKey,
            String strTitle,
            String strBody,
            String strLimit)
    {
        // インスタンス生成し、DBに保存するColumnを設定
        ContentValues src = new ContentValues();
        src.put(STR_KEY_TITLE       , strTitle);
        src.put(STR_KEY_BODY        , strBody);
        src.put(STR_KEY_LIMIT       , strLimit);

        // DBの内容をアップデート
        return mDb.update(
                   STR_DATABASE_TABLE,                         // テーブル名
                   src,                                        // アップデートする内容
                   STR_KEY_PRIMARY + "=" + nPrimaryKey, null); // アップデート対象のPrimaryKey
    }

    /**
     * TodoアイテムStatusをアップデートする.
     *
     * @param nPrimaryKey アップデート対象のPrimaryKey
     * @param strStatus Todo状態
     * @return true if the note was successfully updated, false otherwise
     */
    public void updateTodoItemStatus(
            long   nPrimaryKey[],
            String strStatus[])
    {
        // インスタンス生成
        ContentValues src = new ContentValues();

        // 配列のサイズを取得
        long nArraySize = nPrimaryKey.length;

        // DBを更新
        for( int nIndex = 0; nIndex < nArraySize; nIndex++ )
        {
            // インスタンス生成
            src.put(STR_KEY_STATUS, strStatus[nIndex]);

            // ステータスをキーに、登録日、削除日、完了日のいずれかを更新（？？）
            // パターンを洗い出さないと作れない

            // DBの内容をアップデート
            mDb.update(
                    STR_DATABASE_TABLE,                             // テーブル名
                    src,                                            // アップデートする内容
                    STR_KEY_PRIMARY + "=" + nPrimaryKey[nIndex], null); // アップデート対象のPrimaryKey
        }
    }

    /**
     * Statusが遷移したか確認.
     *
     * @param nPrimaryKey 確認対象のPrimaryKey
     * @param strStatus   状態
     * @return true if the note was successfully updated, false otherwise
     */
    private boolean IsStateTransition(
            long   nPrimaryKey,
            String strStatus)
    {
        // 戻り値初期化
        boolean bRet = false;

        // DBから現Statusを取得
        String[] strTarget = { STR_KEY_STATUS };
        Cursor cursor = mDb.query(
                STR_DATABASE_TABLE,                  // テーブル名
                strTarget,                           // 取得対象のColumn
                STR_KEY_PRIMARY + "=" + nPrimaryKey, // 取得するレコードの条件
                null, null, null, null, null);       // groupby, Having, orderby, limit句

        // カーソルが取得できた場合
        if ( cursor.moveToFirst() )
        {
            String strDbStatus = cursor.getString(cursor.getColumnIndexOrThrow(TodoDbAdapter.STR_KEY_STATUS));

            // 遷移した場合はtrueを返却
            if (strStatus != strDbStatus) { bRet = true; }
        }

        return bRet;
    }

    /**
     * DB Createクラス
     *
     * @author 清兼
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * コンストラクタ
         *
         * @param context   コンテキスト
         */
        DatabaseHelper(Context context)
        {
            super(  context,                // コンテキスト
                    STR_DATABASE_NAME,      // DB名
                    null,                   // ?
                    N_DATABASE_VERSION);    // バージョン
        }

        /**
         * DB Create.
         * DBが存在しない状態でDBをOpenしようとする場合に起動.
         *
         * @param db    新規作成したDBインスタンス
         */
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        /**
         * コンストラクタで渡されたDBのバージョンと、実際に存在するDBのバージョンが異なる場合、
         * テーブルの再構成を行う.
         *
         * @param db            DBインスタンス
         * @param nOldVersion    旧バージョン番号
         * @param nNewVersion    新バージョン番号
         */
        @Override
        public void onUpgrade(
                SQLiteDatabase db,
                int nOldVersion,
                int nNewVersion)
        {
            // ログ出力
            Log.w(STR_CLASS_NAME,                      // クラス名
                    "旧DBバージョン " + nOldVersion +    // 出力文字列
                            " 新DBバージョン " + nNewVersion + ", 旧データを全削除");

            // テーブルの削除
            db.execSQL(DATABASE_DROP);

            // DB Create
            onCreate(db);
        }
    }
}

package com.example.yoshiki.todo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.MultiTapKeyListener;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.text.format.Time;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TodoItem編集用のクラス.
 * @author 清兼
 */
public class TodoEdit extends Activity {

    /**
     * TodoEditActivityコールタイミングの状態定義
     */
    public static final int N_ACTIVITY_CREATE  = 0;    // Todoアイテム作成
    public static final int N_ACTIVITY_EDIT    = 1;    // Todoアイテム編集

    /**
     * メンバ定義
     */
    private Long           mPrimaryKey; // TodoItemを特定するためのKey
    private EditText       mTitleText;  // Todoアイテムのタイトル
    private Button         mLimit;      // Todo実施期限
    private EditText       mBodyText;   // Todoアイテムの内容
    private TodoDbAdapter  mDbHelper;   // DBアクセスクラスのインスタンス
    private String         mStatus;     // ToDoアイテムの状態
    private int a;                      // 醜いフラグ

    /**
     * TodoItem編集画面を表示.
     *
     * @param savedInstanceState 保存されていたアプリケーション情報
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // 醜いフラグを初期化
        a = 1;

        // 適当
        mStatus = TodoDbAdapter.STR_STATUS_OPEN;

        // DBアクセスクラスのインスタンスの生成
        mDbHelper = new TodoDbAdapter(this);

        // DBを開く
        mDbHelper.open();

        // Viewの表示
        setContentView(R.layout.todo_edit);

        // ViewのIDを取得
        mTitleText           = (EditText)findViewById(R.id.title);
        mBodyText            = (EditText)findViewById(R.id.body);
        mLimit               = (Button)findViewById(R.id.limit);
        final Button LimitButton = (Button)findViewById(R.id.limit);

        //===================================================
        // アプリケーションがonPause、onStopから復帰した場合に備え、
        // BundleからPrimaryKeyを取得
        //===================================================
        mPrimaryKey = null;
        if (savedInstanceState != null) {
            mPrimaryKey = savedInstanceState.getLong(TodoDbAdapter.STR_KEY_PRIMARY);
        }

        //====================================================
        // 既存TodoItemの更新に備え、インテントからPrimaryKeyを取得
        //====================================================
        if (mPrimaryKey == null) {
            // インテントを取得
            Bundle extras = getIntent().getExtras();

            // インテントからPrimaryKeyを取得
            if(extras != null) {
                mPrimaryKey = extras.getLong(TodoDbAdapter.STR_KEY_PRIMARY);
            }
        }

        //====================================================
        // 上記でPrimaryKeyを取得できなかった場合は、Todoアイテムの新規作成
        //====================================================

        //====================================================
        // リスナー定義
        //====================================================
        // dateButton
        LimitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TodoDateへインテント
                Intent i = new Intent(TodoEdit.this, TodoDate.class);

                // ボタンに表示している日付を設定
                i.putExtra(TodoDate.STR_KEY_DATE, LimitButton.getText());

                // Activity起動
                startActivityForResult(
                        i,                                          // インテント
                        TodoDate.N_ACTIVITY_DATE);  // Todo実施期限設定
            }
        });

        // Body
        // いらないかも
        mBodyText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            /**
             * アプリ起動後に初めて複数選択モードへ遷移際に起動するメソッド.初期化処理を実行する.
             * trueをリターンしないと、その後何もしない.
             * @param mode 複数選択モードのインスタンス
             * @param menu メニューのインスタンス
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    /**
     * メニューボタンを押したときに表示されるアイテムを生成する
     *
     * @param menu メニューインスタンス
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * DBから取得した情報をViewに表示.
     *
     */
    private void populateFields() {
        // PrimaryKeyがある場合
        if (mPrimaryKey != null) {
            // DBからTodoアイテムを取得
            Cursor TodoItem = mDbHelper.fetchToDoItemByPrimaryKey(mPrimaryKey);
            TodoItem.moveToFirst();

            // カーソル制御をシステムへ移譲
            startManagingCursor(TodoItem);

            // Todoアイテムのインデックスを取得
            int nTitle = TodoItem.getColumnIndexOrThrow(TodoDbAdapter.STR_KEY_TITLE);
            int nLimit = TodoItem.getColumnIndexOrThrow(TodoDbAdapter.STR_KEY_LIMIT);
            int nBody  = TodoItem.getColumnIndexOrThrow(TodoDbAdapter.STR_KEY_BODY);

            // Viewへ設定
            mTitleText.setText(TodoItem.getString(nTitle));
            mLimit.setText(TodoItem.getString(nLimit));
            mBodyText.setText(TodoItem.getString(nBody));

        } else {
            // Todo実施期限に本日の日付を設定
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date(System.currentTimeMillis());
            mLimit.setText(df.format(date));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 醜いフラグが1の場合は保存
        if (a==1) {
            // onResumeに備えて
            saveState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    /**
     * TodoItemの内容をDBへ保存.
     *
     */
    private void saveState() {
        // DBへ保存対象のデータを取得
        String strTitle = mTitleText.getText().toString();
        String strBody  = mBodyText.getText().toString();
        String strLimit = mLimit.getText().toString();

        // PrimaryKeyがない場合は
        if (mPrimaryKey == null) {
            long id = mDbHelper.createTodoItem(
                    TodoDbAdapter.STR_STATUS_OPEN,
                    strTitle,
                    strBody,
                    strLimit);
            if (id > 0) {
                mPrimaryKey = id;
            }
        } else {
            mDbHelper.updateTodoItem(
                    mPrimaryKey,
                    strTitle,
                    strBody,
                    strLimit);
        }
    }

    /**
     * メニューのアイテム選択時の動作
     *
     * @param nFeatureId ?
     * @param item 選択されたメニューアイテム
     */
    @Override
    public boolean onMenuItemSelected(
            int nFeatureId,
            MenuItem item) {

        super.onMenuItemSelected(nFeatureId, item);

        // メニューボタンのIDで分岐
        switch(item.getItemId())
        {
            // 保存
            case R.id.menu_save:
                saveFunc();
                break;

            // 完了済みにする
            case R.id.menu_close:
                closeFunc();
                break;

            // 削除
            case R.id.menu_delete:
                deleteFunc();
                break;

            default:
                // Nothing to do
                break;
        }

        return true;
    }

    /**
     * 保存メニュー選択時の動作
     */
    private void saveFunc()
    {
        // Activityの戻りを設定
        setResult(RESULT_OK);

        // Activityの終了
        finish();
    }

    /**
     * 完了済みにするメニュー選択時の動作
     */
    private void closeFunc()
    {
        // Todoアイテム状態の更新
        mStatus = TodoDbAdapter.STR_STATUS_COMPLETE;

        // Activityの終了
        finish();
    }

    /**
     * 削除メニュー選択時の動作
     */
    private void deleteFunc()
    {
        // PrimaryKeyを保持している場合は削除
        if (mPrimaryKey != null) {
            long[] nPrimaryKey = {mPrimaryKey};
            mDbHelper.deleteTodoItem(nPrimaryKey);
        }
        else {
            // 醜いフラグを更新
            a = 0;
        }
        // Activityの終了
        finish();
    }

    @Override
    protected void onActivityResult(
            int nRequestCode,
            int nResultCode,
            Intent data)
    {
        super.onActivityResult(nRequestCode, nResultCode, data);

        switch (nRequestCode)
        {
            case TodoDate.N_ACTIVITY_DATE:

                if (data != null) {
                    mLimit.setText(
                            data.getStringExtra(TodoDate.STR_KEY_YEAR)  + "/" +
                            data.getStringExtra(TodoDate.STR_KEY_MONTH) + "/" +
                            data.getStringExtra(TodoDate.STR_KEY_DAY)
                    );
                    saveState();
                }
                break;

            default:
                // Nothing to do
                break;
        }
    }
}


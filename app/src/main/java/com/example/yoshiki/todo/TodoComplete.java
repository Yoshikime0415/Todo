package com.example.yoshiki.todo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 完了済みアイテムを表示するクラス.本クラスから各クラスへ処理を流していく.
 * @author 清兼
 */
public class TodoComplete extends ListActivity {
    /**
     * TodoEditActivityコールタイミングの状態定義
     */
    public static final int N_ACTIVITY_SHOW_COMPLETE_ITEM  = 0;    // Todoアイテム作成

    /**
     * メンバ変数定義
     */
    private TodoDbAdapter mDbHelper;
    private Cursor mTodoCursor;

    /**
     * 完了済みアイテム一覧を表示.
     *
     * @param savedInstanceState 保存されていたアプリケーション情報
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Viewの表示
        setContentView(R.layout.todo_complete_list);

        // DBアクセスクラスのインスタンスの生成
        mDbHelper = new TodoDbAdapter(this);

        // DBを開く。DBが存在しない場合はDBを生成する。
        mDbHelper.open();

        // Todoアイテムリストを表示
        fillData();

        // 複数選択モード用のリスナー登録
        ListView listView = getListView();
        listView.setMultiChoiceModeListener(new Callback());
    }

    /**
     * DBから完了済みのTodoアイテムを取得し、画面に配置する.
     */
    private void fillData()
    {
        // DBよりデータ取得(State=Complete)
        mTodoCursor = mDbHelper.fetchAllTodoItemsByStatus(TodoDbAdapter.STR_STATUS_COMPLETE);
        mTodoCursor.moveToFirst();

        // カーソル上のデータ有無はSimpleCursorAdapterが判定してくれるため、チェック不要

        // カーソル制御をシステムへ移譲
        startManagingCursor(mTodoCursor);

        // アプリケーションメイン画面に表示させたいColumn名を指定
        String[] strFrom = new String[]{TodoDbAdapter.STR_KEY_TITLE};

        // 表示させるViewを指定
        int[] nTo = new int[]{R.id.title};

        // SimpleCursorAdapterインスタンス生成
        SimpleCursorAdapter TodoItems = new SimpleCursorAdapter(
                this,                  // Context
                R.layout.todo_row,     // 表示先のViewGroup
                mTodoCursor,           // DBのカーソル
                strFrom,               // 表示させたいColumn名
                nTo,                   // 表示先のView
                0);

        // アプリケーションメイン画面へ表示
        setListAdapter(TodoItems);
    }

    /**
     * Todoアイテムを長押しした際に起動する複数選択モードのコールバック.
     * @author 清兼
     */
    private class Callback implements ListView.MultiChoiceModeListener
    {

        /**
         * アプリ起動後に初めて複数選択モードへ遷移際に起動するメソッド.初期化処理を実行する.
         * trueをリターンしないと、その後何もしない.
         * @param mode 複数選択モードのインスタンス
         * @param menu メニューのインスタンス
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            // メニューを追加
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.todo_list_multi_choice_mode_menu, menu);

            // 選択数を初期化
            getListView().clearChoices();
            return true;
        }

        /**
         * 複数選択モード時にメニューアイテムが選択されると起動するメソッド.
         * @param mode 複数選択モードのインスタンス
         * @param item 選択されたメニューアイテム
         * */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.menu_multi_delete:
                    //long[] nDeleteId = getListView().getCheckedItemIds();
                    //mDbHelper.deleteTodoItem(nDeleteId);
                    ///fillData();
                    // ダイアログを表示する
                    break;

                case R.id.menu_multi_complete:
//                    long[] nDeleteId = getListView().getCheckedItemIds();
                    //                  mDbHelper.updateTodoItem()
                    break;

                default:
                    break;
            }
            return true;
        }

        /**
         * 複数アイテム選択後、決定ボタン（画面左上）をクリックした際に呼び出されるメソッド.
         * @param mode 複数選択モードのインスタンス
         */
        @Override
        public void onDestroyActionMode(ActionMode mode)
        {

        }

        /**
         * 複数選択モードへ遷移時に起動するメソッド.
         * @param mode 複数選択モードのインスタンス
         * @param menu メニューのインスタンス

         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        /**
         * 選択状態が変更された際に起動するメソッド.
         * @param mode 複数選択モードのインスタンス
         * @param position
         * @param checked チェック状態
         */
        @Override
        public void onItemCheckedStateChanged(
                ActionMode mode,
                int position,
                long id,
                boolean checked)
        {
            // 選択数を取得して表示
            mode.setTitle(getListView().getCheckedItemCount() + "件選択済み");
        }
    }

    /**
     * メニューボタンを押したときに表示されるアイテムを生成する
     *
     * @param menu メニューインスタンス
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
            // Todoアイテムを作成
            case R.id.menu_insert:
                createTodoItem();
                break;

            // 完了済みのTodoアイテムを表示
            case R.id.menu_show_complete:
                break;

            default:
                // Nothing to do
                break;
        }

        return true;
    }

    /**
     * Todoアイテム編集用のActivityを起動(新規作成).
     */
    private void createTodoItem()
    {
        // インテント作成
        Intent i = new Intent(this, TodoEdit.class);

        // Activity起動
        startActivityForResult(
                i,                           // インテント
                TodoEdit.N_ACTIVITY_CREATE);   // Todoアイテム作成
    }

    /**
     * Todoアイテム編集用のActivityを起動(既存Todoアイテムの更新).
     *
     * @param l 呼び出し元のListViewオブジェクト
     * @param v ユーザが選択したTodoItem
     * @param nPosition ユーザが選択したTodoItemのポジション
     * @param nId ユーザがクリックしたTodoItemのID
     */
    @Override
    protected void onListItemClick(
            ListView l,
            View v,
            int nPosition,
            long nId)
    {
        super.onListItemClick(l, v, nPosition, nId);

        // インテント作成
        Intent i = new Intent(this, TodoEdit.class);

        // 編集を行うアイテムのidを設定
        i.putExtra(TodoDbAdapter.STR_KEY_PRIMARY, nId);

        // Activity起動
        startActivityForResult(
                i,                         // インテント
                TodoEdit.N_ACTIVITY_EDIT);   // TodoItem編集
    }

    /**
     * Activityが結果を戻してきた時にコールされるメソッド
     *
     * @param nRequestCode Activity起動時の状態定義
     * @param nResultCode Activityの結果
     * @oaram intent インテント
     */
    @Override
    protected void onActivityResult(
            int nRequestCode,
            int nResultCode,
            Intent intent) {
        super.onActivityResult(nRequestCode, nResultCode, intent);

        // Activity起動時の状態で分岐
        switch (nRequestCode)
        {
            // Todoアイテム作成
            // Todoアイテム編集
            case TodoEdit.N_ACTIVITY_CREATE:  // Throw
            case TodoEdit.N_ACTIVITY_EDIT:
                // 最新のTodoアイテムリストを表示
                fillData();
                break;

            default:
                // Nothing to do
                break;
        }
    }
}

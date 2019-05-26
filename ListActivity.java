package com.websarva.wings.android.simplememo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    // MemoOpenHelperクラスを定義
    MemoOpenHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

         // データベースから値を取得する
        if (helper == null) {
            helper = new MemoOpenHelper(ListActivity.this);
        }

        // メモリストデータを格納する変数
        ArrayList<HashMap<String, String>> memoList = new ArrayList<>();
        // データベースを取得する
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            // rawQuery という SELECT 専用メソッドを使用してデータを取得する
            Cursor c = db.rawQuery("SELECT uuid, title, body FROM MEMO_TABLE ORDER BY  id", null);
            // Cursor の先頭行があるかどうかを確認
            boolean next = c.moveToFirst();

            // 取得したすべての行を取得
            while (next) {
                HashMap<String, String> data = new HashMap<>();
                // 取得したカラムの順番（0から始まる）を型を指定したデータを取得
                String uuid = c.getString(0);
                String title = c.getString(1);
                String body = c.getString(2);
                if (body.length() > 10) {
                    // リストに表示するのは 10 字まで
                    body = body.substring(0, 11) + "...";
                }
                data.put("title", title);
                data.put("body", body);
                data.put("id", uuid);
                memoList.add(data);
                // 次の行が存在するか確認
                next = c.moveToNext();
            }
        } finally {
            // finally は、try の中で例外が発生したときでも必ず実行される
            // db を開いたら確実に close
            db.close();
        }

        // 仮のデータを作成
        //ArrayList<HashMap<String, String>> tmpList = new ArrayList<>();
        //for (int i = 1; i <= 5; i++) {
            //HashMap<String, String> data = new HashMap<>();
            // 引数には、（名前, 実際の値）という組み合わせで指定する
            // 名前はSimpleAdapterの引数で使用する
            //data.put("body", "サンプルデータ" + i);
            //data.put("id", "sampleId" + i);
            //tmpList.add(data)};

            /**
             * Adapter生成
             * tmpListを正式なデータと入れ替える
             */
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    memoList, // 使用するデータ (仮のデータの時 tmpList)
                    R.layout.row, // 使用するレイアウト
                    new String[] {"title", "body"}, //どの項目を
                    new int[]{R.id.titleText, R.id.bodyText} // どのidの項目に入れるか
                    );

            ListView listView = findViewById(R.id.memoList);
            listView.setAdapter(simpleAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /**
                 * @param parent ListView
                 * @param view 選択した項目
                 * @param position 選択した項目の添え字
                 * @param id 選択した項目のID
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //　インテント作成　第二引数には遷移先クラスを指定
                    Intent intent = new Intent(ListActivity.this, CreateMemoActivity.class);

                    //　選択されたビューを取得
                    TextView tvTitleText = view.findViewById(R.id.titleText);
                    String titleStr = tvTitleText.getText().toString();
                    TextView tvBodyText = view.findViewById(R.id.bodyText);
                    String bodyStr = tvBodyText.getText().toString();
                    //　uuid 取得
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try {
                        // rawQueryというSELECT専用メソッドを使用してデータを取得する
                        Cursor c = db.rawQuery("SELECT uuid FROM MEMO_TABLE WHERE title = '" + titleStr + "'", null);
                        // Cursor の先頭行があるかどうかを確認
                        boolean next = c.moveToFirst();
                        // 取得したすべての行を取得
                        while (next) {
                            String uuidStr = c.getString(0);
                            intent.putExtra("uuid", uuidStr);
                            next = c.moveToNext();
                        }
                    } finally {
                        // finally は、try の中で例外が発生したときでも必ず実行
                        // db を開いたら確実に close
                        db.close();
                    }
                    //　値を引き渡す（識別名, 値）の順番で指定する


                    //　Activity起動
                    startActivity(intent);
                }
            });

            /**
             *  新規作成するボタン処理
             */
            // idがnewButtonのボタンを取得
            Button newButton = findViewById(R.id.newButton);
            // clickイベント追加
            newButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // CreateMemoActivityへ遷移
                    Intent intent = new Intent(ListActivity.this, CreateMemoActivity.class);
                    intent.putExtra("uuid", "");
                    intent.putExtra("title", "");
                    intent.putExtra("body", "");
                    startActivity(intent);
                }
            });
        }
    }

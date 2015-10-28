package com.beswell.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beswell.car.R;

public class ShowRedRecords extends ActionBarActivity {

    Intent intent;
    Spinner sp;
    int pos;

    Button next;
    Button prev;

    TextView red_id;
    TextView red_plate;
    TextView red_userName;
    TextView red_CarMan;
    TextView red_Motel;
    TextView red_RequestDate;
    TextView red_CompletionDate;
    TextView red_SettlementDate;
    TextView red_ShouldMoney;

    ArrayAdapter<String> adapter;

    Bundle records;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_red_records);

        intent = getIntent();
        records = intent.getBundleExtra("rd_Records");
        Log.d("RED records", records.toString());
        String res = intent.getStringExtra("Out");

        sp = (Spinner)findViewById(R.id.red_item);

        red_id = (TextView)findViewById(R.id.red_id);
        red_plate = (TextView)findViewById(R.id.red_plate);
        red_userName = (TextView)findViewById(R.id.red_uesrName);
        red_CarMan = (TextView)findViewById(R.id.red_CarMan);
        red_Motel = (TextView)findViewById(R.id.red_Motel);
        red_RequestDate = (TextView)findViewById(R.id.red_RequestDate);
        red_CompletionDate = (TextView)findViewById(R.id.red_CompletionDate);
        red_SettlementDate = (TextView)findViewById(R.id.red_SettlementDate);
        red_ShouldMoney = (TextView)findViewById(R.id.red_ShouldMoney);

        next = (Button)findViewById(R.id.red_nextPage);
        prev = (Button)findViewById(R.id.red_prevPage);

        Log.d("Red Debug", res);

/*        tv = (TextView)findViewById(R.id.expr);
        tv.setText(res);*/
        if(records.size() != 0) {
            setContent(records, 0);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            for (int i = 0; i < records.size(); i++) {
                int temp = i + 1;
                adapter.add("第" + temp + "页");
            }

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    setContent(records, position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == 0) {
                        Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos--;
                        setContent(records, pos);
                        sp.setSelection(pos);
                    }
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == records.size() - 1) {
                        Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos++;
                        setContent(records, pos);
                        sp.setSelection(pos);
                    }
                }
            });
        }
        else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                                                    .setMessage("记录为空，没有相关记录")
                                                    .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_red_records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setContent(Bundle set, int serialNo){
        Bundle content = set.getBundle("" + serialNo);
        red_id.setText("序号：" + (serialNo+1));
        red_plate.setText("车牌：" + content.getString("plate"));
        red_userName.setText("客户：" + content.getString("userName"));
        red_CarMan.setText("送修人：" + content.getString("CarMan"));
/*        wr_consumeType.setText("电话：" + content.getString("Tel"));
        wr_cardCode.setText("手机：" + content.getString("Mobile"));*/
        String motel;
        if(content.getString("Tel").isEmpty() && content.getString("Mobile").isEmpty()){
            motel = "-";
        }
        else if(!content.getString("Tel").isEmpty() && content.getString("Mobile").isEmpty()){
            motel = content.getString("Tel");
        }
        else if(content.getString("Tel").isEmpty() && !content.getString("Mobile").isEmpty()){
            motel = content.getString("Mobile");
        }
        else{
            motel = content.getString("Tel") + ";" + content.getString("Mobile");
        }
        red_Motel.setText("电话：" + motel);
        red_RequestDate.setText("进厂时间：" + content.getString("RequestDate"));
        red_CompletionDate.setText("完工时间：" + content.getString("CompletionDate"));
        red_SettlementDate.setText("结算时间：" + content.getString("SettlementDate"));

        float sm = Float.parseFloat(content.getString("ShouldMoney"));
        int i_sm = (int)sm;
        red_ShouldMoney.setText("费用" + i_sm);

    }

    //根据关键词 kw 获取汇总信息
    public int getTotalInfo(Bundle ds, String kw){
        int total = 0;
        for(int i = 0; i < ds.size(); i++){
            Bundle b = ds.getBundle("" + i);;
            if(kw.equals("cost")){
                float t = Float.parseFloat(b.getString(kw));
                total = total + (int)t;
            }
            else{
                total = total + Integer.parseInt(b.getString(kw));
            }
        }

        return total;
    }
}

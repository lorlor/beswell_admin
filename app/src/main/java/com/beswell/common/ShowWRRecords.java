package com.beswell.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class ShowWRRecords extends ActionBarActivity {

    Intent intent;
    Spinner sp;
    int pos;

    TextView wr_id;
    TextView wr_plate;
    TextView wr_ccTime;
    TextView wr_ccType;
    TextView wr_consumeType;
    TextView wr_cardCode;
    TextView wr_cctimes;
    TextView wr_cost;
    TextView wr_pay;
    TextView wr_staff;
    TextView wr_memo;

    TextView total1;
    TextView total2;
    TextView total3;

    Button next;
    Button prev;

    ArrayAdapter<String> adapter;

    Bundle wr_records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wrrecords);

        intent = getIntent();
        sp = (Spinner)findViewById(R.id.item);
        wr_records = intent.getBundleExtra("Records");

        wr_id = (TextView) findViewById(R.id.wr_id);
        wr_plate = (TextView) findViewById(R.id.wr_plate);
        wr_ccTime = (TextView) findViewById(R.id.wr_ccTime);
        wr_ccType = (TextView) findViewById(R.id.wr_ccType);
        wr_consumeType = (TextView) findViewById(R.id.wr_consumeType);
        wr_cardCode = (TextView) findViewById(R.id.wr_cardCode);
        wr_cctimes = (TextView) findViewById(R.id.wr_cctimes);
        wr_cost = (TextView) findViewById(R.id.wr_cost);
        wr_pay = (TextView) findViewById(R.id.wr_pay);
        wr_staff = (TextView) findViewById(R.id.wr_staff);
        wr_memo = (TextView) findViewById(R.id.wr_memo);

        next = (Button) findViewById(R.id.nextPage);
        prev = (Button) findViewById(R.id.prevPage);

        total1 = (TextView)findViewById(R.id.total_1);
        total2 = (TextView)findViewById(R.id.total_2);
        total3 = (TextView)findViewById(R.id.total_3);
/*
        int totalCars = wr_records.size();
        total1.setText("数量合计：" + totalCars);
        int totalTimes = getTotalInfo(wr_records, "cctimes");
        total2.setText("洗车次数合计：" + totalTimes);
        int totalCost = getTotalInfo(wr_records, "cost");
        total3.setText("金额合计：" + totalCost);*/
        if(wr_records.size() != 0) {
            setContent(wr_records, 0);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            for (int i = 0; i < wr_records.size(); i++) {
                int temp = i + 1;
                adapter.add("第" + temp + "页");
            }

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    setContent(wr_records, position);

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
                        setContent(wr_records, pos);
                        sp.setSelection(pos);
                    }
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == wr_records.size() - 1) {
                        Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos++;
                        setContent(wr_records, pos);
                        sp.setSelection(pos);
                    }
                }
            });
        }
        else {
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
        getMenuInflater().inflate(R.menu.menu_show_records, menu);
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
        wr_id.setText("序号：" + (serialNo+1));
        wr_plate.setText("车牌：" + content.getString("plate"));
        wr_ccTime.setText("洗车时间：" + content.getString("ccTime"));
        wr_ccType.setText("类型：" + getCCType(content.getString("ccType")));
        wr_consumeType.setText("消费：" + content.getString("consumeType"));
        wr_cardCode.setText("卡号：" + content.getString("cardCode"));
        wr_cctimes.setText("次数：" + content.getString("cctimes"));
        float cash_f = Float.parseFloat(content.getString("cost"));
        int cash_i = (int)cash_f;
        wr_cost.setText("金额：" + cash_i);
        wr_pay.setText("付款方式：" + getChargeType(content.getString("pay")));
        wr_staff.setText("洗车人：" + content.getString("staff"));
        wr_memo.setText("备注：" + content.getString("memo"));
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

    public String getCCType(String type){
        switch(type){
            case "0":
                return "外观洗车";
            case "1":
                return "标准洗车";
            default:
                return "未知洗车类型";
        }
    }

    public String getChargeType(String type){
        switch (type){
            case "0":
                return "现金";
            case "1":
                return "POS";
            case "2":
                return "拉卡拉";
            case "3":
                return "支付宝";
            case "4":
                return "券";
            case "5":
                return "记账";
            case "6":
                return "免单";
            case "7":
                return "支票";
            case "8":
                return "微信";
            case "9":
                return "美团";
            default:
                return "未知付款方式";
        }
    }
}

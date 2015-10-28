package com.beswell.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beswell.car.R;

public class ShowCRRecords extends ActionBarActivity {

    Intent intent;
    Spinner sp;
    int pos;

    TextView acr_id;
    TextView acr_cardcode;
    TextView acr_plate;
    TextView acr_ccType;
    TextView acr_saleTime;
    TextView acr_userName;
    TextView acr_tel;
    TextView acr_serviceType;
    TextView acr_chargeType;
    TextView acr_chargeMoney;
    TextView acr_opid;
    TextView acr_memo;

    TextView total1;
    TextView total2;
    TextView total3;

    Button acr_next;
    Button acr_prev;

    ArrayAdapter<String> adapter;

    Bundle cr_records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_crrecords);

        intent = getIntent();
        sp = (Spinner)findViewById(R.id.acr_item);
        cr_records = intent.getBundleExtra("Records");

        acr_id = (TextView)findViewById(R.id.acr_id);
        acr_cardcode = (TextView)findViewById(R.id.acr_cardcode);
        acr_plate = (TextView)findViewById(R.id.acr_plate);
        acr_ccType = (TextView)findViewById(R.id.acr_ccType);
        acr_saleTime = (TextView)findViewById(R.id.acr_saleTime);
        acr_userName = (TextView)findViewById(R.id.acr_userName);
        acr_tel = (TextView)findViewById(R.id.acr_tel);
        acr_serviceType = (TextView)findViewById(R.id.acr_serviceType);
        acr_chargeType = (TextView)findViewById(R.id.acr_chargeType);
        acr_chargeMoney = (TextView)findViewById(R.id.acr_chargeMoney);
        acr_opid = (TextView)findViewById(R.id.acr_opid);
        acr_memo = (TextView)findViewById(R.id.acr_memo);

        acr_next = (Button)findViewById(R.id.acr_nextPage);
        acr_prev = (Button)findViewById(R.id.acr_prevPage);

        Log.d("Length of CR", cr_records.size() + "");

        if(cr_records.size() != 0) {
            setContent(cr_records, 0);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            for (int i = 0; i < cr_records.size(); i++) {
                int temp = i + 1;
                adapter.add("第" + temp + "页");
            }

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    setContent(cr_records, position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            acr_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == 0) {
                        Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos--;
                        setContent(cr_records, pos);
                        sp.setSelection(pos);
                    }
                }
            });

            acr_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == cr_records.size() - 1) {
                        Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos++;
                        setContent(cr_records, pos);
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
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == 4){
            finish();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_crrecords, menu);
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
        acr_id.setText("序号：" + (serialNo+1));
        acr_cardcode.setText("卡号：" + content.getString("cardcode"));
        acr_plate.setText("车牌：" + content.getString("plate"));
        acr_ccType.setText("类型：" + getCCType(content.getString("ccType")));
        acr_saleTime.setText("开卡时间：" + content.getString("saleTime"));
        acr_userName.setText("联系人：" + content.getString("userName"));
        acr_tel.setText("电话：" + content.getString("tel"));
        acr_serviceType.setText("服务等级：" + content.getString("serviceType"));
        acr_chargeType.setText("收款方式：" + getChargeType(content.getString("chargeType")));
        acr_chargeMoney.setText("金额" + content.getString("chargeMoney"));
        acr_memo.setText("备注：" + content.getString("memo"));
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

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

public class ShowRingRecords extends ActionBarActivity {

    Intent intent;

    Spinner sp;
    Button next;
    Button prev;
    int pos;

    TextView ring_id;
    TextView ring_CarCode;
    TextView ring_ClientName;
    TextView ring_CarMan;
    TextView ring_Motel;
    TextView ring_CarType;
    TextView ring_RequestDate;
    TextView ring_ShouldMoney;
    TextView ring_Progress;

    ArrayAdapter<String> adapter;

    Bundle records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ring_records);

        records = getIntent().getBundleExtra("Records");

        sp = (Spinner)findViewById(R.id.ring_item);
        next = (Button)findViewById(R.id.ring_nextPage);
        prev = (Button)findViewById(R.id.ring_prevPage);

        ring_id = (TextView)findViewById(R.id.ring_id);
        ring_CarCode = (TextView)findViewById(R.id.ring_CarCode);
        ring_ClientName = (TextView)findViewById(R.id.ring_ClientName);
        ring_CarMan = (TextView)findViewById(R.id.ring_CarMan);
        ring_Motel = (TextView)findViewById(R.id.ring_Motel);
        ring_CarType = (TextView)findViewById(R.id.ring_CarType);
        ring_RequestDate = (TextView)findViewById(R.id.ring_RequestDate);
        ring_ShouldMoney = (TextView)findViewById(R.id.ring_ShouldMoney);
        ring_Progress = (TextView)findViewById(R.id.ring_Progress);

        if(records.size() != 0) {
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
                                                    .setMessage("相关记录为空，没有记录")
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
        getMenuInflater().inflate(R.menu.menu_show_ring_records, menu);
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
        ring_id.setText("序号：" + (serialNo+1));
        ring_CarCode.setText("车牌：" + content.getString("CarCode"));
        ring_ClientName.setText("客户：" + content.getString("ClientName"));
        ring_CarMan.setText("送修人：" + content.getString("CarMan"));
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
            if(content.getString("Tel").equals(content.getString("Mobile")))
                motel = content.getString("Tel");
            else
                motel = content.getString("Tel") + ";" + content.getString("Mobile");
        }
        ring_CarType.setText("车型：" + content.getString("CarType"));
        ring_RequestDate.setText("进厂时间：" + content.getString("RequestDate"));
        float sm = Float.parseFloat(content.getString("ShouldMoney"));
        int i_sm = (int)sm;
        ring_ShouldMoney.setText("当前费用：" + i_sm);
        ring_Progress.setText("状态：" + content.getString("Progress"));

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

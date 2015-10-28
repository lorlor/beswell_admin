package com.beswell.plateService;

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

public class PlateShowCRRecords extends ActionBarActivity {

    Spinner sp;
    Button next;
    Button prev;
    int pos;

    TextView pcr_id;
    TextView pcr_cctime;
    TextView pcr_cardcode;
    TextView pcr_cctype;
    TextView pcr_cctimes;
    TextView pcr_cost;
    TextView pcr_pay;
    TextView pcr_memo;

    ArrayAdapter<String> adapter;
    Intent intent;

    Bundle records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_show_crrecords);

        intent = getIntent();
        records = intent.getBundleExtra("Rset");

        pcr_id = (TextView)findViewById(R.id.pcr_id);
        pcr_cctime = (TextView)findViewById(R.id.pcr_cctime);
        pcr_cardcode = (TextView)findViewById(R.id.pcr_cardcode);
        pcr_cctype = (TextView)findViewById(R.id.pcr_cctype);
        pcr_cctimes = (TextView)findViewById(R.id.pcr_cctimes);
        pcr_cost = (TextView)findViewById(R.id.pcr_cost);
        pcr_pay = (TextView)findViewById(R.id.pcr_pay);
        pcr_memo = (TextView)findViewById(R.id.pcr_memo);

        sp = (Spinner)findViewById(R.id.pcr_item);
        next = (Button)findViewById(R.id.pcr_nextPage);
        prev = (Button)findViewById(R.id.pcr_prevPage);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i = 0; i < records.size(); i++ ){
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
                if(pos == 0){
                    Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                }
                else{
                    pos--;
                    setContent(records, pos);
                    sp.setSelection(pos);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == records.size() - 1){
                    Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                }
                else{
                    pos++;
                    setContent(records, pos);
                    sp.setSelection(pos);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plate_show_crrecords, menu);
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
        pcr_id.setText("序号：" + (serialNo+1));
        pcr_cctime.setText("洗车时间：" + content.getString("ccTime"));
        pcr_cardcode.setText("客户：" + content.getString("cardCode"));
        pcr_cctype.setText("洗车类型：" + getCcType(Integer.parseInt(content.getString("ccType"))));
        pcr_cctimes.setText("次数：" + content.getString("cctimes"));
        pcr_cost.setText("付款金额：" + content.getString("cost"));
        pcr_pay.setText("付款方式：" + content.getString("pay"));
        pcr_memo.setText("备注：" + content.getString("memo"));
    }

    public String getCcType(int cctype){
        switch (cctype){
            case 0:
                return "外观洗车";
            case 1:
                return "标准洗车";
            default:
                return null;
        }
    }

    public String getPay(int pay){
        switch (pay){

        }

        return null;
    }
}

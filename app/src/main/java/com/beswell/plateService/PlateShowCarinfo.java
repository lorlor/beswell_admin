package com.beswell.plateService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.beswell.common.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlateShowCarinfo extends ActionBarActivity {

    Intent intent;
    Bundle ci_records;
    Bundle aw_r;
    Bundle sw_r;

    ViewPager vp;
    ViewPagerAdapter vpa;
    List<View> views;

    TextView car_info;
    TextView voucher_info;

    // For car info
    TextView ci_plate;
    TextView ci_chassisNum;
    TextView ci_engineNum;
    TextView ci_brandName;
    TextView ci_seriesName;
    TextView ci_carColor;
    TextView ci_userName;
    TextView ci_phone;
    TextView ci_buyDate;

    // For voucher info
    TextView pvi_plate;
    TextView pvi_ccType;
    TextView pvi_cardcode;
    TextView pvi_totalTimes;
    TextView pvi_leftTimes;
    TextView pvi_beginTime;
    TextView pvi_endTime;

    Spinner sp;
    Button next;
    Button prev;

    int pos;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_show_carinfo);

        final Bundle r = new Bundle();

        car_info = (TextView) findViewById(R.id.car_info);
        voucher_info = (TextView)findViewById(R.id.voucher_info);

        intent = getIntent();
        vp = (ViewPager)findViewById(R.id.vp);

        ci_records = intent.getBundleExtra("Carinfo");
        aw_r = intent.getBundleExtra("Awvoucher");
        sw_r = intent.getBundleExtra("Swvoucher");
        for(int index = 0; index < aw_r.size(); index++){
            r.putBundle(""+index, aw_r.getBundle(""+index));
        }
        for(int index = aw_r.size(); index < aw_r.size() + sw_r.size(); index++){
            r.putBundle("" + index, sw_r.getBundle(""+(index - aw_r.size())));
        }

        initViews();
        if(!ci_records.isEmpty()) {
            setCarinfo(ci_records.getBundle("0"));
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                                                .setMessage("抱歉，没有对应车辆的信息")
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

/*         plate = (TextView)findViewById(R.id.pci_plate);
        chassisNum = (TextView)findViewById(R.id.pci_chassisNum);
        engineNum = (TextView)findViewById(R.id.pci_engineNum);
        brandName = (TextView)findViewById(R.id.pci_brandName);
        seriesName = (TextView)findViewById(R.id.pci_seriesName);
        carColor = (TextView)findViewById(R.id.pci_carColor);
        userName = (TextView)findViewById(R.id.pci_userName);
        phone = (TextView)findViewById(R.id.pci_phone);
        buyDate = (TextView)findViewById(R.id.pci_buyDate);

        voucher = (Button)findViewById(R.id.pci_voucher);

        setCarinfo(ci_records);*/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i = 0; i < r.size(); i++ ){
            int temp = i + 1;
            adapter.add("第" + temp + "页");
        }

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                setVoucherinfo(r, position);

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
                    setVoucherinfo(r, pos);
                    sp.setSelection(pos);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos == r.size() - 1){
                    Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                }
                else{
                    pos++;
                    setVoucherinfo(r, pos);
                    sp.setSelection(pos);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plate_show_carinfo, menu);
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

    public void setCarinfo(Bundle input){
        ci_plate.setText("车牌："+input.getString("plate"));
        ci_chassisNum.setText("车架号：" + input.getString("chassisNum"));
        ci_engineNum.setText("发动机号：" + input.getString("engineNum"));
        ci_brandName.setText("品牌：" + input.getString("brandName"));
        ci_seriesName.setText("车系：" + input.getString("seriesName"));
        ci_carColor.setText("颜色：" + input.getString("carColor"));
        ci_userName.setText("姓名：" + input.getString("userName"));
        ci_phone.setText("电话：" + input.getString("phone"));
        ci_buyDate.setText("购买日期：" + input.getString("buyDate"));
    }

    public void setVoucherinfo(Bundle input, int serialNo){
        Bundle content = input.getBundle("" + serialNo);
        pvi_plate.setText("车牌：" + content.getString("plate"));
        pvi_ccType.setText("类型：" + getCCType(Integer.parseInt(content.getString("ccType"))));
        pvi_cardcode.setText("卡号：" + content.getString("cardcode"));
        pvi_totalTimes.setText("初始：" + content.getString("totalTimes"));
        pvi_leftTimes.setText("剩余：" + content.getString("leftTimes"));
        pvi_beginTime.setText("服务起始：" + content.getString("beginTime"));
        pvi_endTime.setText("服务到期：" + content.getString("endTime"));
    }

    public void initViews(){
        views = new ArrayList<View>();

        LayoutInflater inflater = LayoutInflater.from(this);

        views.add(inflater.inflate(R.layout.vp_carinfo, null));
        views.add(inflater.inflate(R.layout.vp_voucherinfo, null));

        car_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0);
            }
        });

        voucher_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });

        // Get widgets in vp_carinfo layout file.
        ci_plate = (TextView)views.get(0).findViewById(R.id.pci_plate);
        ci_chassisNum = (TextView)views.get(0).findViewById(R.id.pci_chassisNum);
        ci_engineNum = (TextView)views.get(0).findViewById(R.id.pci_engineNum);
        ci_brandName = (TextView)views.get(0).findViewById(R.id.pci_brandName);
        ci_seriesName = (TextView)views.get(0).findViewById(R.id.pci_seriesName);
        ci_carColor = (TextView)views.get(0).findViewById(R.id.pci_carColor);
        ci_userName = (TextView)views.get(0).findViewById(R.id.pci_userName);
        ci_phone = (TextView)views.get(0).findViewById(R.id.pci_phone);
        ci_buyDate = (TextView)views.get(0).findViewById(R.id.pci_buyDate);

        // Get widgets in vp_voucherinfo layout file.
        pvi_plate = (TextView)views.get(1).findViewById(R.id.vi_plate);
        pvi_ccType = (TextView)views.get(1).findViewById(R.id.vi_ccType);
        pvi_cardcode = (TextView)views.get(1).findViewById(R.id.vi_cardcode);
        pvi_totalTimes = (TextView)views.get(1).findViewById(R.id.vi_totalTimes);
        pvi_leftTimes = (TextView)views.get(1).findViewById(R.id.vi_leftTimes);
        pvi_beginTime = (TextView)views.get(1).findViewById(R.id.vi_beginTime);
        pvi_endTime = (TextView)views.get(1).findViewById(R.id.vi_endTime);

        sp = (Spinner)views.get(1).findViewById(R.id.vi_item);
        next = (Button)views.get(1).findViewById(R.id.vi_nextPage);
        prev = (Button)views.get(1).findViewById(R.id.vi_prevPage);

        vpa = new ViewPagerAdapter(views, this);
        vp.setAdapter(vpa);
    }

    public String getCCType(int id){
        switch (id){
            case 0:
                return "外观洗车";
            case 1:
                return "标准洗车";
            default:
                return "未定义";
        }
    }
}

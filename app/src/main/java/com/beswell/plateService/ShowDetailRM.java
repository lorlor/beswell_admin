package com.beswell.plateService;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beswell.car.R;
import com.beswell.common.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailRM extends ActionBarActivity {

    Intent intent;

    ViewPager vp;
    ViewPagerAdapter vpa;
    List<View> views;

    TextView overview;
    TextView item;
    TextView part;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_rm);

        intent = getIntent();
        vp = (ViewPager)findViewById(R.id.detail_rm);
        initViews();
        initTextVbtn();

        DeploymentOfOverview();
        DeploymentOfItem();
        DeploymentOfPart();
        DeploymentOfTotal();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_items_rm, menu);
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

    public void initViews(){
        views = new ArrayList<View>();

        LayoutInflater inflater = LayoutInflater.from(this);

        views.add(inflater.inflate(R.layout.rm_overview, null));
        views.add(inflater.inflate(R.layout.rm_items, null));
        views.add(inflater.inflate(R.layout.rm_part, null));
        views.add(inflater.inflate(R.layout.rm_total, null));

        vpa = new ViewPagerAdapter(views, this);
        vp.setAdapter(vpa);

    }

    public void initTextVbtn(){
        overview = (TextView)findViewById(R.id.rm_overview);
        item = (TextView)findViewById(R.id.rm_items);
        part = (TextView)findViewById(R.id.rm_part);
        total = (TextView)findViewById(R.id.rm_total);

        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0);
            }
        });

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(1);
            }
        });

        part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(2);
            }
        });

        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(3);
            }
        });
    }

    public void DeploymentOfOverview(){
        Bundle overview = new Bundle();
        overview = intent.getBundleExtra("Overview");

        TextView serviceNo = (TextView)views.get(0).findViewById(R.id.ov_sn);
        TextView settlementDate = (TextView)views.get(0).findViewById(R.id.ov_sd);
        TextView settlementOperator = (TextView)views.get(0).findViewById(R.id.ov_so);

        TextView carMan = (TextView)views.get(0).findViewById(R.id.ov_cm);
        TextView carCode = (TextView)views.get(0).findViewById(R.id.ov_cc);
        TextView carManMobile = (TextView)views.get(0).findViewById(R.id.ov_cmm);
        TextView requestDate = (TextView)views.get(0).findViewById(R.id.ov_rd);
        TextView seriesName = (TextView)views.get(0).findViewById(R.id.ov_ssn);
        TextView carMileage = (TextView)views.get(0).findViewById(R.id.ov_cma);

        //想法：空指针的错误应该是由空间的放置引起的
        serviceNo.setText("委托书号：" + overview.getString("ServiceNo"));
        settlementDate.setText("结算日期：" + overview.getString("SettlementDate"));
        settlementOperator.setText("结算操作员：" + overview.getString("SettlementOperator"));
        carMan.setText("托修单位：" + overview.getString("CarMan"));
        carCode.setText("车牌号：" + overview.getString("CarCode"));
        carManMobile.setText("电话：" + overview.getString("CarManMobile"));
        requestDate.setText("进厂日期：" + overview.getString("RequestDate"));
        seriesName.setText("车型：" + overview.getString("seriesName"));
        carMileage.setText("里程：" + overview.getString("CarMileage"));
    }

    public void DeploymentOfItem(){
        Bundle item = intent.getBundleExtra("Item");
        if(item.size() == 0){
            AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                                                    .setMessage("抱歉，没有对应的服务记录")
                                                    .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        else{
            LinearLayout ill = (LinearLayout) views.get(1).findViewById(R.id.rm_items);
            TextView[] tv = new TextView[item.size()];
            for(int i = 0; i < item.size(); i++){
                tv[i] = new TextView(this);
                tv[i].setText("维修项目：" + item.getBundle(""+i).getString("ItemsName") + "|" + "工时：" + item.getBundle("" + i).getString("ManHour"));
                ill.addView(tv[i]);
            }
        }
    }

    public void DeploymentOfPart(){
        Bundle part = intent.getBundleExtra("Part");
        if(part.size() == 0){
            AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                                                    .setMessage("抱歉，没有对应的配件记录")
                                                    .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        else{
            LinearLayout pll = (LinearLayout) views.get(2).findViewById(R.id.rm_part);
            TextView[] tv = new TextView[part.size()];
            for(int i = 0; i < part.size(); i++){
                tv[i] = new TextView(this);
                tv[i].setText("用料名称：" + part.getBundle("" + i).getString("PartName") + "|"
                                + "数量：" + part.getBundle("" + i).getString("Amount") + "|"
                                + "单价：" + part.getBundle("" + i).getString("Price") + "|"
                                + "金额：" + part.getBundle("" + i).getString("CostMoney"));
                pll.addView(tv[i]);
            }
        }
    }

    public void DeploymentOfTotal(){
        Bundle total = intent.getBundleExtra("Total");

        TextView tt_am = (TextView)views.get(3).findViewById(R.id.tt_am);
        TextView tt_atm = (TextView)views.get(3).findViewById(R.id.tt_atm);
        TextView tt_cm = (TextView)views.get(3).findViewById(R.id.tt_cm);
        TextView tt_lm = (TextView)views.get(3).findViewById(R.id.tt_lm);
        TextView tt_sm = (TextView)views.get(3).findViewById(R.id.tt_sm);
        TextView tt_sn = (TextView)views.get(3).findViewById(R.id.tt_sn);

        tt_am.setText("总额：￥" + total.getString("AllMoney"));
        tt_atm.setText("实收：￥" + total.getString("AcceptMoney"));
        tt_cm.setText("优惠：￥" + total.getString("CheapMoney"));
        tt_lm.setText("余额：￥" + total.getString("LeaveMoney"));
        tt_sm.setText("应收：￥" + total.getString("ShouldMoney"));
        tt_sn.setText("结算：￥" + total.getString("SettlementName"));
    }
}

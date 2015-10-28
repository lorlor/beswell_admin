package com.beswell.car;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.beswell.common.ShowRingRecords;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by beswell10 on 2015/8/10.
 */
public class MainFragment extends Fragment{

    String IP = "192.168.0.100";

    Button create_card;             // 开卡
    Button cash_washing;            // 现金洗车
    Button card_record;             // 开卡记录
    Button wash_record;             // 洗车记录
    Button free_washing;            // 免费洗车
    Button rping_cars;              // 在修车辆
    Button rped_cars;               // 完工车辆
//    Button exit_system;             // 退出系统

    Button plate_enter;
    EditText plate;

    public static MainFragment newInstance(String digest, String usercode){
        MainFragment mf = new MainFragment();

        Bundle para = new Bundle();
        para.putString("digest", digest);
        para.putString("usercode", usercode);
        mf.setArguments(para);

        return mf;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        hash = getArguments().getString("digest");
        usercode = getArguments().getString("usercode");

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        create_card     = (Button)v.findViewById(R.id.create_card);
        cash_washing    = (Button)v.findViewById(R.id.cash_washing);
        card_record     = (Button)v.findViewById(R.id.card_record);
        wash_record     = (Button)v.findViewById(R.id.wash_record);
        free_washing    = (Button)v.findViewById(R.id.free_washing);
//        exit_system     = (Button)v.findViewById(R.id.exit_system);
        rping_cars      = (Button)v.findViewById(R.id.reparing_cars);
        rped_cars       = (Button)v.findViewById(R.id.repaired_cars);

        plate_enter     = (Button)v.findViewById(R.id.plate_enter);
        plate = (EditText)v.findViewById(R.id.plate);

        create_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCardFragment ccf = CreateCardFragment.newInstance(hash, usercode);
                ccf.show(getFragmentManager(), "开卡");
            }
        });

        cash_washing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashWashingFragment cwf = CashWashingFragment.newInstance(hash);
                cwf.show(getFragmentManager(), "现金洗车");
            }
        });

        card_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardRecordFragment crf = CardRecordFragment.newInstance(hash);
                crf.show(getFragmentManager(), "开卡记录");
            }
        });

        wash_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WashRecordFragment wrf = WashRecordFragment.newInstance(hash);
                wrf.show(getFragmentManager(), "洗车记录");
            }
        });

        free_washing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeWashingFragment fwf = FreeWashingFragment.newInstance(hash);
                fwf.show(getFragmentManager(), "免费洗车");
            }
        });

        rping_cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);
            }
        });

        rped_cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RpedCarsFragment rcf = RpedCarsFragment.newInstance(hash);
                rcf.show(getFragmentManager(), "完工车辆查询");
            }
        });

/*        exit_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitSystemFragment esf = new ExitSystemFragment();
                esf.show(getFragmentManager(), "退出系统");
            }
        });*/

        plate_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plate_str = plate.getText().toString();
                if(plate_str.length() < 7){
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                        .setMessage("车牌格式错误。格式：鲁A12345")
                                                        .create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                else{
                    String pp = plate_str.trim().replace("-", "").replace(" ", "").toUpperCase();
                    Intent intent = new Intent(getActivity(), ServiceOnPlate.class);
                    intent.putExtra("plate", pp);
                    intent.putExtra("hash", hash);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        Bundle records;

/*        String nameSpace = "http://192.168.0.100";
        String url = "http://192.168.0.100/server.php";
        String methodName = "rping_record";
        String soapAction = "http://192.168.0.100/server.php/rping_record";*/

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(getActivity(), ShowRingRecords.class);

            intent.putExtra("Records", records);
/*            intent.putExtra("Out", out);*/

            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "rping_record";
            String soapAction = "http://" + params[0] + "/server.php/rping_record";

            records = new Bundle();

            int stCode = 0;
            int rsCount = 0;

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);

            request.addProperty("stCode", stCode);
            request.addProperty("rsCount", rsCount);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.setOutputSoapObject(request);


            HttpTransportSE httpTransport = new HttpTransportSE(url);

            httpTransport.debug = true;
            try {
                httpTransport.call(soapAction, envelope);
            } catch (HttpResponseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //send request

            SoapObject result = (SoapObject) envelope.bodyIn;

            //temp为所有记录的集合，其中每一项都是一条记录
            Vector<SoapObject> temp = (Vector<SoapObject>)result.getProperty("return");
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < temp.size(); i++) {
                Bundle b = new Bundle();
                for(int j = 0; j < temp.elementAt(i).getPropertyCount(); j++) {
                    if(j % 2 == 0) {
                        String t;

                        t = temp.elementAt(i).getProperty(j).toString().replace("anyType{", "");
                        t = t.replace("}", "");
                        t = t.replace(";", " ");
                        t = t.replace("=", " ");
                        String[] rs = t.split(" ");

                        if(rs[1].equals("CarCode") || rs[1].equals("ClientName")
                                || rs[1].equals("CarMan") || rs[1].equals("Mobile")
                                || rs[1].equals("Tel") || rs[1].equals("RequestDate")
                                || rs[1].equals("Progress") || rs[1].equals("ShouldMoney")
                                || rs[1].equals("CarType")){
                            switch (rs.length){
                                case 4:
                                    t = rs[1] + " >> " + " - " + "\n";
                                    b.putString(rs[1], "-");
                                    break;
                                case 5:
                                    t = rs[1] + " >> " + rs[4] + "\n";
                                    b.putString(rs[1], rs[4]);
                                    break;
                                case 6:
                                    t = rs[1] + " >> " + rs[4] + " " + rs[5].substring(0,5) + "\n";
                                    b.putString(rs[1], rs[4] + " " + rs[5].substring(0,5));
                                    break;

                            }

                            sb.append(t + rs.length + "\n");
                        }
                        else{
                            continue;
                        }
                    }

                }
                sb.append("\n");
                records.putBundle("" + i, b);

            }
            Log.d("Response", temp.toString());


            return null;
        }
    }

    String hash;
    String usercode;
}

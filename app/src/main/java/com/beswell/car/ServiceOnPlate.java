package com.beswell.car;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.beswell.plateService.AwVoucherFragment;
import com.beswell.plateService.BindCarFragment;
import com.beswell.plateService.CardServiceFragment;
import com.beswell.plateService.PlateShowCRRecords;
import com.beswell.plateService.PlateShowCarinfo;
import com.beswell.plateService.PlateShowRmRecords;
import com.beswell.plateService.ResetPasswdFragment;
import com.beswell.plateService.SwVoucherFragment;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Vector;


public class ServiceOnPlate extends ActionBarActivity {

    String IP = "192.168.0.100";

    Button awVoucher;
    Button swVoucher;
    Button ccRecord;
    Button rmRecord;
    Button carInfo;
    Button cardService;
    Button bindCar;
    Button rstPasswd;

    String hash;
    String plate;

    Bundle aw_records;
    Bundle sw_records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hash = getIntent().getStringExtra("hash");
        plate = getIntent().getStringExtra("plate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_on_plate);
        this.setTitle(plate);

        aw_records = new Bundle();
        sw_records = new Bundle();

        awVoucher    = (Button)findViewById(R.id.aw_voucher);
        swVoucher    = (Button)findViewById(R.id.sw_voucher);
        ccRecord      = (Button)findViewById(R.id.cc_record);
        rmRecord     = (Button)findViewById(R.id.rm_record);
        carInfo      = (Button)findViewById(R.id.car_info);
        cardService  = (Button)findViewById(R.id.card_service);
        bindCar      = (Button)findViewById(R.id.bind_car);
        rstPasswd    = (Button)findViewById(R.id.reset_passwd);

        awVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonAwTriggered bat = new ButtonAwTriggered();
                bat.execute(IP);
            }
        });

        swVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonSwTriggered bst = new ButtonSwTriggered();
                bst.execute(IP);
            }
        });

        ccRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonCrTriggered bct = new ButtonCrTriggered();
                bct.execute(IP);
            }
        });

        rmRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonRmTriggered brt = new ButtonRmTriggered();
                brt.execute(IP);
            }
        });

        carInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonCiTriggered bct = new ButtonCiTriggered();
                bct.execute(IP);
            }
        });

        cardService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonCsTriggered bct = new ButtonCsTriggered();
                bct.execute(IP);
            }
        });

        bindCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindCarFragment bcf = BindCarFragment.newInstance(aw_records, sw_records);
                bcf.show(getSupportFragmentManager(), "绑定车辆");
            }
        });

        rstPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswdFragment rpf = ResetPasswdFragment.newInstance(hash, plate);
                rpf.show(getSupportFragmentManager(), "重置密码");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_on_plate, menu);
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

    class ButtonAwTriggered extends AsyncTask<String, Void, String> {

/*        String nameSpace = "http://192.168.0.100";
        String url = "http://192.168.0.100/server.php";
        String methodName = "plate_voucherInfo";
        String soapAction = "http://192.168.0.100/server.php/plate_voucherInfo";*/

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("SOP Debug", aw_records.size()+"");

            AwVoucherFragment awf = AwVoucherFragment.newInstance(aw_records, hash, plate);
            awf.show(getSupportFragmentManager(), "外观洗车券");
        }

        @Override
        protected String doInBackground(String... params) {
//            aw_records = new Bundle();

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_voucherInfo";
            String soapAction = "http://" + params[0] + "/server.php/plate_voucherInfo";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate);
            request.addProperty("ccType", 0);           // 0 represents [Apperance Cleaning]

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

                        if(rs[1].equals("cardcode") || rs[1].equals("id") || rs[1].equals("saletime") || rs[1].equals("feeType")
                                || rs[1].equals("plate") || rs[1].equals("ccType") || rs[1].equals("cardcode") || rs[1].equals("totalTimes")
                                || rs[1].equals("leftTimes") || rs[1].equals("beginTime") || rs[1].equals("endTime")){
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

//                        sb.append(t + rs.length + "\n");

                    }

                }
                sb.append("\n");
                aw_records.putBundle("" + i, b);
            }


            return null;
        }
    }

    class ButtonSwTriggered extends AsyncTask<String, Void, String> {

/*        String nameSpace = "http://192.168.0.100";
        String url = "http://192.168.0.100/server.php";
        String methodName = "plate_voucherInfo";
        String soapAction = "http://192.168.0.100/server.php/plate_voucherInfo";*/

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SwVoucherFragment svf = SwVoucherFragment.newInstance(sw_records, hash, plate);
            svf.show(getSupportFragmentManager(), "标准洗车券");
        }

        @Override
        protected String doInBackground(String... params) {
//            sw_records = new Bundle();

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_voucherInfo";
            String soapAction = "http://" + params[0] + "/server.php/plate_voucherInfo";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate);
            request.addProperty("ccType", 1);           // 1 represents [Standard Cleaning]

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

                        if(rs[1].equals("cardcode") || rs[1].equals("id") || rs[1].equals("saletime") || rs[1].equals("feeType")
                                || rs[1].equals("plate") || rs[1].equals("ccType") || rs[1].equals("cardcode") || rs[1].equals("totalTimes")
                                || rs[1].equals("leftTimes") || rs[1].equals("beginTime") || rs[1].equals("endTime")){
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

//                        sb.append(t + rs.length + "\n");

                    }

                }
                sb.append("\n");
                sw_records.putBundle("" + i, b);
            }
            Log.d("SOP Debug", sw_records.size()+"");

            return null;
        }
    }

    class ButtonCrTriggered extends AsyncTask<String, Void, String>{
        Bundle records;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(ServiceOnPlate.this, PlateShowCRRecords.class);
            intent.putExtra("Rset", records);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            records = new Bundle();

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_CCRecord";
            String soapAction = "http://" + params[0] + "/server.php/plate_CCRecord";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);            // Replaced by hash String
            request.addProperty("plate", plate);         // Replaced by plate String

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

                        if(rs[1].equals("ccTime") || rs[1].equals("cardCode") || rs[1].equals("ccType") || rs[1].equals("cctimes")
                                || rs[1].equals("cost") || rs[1].equals("pay") || rs[1].equals("memo")){
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
//                sb.append("\n");
                records.putBundle("" + i, b);
            }
            Log.d("Out", sb.toString());

            return null;
        }
    }

    class ButtonRmTriggered extends AsyncTask<String, Void, String>{

        Bundle records;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(ServiceOnPlate.this, PlateShowRmRecords.class);
            intent.putExtra("Rset", records);
            intent.putExtra("hash", hash);
            intent.putExtra("plate", plate);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_mainRm";
            String soapAction = "http://" + params[0] + "/server.php/plate_mainRm";

            records = new Bundle();

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);            // Replaced by hash String
            request.addProperty("plate", plate);         // Replaced by plate String

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

                        if(rs[1].equals("CarMan") || rs[1].equals("seriesName") || rs[1].equals("CarMileage") || rs[1].equals("RequestDate")
                                || rs[1].equals("ServiceCost") || rs[1].equals("SettlementName") || rs[1].equals("MainRepaire") || rs[1].equals("ServiceNo")
                                || rs[1].equals("SettlementOperator") || rs[1].equals("SettlementDate") || rs[1].equals("CarCode") || rs[1].equals("CarManMobile")
                                || rs[1].equals("AllMoney") || rs[1].equals("AcceptMoney") || rs[1].equals("CheapMoney") || rs[1].equals("LeaveMoney")
                                || rs[1].equals("ShouldMoney")){
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
                        Log.d("RM debug", b.toString());
                    }

                }
//                sb.append("\n");
                records.putBundle("" + i, b);
            }

            return null;
        }
    }

    class ButtonCiTriggered extends AsyncTask<String, Void, String>{

        Bundle records;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(ServiceOnPlate.this, PlateShowCarinfo.class);
            intent.putExtra("Carinfo", records);
            intent.putExtra("Awvoucher", aw_records);
            intent.putExtra("Swvoucher", sw_records);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... params) {
            records = new Bundle();

            String nameSpace = "http://" + params[0];
            String url = "http://"  + params[0] + "/server.php";
            String methodName = "plate_carInfo";
            String soapAction = "http://"  + params[0] + "/server.php/plate_carInfo";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate);

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
            Log.d("Size of temp", temp.size() + "");
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

                        if(rs[1].equals("plate") || rs[1].equals("chassisNum") || rs[1].equals("engineNum") || rs[1].equals("brandName")
                                || rs[1].equals("seriesName") || rs[1].equals("carColor") || rs[1].equals("userName") || rs[1].equals("phone")
                                || rs[1].equals("buyDate")){
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

//                        sb.append(t + rs.length + "\n");

                    }

                }
                sb.append("\n");
                Log.d("Debug", sb.toString());
                records.putBundle("" + i, b);
            }

            if(aw_records.size() == 0){
                String at_nameSpace = "http://" + params[0];
                String at_url = "http://" + params[0] + "/server.php";
                String at_methodName = "plate_voucherInfo";
                String at_soapAction = "http://" + params[0] + "/server.php/plate_voucherInfo";

//                aw_records = new Bundle();

                SoapObject at_request = new SoapObject(at_nameSpace, at_methodName);
                at_request.addProperty("hashStr", hash);
                at_request.addProperty("plate", plate);
                at_request.addProperty("ccType", 0);           // 0 represents [Apperance Cleaning]

                SoapSerializationEnvelope at_envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                at_envelope.setOutputSoapObject(at_request);


                HttpTransportSE at_httpTransport = new HttpTransportSE(at_url);

                httpTransport.debug = true;
                try {
                    at_httpTransport.call(at_soapAction, at_envelope);
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
                SoapObject at_result = (SoapObject) at_envelope.bodyIn;

                //temp为所有记录的集合，其中每一项都是一条记录
                Vector<SoapObject> at_temp = (Vector<SoapObject>)at_result.getProperty("return");
                for(int i = 0; i < at_temp.size(); i++) {
                    Bundle b = new Bundle();
                    for(int j = 0; j < at_temp.elementAt(i).getPropertyCount(); j++) {
                        if(j % 2 == 0) {
                            String t;

                            t = at_temp.elementAt(i).getProperty(j).toString().replace("anyType{", "");
                            t = t.replace("}", "");
                            t = t.replace(";", " ");
                            t = t.replace("=", " ");
                            String[] rs = t.split(" ");

                            if(rs[1].equals("cardcode") || rs[1].equals("id") || rs[1].equals("saletime") || rs[1].equals("feeType")
                                    || rs[1].equals("plate") || rs[1].equals("ccType") || rs[1].equals("cardcode") || rs[1].equals("totalTimes")
                                    || rs[1].equals("leftTimes") || rs[1].equals("beginTime") || rs[1].equals("endTime")){
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

                            }
                            else{
                                continue;
                            }

//                        sb.append(t + rs.length + "\n");

                        }

                    }
                    aw_records.putBundle("" + i, b);
                }
            }

            if(sw_records.size() == 0){
                String st_nameSpace = "http://" + params[0];
                String st_url = "http://"  + params[0] + "/server.php";
                String st_methodName = "plate_voucherInfo";
                String st_soapAction = "http://"  + params[0] + "/server.php/plate_voucherInfo";

//                sw_records = new Bundle();

                SoapObject st_request = new SoapObject(st_nameSpace, st_methodName);
                st_request.addProperty("hashStr", hash);
                st_request.addProperty("plate", plate);
                st_request.addProperty("ccType", 1);           // 1 represents [Standard Cleaning]

                SoapSerializationEnvelope st_envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                st_envelope.setOutputSoapObject(st_request);


                HttpTransportSE st_httpTransport = new HttpTransportSE(st_url);

                httpTransport.debug = true;
                try {
                    st_httpTransport.call(st_soapAction, st_envelope);
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
                SoapObject st_result = (SoapObject) st_envelope.bodyIn;

                //temp为所有记录的集合，其中每一项都是一条记录
                Vector<SoapObject> st_temp = (Vector<SoapObject>)st_result.getProperty("return");
                for(int i = 0; i < st_temp.size(); i++) {
                    Bundle b = new Bundle();
                    for(int j = 0; j < st_temp.elementAt(i).getPropertyCount(); j++) {
                        if(j % 2 == 0) {
                            String t;

                            t = st_temp.elementAt(i).getProperty(j).toString().replace("anyType{", "");
                            t = t.replace("}", "");
                            t = t.replace(";", " ");
                            t = t.replace("=", " ");
                            String[] rs = t.split(" ");

                            if(rs[1].equals("cardcode") || rs[1].equals("id") || rs[1].equals("saletime") || rs[1].equals("feeType")
                                    || rs[1].equals("plate") || rs[1].equals("ccType") || rs[1].equals("cardcode") || rs[1].equals("totalTimes")
                                    || rs[1].equals("leftTimes") || rs[1].equals("beginTime") || rs[1].equals("endTime")){
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
                            }
                            else{
                                continue;
                            }

//                        sb.append(t + rs.length + "\n");

                        }

                    }
                    sw_records.putBundle("" + i, b);
                }
            }

            return null;
        }
    }

    class ButtonCsTriggered extends AsyncTask<String, Void, String>{

        String userName = "";
        String userTel = "";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CardServiceFragment csf = CardServiceFragment.newInstance(hash, plate, userName, userTel);
            csf.show(getSupportFragmentManager(), "开卡服务");
        }

        @Override
        protected String doInBackground(String... params) {
            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_carInfo";
            String soapAction = "http://" + params[0] + "/server.php/plate_carInfo";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate);

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
            Bundle b = new Bundle();
            if(temp.size() != 0) {
                for (int j = 0; j < temp.elementAt(0).getPropertyCount(); j++) {
                    if (j % 2 == 0) {
                        String t;

                        t = temp.elementAt(0).getProperty(j).toString().replace("anyType{", "");
                        t = t.replace("}", "");
                        t = t.replace(";", " ");
                        t = t.replace("=", " ");
                        String[] rs = t.split(" ");

                        if (rs[1].equals("plate") || rs[1].equals("chassisNum") || rs[1].equals("engineNum") || rs[1].equals("brandName")
                                || rs[1].equals("seriesName") || rs[1].equals("carColor") || rs[1].equals("userName") || rs[1].equals("phone")
                                || rs[1].equals("buyDate")) {
                            switch (rs.length) {
                                case 4:
                                    t = rs[1] + " >> " + " - " + "\n";
                                    b.putString(rs[1], "-");
                                    break;
                                case 5:
                                    t = rs[1] + " >> " + rs[4] + "\n";
                                    b.putString(rs[1], rs[4]);
                                    break;
                                case 6:
                                    t = rs[1] + " >> " + rs[4] + " " + rs[5].substring(0, 5) + "\n";
                                    b.putString(rs[1], rs[4] + " " + rs[5].substring(0, 5));
                                    break;

                            }

                            sb.append(t + rs.length + "\n");
                        } else {
                            continue;
                        }

//                        sb.append(t + rs.length + "\n");

                    }

                }
                userName = b.getString("userName");
                userTel = b.getString("phone");
            }

            return null;
        }
    }
}

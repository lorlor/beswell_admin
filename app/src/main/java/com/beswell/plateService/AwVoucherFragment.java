package com.beswell.plateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beswell.car.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by beswell10 on 2015/8/14.
 */
public class AwVoucherFragment extends DialogFragment {

    String IP = "192.168.0.100";

    Spinner aw_voucher;
    RadioGroup aw_rg;
    EditText aw_cost;
    Spinner aw_pay;
    Spinner aw_staff;
    EditText aw_memo;
    TextView sale_date;

    Button aw_y;
    Button aw_n;

    ArrayAdapter<String> adapter;

    Bundle rs;
    String plate;
    String hash;

    int cctimes;
    int consumeType;
    int ccVoucherId;
    int cost;
    int pay;
    String staff;
    String memo;

    int stCode;
    RadioButton rb;
    String voucher;

    public static AwVoucherFragment newInstance(Bundle b, String h_str, String p_str){
        AwVoucherFragment awf = new AwVoucherFragment();
        Bundle temp_v = new Bundle();
        temp_v.putBundle("Rs", b);
        temp_v.putString("hash", h_str);
        temp_v.putString("plate", p_str);
        awf.setArguments(temp_v);

        return awf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.sf_aw_voucher, null);

        rs = getArguments().getBundle("Rs");

        hash = getArguments().getString("hash");
        plate = getArguments().getString("plate");
        Log.d("AVF Debug", rs.toString());

        aw_voucher = (Spinner)v.findViewById(R.id.aw_xca);
        aw_rg = (RadioGroup)v.findViewById(R.id.aw_times);
        aw_cost = (EditText)v.findViewById(R.id.aw_cost);
        aw_pay = (Spinner)v.findViewById(R.id.aw_pay);
        aw_staff = (Spinner)v.findViewById(R.id.aw_staff);
        aw_memo = (EditText)v.findViewById(R.id.aw_memo);
        sale_date = (TextView)v.findViewById(R.id.sale_date);

        aw_y = (Button)v.findViewById(R.id.aw_btnYes);
        aw_n = (Button)v.findViewById(R.id.aw_btnNo);

        String [] voucher_v;
        if(rs.size() != 0 ){
            voucher_v = new String[rs.size()];
            for(int i = 0; i < rs.size(); i++){
                voucher_v[i] = rs.getBundle(""+i).getString("cardcode");
                Log.d("voucher_v["+i+"]", voucher_v[i]);
            }
        }
        else{
            voucher_v = new String[0];
        }

        adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, voucher_v);
        aw_voucher.setAdapter(adapter);
        aw_voucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                voucher = aw_voucher.getSelectedItem().toString();
                if(rs.size() == 0){
                    sale_date.setText("开卡日期：-");
                }
                else{
                    sale_date.setText("开卡日期：" + getSaleTime(voucher, rs));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                voucher = rs.getBundle("" + 0).getString("cardcode");
                if(rs.size() == 0){
                    sale_date.setText("开卡日期：-");
                }
                else{
                    sale_date.setText("开卡日期：" + getSaleTime(voucher,rs));
                }
            }
        });

        rb = (RadioButton)v.findViewById(aw_rg.getCheckedRadioButtonId());
        cctimes = Integer.parseInt(rb.getText().toString().substring(0, 1));
        aw_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = (RadioButton)v.findViewById(aw_rg.getCheckedRadioButtonId());
                cctimes = Integer.parseInt(rb.getText().toString().substring(0, 1));
            }
        });


        cost = Integer.parseInt(aw_cost.getText().toString());

        aw_staff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                staff = aw_staff.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aw_pay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pay = aw_pay.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aw_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avfUserAsynTask auat = new avfUserAsynTask();
                auat.execute(IP);
            }
        });

        aw_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("外观洗车 消费")
                .setView(v);

        return builder.create();
    }

    public class avfUserAsynTask extends AsyncTask<String, Void, String>{

/*        String nameSpace = "http://192.168.0.100";
        String url = "http://192.168.0.100/server.php";
        String methodName = "plate_CCRecordAdd";
        String soapAction = "http://192.168.0.100/server.php/plate_CCRecordAdd";*/

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(stCode == 0){
                Toast.makeText(getActivity().getApplicationContext(), "操作成功", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), "操作失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://"  + params[0] + "/server.php";
            String methodName = "plate_CCRecordAdd";
            String soapAction = "http://"  + params[0] + "/server.php/plate_CCRecordAdd";

            consumeType = getConsumeType(voucher, rs);
            ccVoucherId = getVoucherId(voucher, rs);

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);     //String, from Bundle
            request.addProperty("plate", plate);     //String, from Bundle
            request.addProperty("ccType", 0);    // int
            request.addProperty("ccTimes", cctimes);   // int
            request.addProperty("consumeType", consumeType);      // int, from feeType
            request.addProperty("ccVoucherId", ccVoucherId);       // int
            request.addProperty("cost", cost);      // int
            request.addProperty("pay", pay);       // int
            request.addProperty("staff", staff);     // String
            request.addProperty("memo", "测试用例");      // String

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
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
            Object result;
            try {
                result = envelope.getResponse();
                stCode = Integer.parseInt(result.toString());

            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

    public int getVoucherId(String voucher, Bundle input){
        int id = 0;
        for(int i = 0; i < input.size(); i++){
            Bundle b = input.getBundle("" + i);
            if(b.getString("cardcode").equals(voucher)){
                id = Integer.parseInt(b.getString("id"));
                break;
            }
        }
        return id;
    }

    public String getSaleTime(String voucher, Bundle input){
        String saletime = null;
        for(int i = 0; i < input.size(); i++){
            Bundle b = input.getBundle("" + i);
            if(b.getString("cardcode").equals(voucher)){
                saletime = b.getString("saletime");
                break;
            }
        }

        return saletime;
    }

    public int getConsumeType(String voucher, Bundle input){
        int consumeType = 0;
        for(int i = 0 ; i < input.size(); i++){
            Bundle b = input.getBundle("" + i);
            if(b.getString("cardcode").equals(voucher)){
                consumeType = Integer.parseInt(b.getString("feeType"));
                break;
            }
        }

        return consumeType;
    }
}

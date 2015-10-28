package com.beswell.car;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
public class CashWashingFragment extends DialogFragment{

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "cash_washing";
    String soapAction = "http://192.168.0.100/server.php/cash_washing";*/

    String IP = "192.168.0.100";

    EditText plate;
    Spinner cwType;
    EditText cwChargemoney;
    Spinner cwChargetype;
    Spinner cwStaff;
    EditText cwMemo;

    Button cwSubmit;
    Button cwBack;

    String hash;
    int stCode;

    String plate_str;
    String cwChargemoney_str;
    String cwMemo_str;
    int cwType_pos;
    int cwChargetype_pos;
    int cwStaff_pos;

    public static CashWashingFragment newInstance(String input){
        CashWashingFragment cwf = new CashWashingFragment();
        Bundle b = new Bundle();
        b.putString("hash", input);
        cwf.setArguments(b);

        return cwf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_cash_washing, null);

        plate = (EditText)v.findViewById(R.id.plate);
        cwType = (Spinner)v.findViewById(R.id.cw_type);
        cwChargemoney = (EditText)v.findViewById(R.id.cw_chargemoney);
        cwChargetype = (Spinner)v.findViewById(R.id.cw_chargetype);
        cwStaff = (Spinner)v.findViewById(R.id.cw_staff);
        cwMemo = (EditText)v.findViewById(R.id.cw_memo);

        cwSubmit = (Button)v.findViewById(R.id.cw_submit);
        cwBack = (Button)v.findViewById(R.id.cw_back);

        cwSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cwType_pos = cwType.getSelectedItemPosition();
                cwChargemoney_str = cwChargemoney.getText().toString();
                cwChargetype_pos = cwChargetype.getSelectedItemPosition();
                cwStaff_pos = cwStaff.getSelectedItemPosition();
                cwMemo_str = cwMemo.getText().toString();

                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);
            }
        });

        cwBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("现金洗车")
                .setView(v);

        return builder.create();
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        Object result = null;
        int stCode;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(stCode == 0){
                getDialog().dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "操作成功", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(), "操作失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "cash_washing";
            String soapAction = "http://" + params[0] + "/server.php/cash_washing";

            hash = getArguments().getString("hash");

            plate_str = plate.getText().toString();
            Log.d("Plate Debug", plate_str.toUpperCase());

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate_str.toUpperCase());
            request.addProperty("ccType", cwType_pos);
            request.addProperty("staff", cwStaff_pos);
            request.addProperty("cost", cwChargemoney_str);
            request.addProperty("pay", cwChargetype_pos);
            request.addProperty("memo", cwMemo_str);

            request.addProperty("stCode", stCode);

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
//            Object result;
            try {
                result = envelope.getResponse();
                Log.d("现金洗车Debug:", result.toString());
                stCode = Integer.parseInt(result.toString());
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}

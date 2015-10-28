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
public class FreeWashingFragment extends DialogFragment {

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "free_washing";
    String soapAction = "http://192.168.0.100/server.php/free_washing";*/

    String IP = "192.168.0.100";

    EditText fwPlate;
    Spinner fwCCType;
    EditText fwChargemoney;
    Spinner fwChargetype;
    Spinner fwStaff;
    EditText fwMemo;

    Button fwSubmit;
    Button fwBack;

    String hash;
    int stCode;

    String fwPlate_str;
    int fwCCType_pos;
    String fwChargemoney_str;
    int fwChargetype_pos;
    String fwStaff_str;
    String fwMemo_str;

    Object result;

    public static FreeWashingFragment newInstance(String input){
        FreeWashingFragment fwf = new FreeWashingFragment();
        Bundle b = new Bundle();
        b.putString("hash", input);
        fwf.setArguments(b);

        return fwf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_free_washing, null);

        fwPlate = (EditText)v.findViewById(R.id.fw_plate);
        fwCCType = (Spinner)v.findViewById(R.id.fw_cctype);
        fwChargemoney = (EditText)v.findViewById(R.id.fw_chargemoney);
        fwChargetype = (Spinner)v.findViewById(R.id.fw_chargetype);
        fwStaff = (Spinner)v.findViewById(R.id.fw_staff);
        fwMemo = (EditText)v.findViewById(R.id.fw_memo);

        fwSubmit = (Button)v.findViewById(R.id.fw_submit);
        fwBack = (Button)v.findViewById(R.id.fw_back);

        fwSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fwPlate_str = fwPlate.getText().toString();
                fwCCType_pos = fwCCType.getSelectedItemPosition();
                fwChargemoney_str = fwChargemoney.getText().toString();
                fwChargetype_pos = fwChargetype.getSelectedItemPosition();
                fwStaff_str = fwStaff.getSelectedItem().toString();
                fwMemo_str = fwMemo.getText().toString();

                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);
            }
        });

        fwBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("免费洗车")
                .setView(v);

        return builder.create();
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(result.toString().equals("Success")){
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
            String methodName = "free_washing";
            String soapAction = "http://" + params[0] + "/server.php/free_washing";

            hash = getArguments().getString("hash");

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", fwPlate_str);
            request.addProperty("ccType", fwCCType_pos);
            request.addProperty("staff", fwStaff_str);
            request.addProperty("cost", fwChargemoney_str);
            request.addProperty("pay", fwChargetype_pos);
            request.addProperty("memo", fwMemo_str);

            request.addProperty("stCode", stCode);

            Log.d("FW Debug", fwPlate_str);

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

            try {
                result = envelope.getResponse();

            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}

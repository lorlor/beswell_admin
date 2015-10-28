package com.beswell.plateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class ResetPasswdFragment extends DialogFragment {

    String IP = "192.168.0.100";

    Button cpw;
    EditText phone;
    Button btn_y;
    Button btn_n;

    String hash;
    String plate;
    String phone_str;

    public static ResetPasswdFragment newInstance(String h_str, String p_str){
        ResetPasswdFragment rpf = new ResetPasswdFragment();
        Bundle b = new Bundle();
        b.putString("hash", h_str);
        b.putString("plate", p_str);
        rpf.setArguments(b);

        return rpf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.sf_reset_passwd, null);

        cpw = (Button)v.findViewById(R.id.plate_cpw);
        phone = (EditText)v.findViewById(R.id.plate_phone);
        btn_y = (Button)v.findViewById(R.id.plate_chpw_yes);
        btn_n = (Button)v.findViewById(R.id.plate_chpw_back);

        cpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PwdAsyncTask pat = new PwdAsyncTask();
                pat.execute(IP);
            }
        });

        btn_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(), "手机号未修改", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
                else{
                    phone_str = phone.getText().toString();
                    TelAsyncTask tat = new TelAsyncTask();
                    tat.execute(IP);
                }
            }
        });

        btn_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("重置密码")
                .setView(v);

        return builder.create();
    }

    class PwdAsyncTask extends AsyncTask<String, Void, String>{
        int stCode;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(stCode == 0){
                Toast.makeText(getActivity().getBaseContext(), "已清空登录密码", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity().getBaseContext(), "清空失败，请重新操作", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            hash = getArguments().getString("hash");
            plate = getArguments().getString("plate");

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_reset_pwd";
            String soapAction = "http://" + params[0] + "/server.php/plate_reset_pwd";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", "25aad65071035d26a7a8ef53fed23c7b");
            request.addProperty("plate", "鲁A2Z278");

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

            try {
                Object result = envelope.getResponse();
                stCode = Integer.parseInt(request.toString());
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            }

            return null;
        }
    }

    class TelAsyncTask extends AsyncTask<String, Void, String>{
        int stCode;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(stCode == 0){
                Toast.makeText(getActivity().getBaseContext(), "操作成功", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
            else{
                Toast.makeText(getActivity().getBaseContext(), "操作失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            hash = getArguments().getString("hash");
            plate = getArguments().getString("plate");

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "plate_reset_tel";
            String soapAction = "http://" + params[0] + "/server.php/plate_reset_tel";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("plate", plate);
            request.addProperty("phone", phone_str);

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

            try {
                Object result = envelope.getResponse();
                stCode = Integer.parseInt(result.toString());
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            }

            return null;
        }
    }
}

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
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Created by beswell10 on 2015/8/10.
 */
public class ChpwFragment extends Fragment {

    String IP = "192.168.0.100";

    String hash;
    int stCode;

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "change_passwd";
    String soapAction = "http://192.168.0.100/server.php/change_passwd";*/

    EditText oldPwd;
    EditText newPwd;
    EditText confirmPwd;
    Button pwdYes;
    Button pwdNo;

    String oldPwd_str;
    String newPwd_str;
    String confirmPwd_str;

    public static ChpwFragment newInstance(String input, String para){
        ChpwFragment cf = new ChpwFragment();
        Bundle b = new Bundle();
        b.putString("hash", input);
        b.putString("userCode", para);
        cf.setArguments(b);

        return cf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chpw, container, false);

        oldPwd = (EditText)v.findViewById(R.id.old_passwd);
        newPwd = (EditText)v.findViewById(R.id.new_passwd);
        confirmPwd = (EditText)v.findViewById(R.id.confirm_passwd);

        pwdYes = (Button)v.findViewById(R.id.pwd_yes);
        pwdNo = (Button)v.findViewById(R.id.pwd_no);

        pwdYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPwd_str = oldPwd.getText().toString();
                newPwd_str = newPwd.getText().toString();
                confirmPwd_str = confirmPwd.getText().toString();

                if(oldPwd_str.isEmpty() || newPwd_str.isEmpty() || confirmPwd_str.isEmpty()){
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                        .setMessage("请输入密码")
                                                        .create();
                    dialog.show();
                }
                else if(newPwd_str.equals(confirmPwd_str)){
                    UserAsyncTask uat = new UserAsyncTask();
                    uat.execute(IP);
                }
                else{
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("两次输入的新密码不一致，请检查")
                            .create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }
        });

        pwdNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPwd.setText("");
                newPwd.setText("");
                confirmPwd.setText("");
            }
        });

        return v;
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(stCode == 0){
                Toast.makeText(getActivity().getApplicationContext(), "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            else{
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                    .setMessage("操作失败，请重试")
                                                    .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://"  + params[0] + "/server.php";
            String methodName = "change_passwd";
            String soapAction = "http://"  + params[0] + "/server.php/change_passwd";

            hash = getArguments().getString("hash");
            String usercode = getArguments().getString("userCode");

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("userCode", usercode);
            try {
                request.addProperty("oldPwd", MD5(oldPwd_str));
                request.addProperty("newPwd", MD5(newPwd_str));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Debug", request.toString());

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
                Log.d("Chpw Debug", "" + stCode);
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    public static String MD5(String input) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = input.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}

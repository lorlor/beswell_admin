package com.beswell.car;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beswell.common.ErrorFragment;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends ActionBarActivity {

    String IP = "192.168.0.100";

    EditText name;
    EditText pwd;
    Button login;
    TextView tv;

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "login";
    String soapAction = "http://192.168.0.100/server.php/login";*/

    String retValue;
    int stCode;
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.login);
        tv = (TextView)findViewById(R.id.res);
        name = (EditText)findViewById(R.id.name);
        pwd = (EditText)findViewById(R.id.passwd);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);

                tv.setText(hash);

          /*      if(stCode == 0 && name.getText().toString() != "" && pwd.getText().toString() != "" && hash != null){
                    Intent intent = new Intent(LoginActivity.this, IndexScreen.class);
                    intent.putExtra("hash", hash);
                    startActivity(intent);
                }
                else if(stCode == -1013){
                    ErrorFragment ef = ErrorFragment.newInstance(R.string.error_1013);
                    ef.show(getSupportFragmentManager(), "Invalid Login");
                }
                else{
                    ErrorFragment ef = ErrorFragment.newInstance(R.string.error_0);
                    ef.show(getSupportFragmentManager(), "Invalid Login");
                }*/
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
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

    public class UserAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Return Value => ", retValue);

            if(retValue.length() == 0){
                Toast.makeText(getApplicationContext(), "请重试", Toast.LENGTH_SHORT).show();
            }
            else {
                String[] para = retValue.split("\\.");
                Log.d("Length of para" ,para.length + "");
                stCode = Integer.parseInt(para[0]);
                if (stCode == 0 && name.getText().toString() != "" && pwd.getText().toString() != "") {
                    hash = para[1];
                    Intent intent = new Intent(LoginActivity.this, IndexScreen.class);
                    intent.putExtra("hash", hash);
                    intent.putExtra("userCode", name.getText().toString());
                    startActivity(intent);
                    finish();
                } else if (stCode == -1013) {
                    ErrorFragment ef = ErrorFragment.newInstance(R.string.error_1013);
                    ef.show(getSupportFragmentManager(), "Invalid Login");
                } else {
                    ErrorFragment ef = ErrorFragment.newInstance(R.string.error_0);
                    ef.show(getSupportFragmentManager(), "Invalid Login");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String user_name = name.getText().toString();
            String user_pwd = pwd.getText().toString();

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "login";
            String soapAction = "http://" + params[0] + "/server.php/login";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("userCode", user_name);
            try {
                request.addProperty("passwd", MD5(user_pwd));
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.addProperty("ip", "192.168.0.54");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);

            HttpTransportSE se = new HttpTransportSE(url);
            try{
                se.call(soapAction, envelope);
            }
            catch (HttpResponseException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }

  /*          Object ret = null;
            try{
                ret = envelope.getResponse();
                retValue = ret.toString();
*//*                Message msg = handler.obtainMessage();
                msg.obj = retValue;
                handler.sendMessage(msg);*//*
                Log.d("Login Debug", retValue);
            }
            catch (SoapFault e){
                e.toString();
            }*/
            SoapObject ret = (SoapObject) envelope.bodyIn;
            retValue = ret.getProperty("return").toString();

            return null;
        }

/*        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String[] out = msg.obj.toString().split("\\.");
                stCode = Integer.parseInt(out[0]);
                if(stCode == 0) {
                    hash = out[1];
                }

            };
        };*/
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

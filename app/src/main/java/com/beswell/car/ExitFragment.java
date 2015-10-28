package com.beswell.car;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.LogRecord;

/**
 * Created by beswell10 on 2015/8/10.
 */
public class ExitFragment extends Fragment {

    String IP = "192.168.0.100";

    Button btn_yes;
    TextView tv;

    String hash;
    int stCode;

/*
    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "logout";
    String soapAction = "http://192.168.0.100/server.php/logout";
*/

    public static ExitFragment newInstance(String input){
        ExitFragment ef = new ExitFragment();
        Bundle b = new Bundle();
        b.putString("hash", input);

        ef.setArguments(b);

        return ef;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exit, container, false);

        btn_yes = (Button)v.findViewById(R.id.yes_button);
        tv = (TextView)v.findViewById(R.id.exit_tv);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);

            }
        });

        return v;
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(stCode == 0){
                Toast.makeText(getActivity().getApplicationContext(), "成功推出，请重新登录", Toast.LENGTH_SHORT);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "logout";
            String soapAction = "http://" + params[0] + "/server.php/logout";

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", getArguments().getString(hash));

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
            Object result  = null;
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
}

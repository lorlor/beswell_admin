package com.beswell.car;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.security.DigestException;

/**
 * Created by beswell10 on 2015/8/13.
 */
public class CreateCardFragment extends DialogFragment {

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "create_card";
    String soapAction = "http://192.168.0.100/server.php/create_card";*/
    String IP = "192.168.0.100";

    EditText cardCode;
    EditText chargeMoney;
    Spinner ccType;
    Spinner chargeType;
    Spinner customerType;
    EditText mainPlate;
    EditText custName;
    EditText custTel;
    EditText createCMemo;

    String cardCode_str;
    String chargeMoney_str;
    String ccType_str;
    String chargeType_str;
    String customerType_str;
    String mainPlate_str;
    String custName_str;
    String custTel_str;
    String memo;

    Button createCSubmit;
    Button createCBack;

    public static CreateCardFragment newInstance(String hash, String usercode){
        CreateCardFragment ccf = new CreateCardFragment();
        Bundle b = new Bundle();
        b.putString("hash", hash);
        b.putString("usercode", usercode);
        ccf.setArguments(b);

        return ccf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_create_card, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setTitle("新卡开卡");

        cardCode = (EditText)v.findViewById(R.id.card_code);
        chargeMoney = (EditText)v.findViewById(R.id.charge_money);
        ccType = (Spinner)v.findViewById(R.id.cc_type);
        ccType.setPrompt("洗车类型");
        chargeType = (Spinner)v.findViewById(R.id.charge_type);
        chargeType.setPrompt("付款方式");
        customerType = (Spinner)v.findViewById(R.id.customer_type);
        customerType.setPrompt("客户类型");
        mainPlate = (EditText)v.findViewById(R.id.main_plate);
        custName = (EditText)v.findViewById(R.id.cust_name);
        custTel = (EditText)v.findViewById(R.id.cust_tel);

        createCMemo = (EditText)v.findViewById(R.id.create_c_memo);
/*        plateNo1 = (EditText)v.findViewById(R.id.plate_no1);
        plateNo2 = (EditText)v.findViewById(R.id.plate_no2);
        plateNo3 = (EditText)v.findViewById(R.id.plate_no3);*/

        createCSubmit = (Button)v.findViewById(R.id.create_c_submit);
        createCBack = (Button)v.findViewById(R.id.create_c_back);

        createCSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardCode_str = cardCode.getText().toString();
                chargeMoney_str = chargeMoney.getText().toString();
                ccType_str = ccType.getSelectedItem().toString();
                chargeType_str = chargeType.getSelectedItem().toString();
                customerType_str = customerType.getSelectedItem().toString();
                mainPlate_str = mainPlate.getText().toString().toUpperCase();
                custName_str = custName.getText().toString();
                custTel_str = custTel.getText().toString();
                memo = createCMemo.getText().toString();

                UserAsyncTask uat = new UserAsyncTask();
                uat.execute(IP);

            }
        });

        createCBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        int stCode;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(stCode == 0){
                Toast.makeText(getActivity().getBaseContext(), "开卡成功", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
            else{
                Toast.makeText(getActivity().getBaseContext(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "create_card";
            String soapAction = "http://" + params[0] + "/server.php/create_card";

            String hash = getArguments().getString("hash");
            String usercode = getArguments().getString("usercode");

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("cardCode", cardCode_str);
            request.addProperty("ccType", getCCType(ccType_str));          
            request.addProperty("userName", custName_str);
            request.addProperty("tel", custTel_str);
            request.addProperty("plate", mainPlate_str);
            request.addProperty("opid", usercode);
            request.addProperty("serviceType", getServiceType(customerType_str));
            request.addProperty("chargeType", getChargeType(chargeType_str));
            request.addProperty("chargeMoney", getChargeMoney(chargeMoney_str));
            request.addProperty("memo", memo);

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
                Log.d("Card Debug", stCode + "");
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    public int getCCType(String type){
        switch (type){
            case "标准洗车":
                return 1;
            case "外观洗车":
                return 0;
            default:
                return -1;
        }
    }

    public int getServiceType(String type){
        switch (type){
            case "普通客户":
                return 0;
            case "重要客户":
                return 1;
            case "VIP客户":
                return 2;
            default:
                return -1;
        }
    }

    public int getChargeType(String type){
        switch (type){
            case "现金":
                return 0;
            case "POS":
                return 1;
            case "拉卡拉":
                return 2;
            case "支付宝":
                return 3;
            case "券":
                return 4;
            case "记账":
                return 5;
            case "免单":
                return 6;
            case "支票":
                return 7;
            case "微信":
                return 8;
            case "美团":
                return 9;
            default:
                return -1;
        }
    }

    public int getChargeMoney(String money){
        float temp = Float.parseFloat(money);
        int i_temp = (int)temp;

        return i_temp;
    }
}

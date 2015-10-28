package com.beswell.plateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
public class CardServiceFragment extends DialogFragment {

    String IP = "192.168.0.100";

    EditText cardCode;
    EditText chargeMoney;
    Spinner ccType;
    Spinner chargeType;
    Spinner customerType;

    EditText mainPlate;
    EditText custName;
    EditText custTel;
    EditText memo;

    String hash;
    String plate;
    String userName;
    String userTel;

/*    EditText plate1;
    EditText plate2;
    EditText plate3;*/

    Button btn_s;
    Button btn_b;

    public static CardServiceFragment newInstance(String hash, String plate, String name, String tel){
        CardServiceFragment csf = new CardServiceFragment();
        Bundle b = new Bundle();
        b.putString("hash", hash);
        b.putString("plate", plate);
        b.putString("name", name);
        b.putString("tel", tel);
        csf.setArguments(b);

        return csf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hash = getArguments().getString("hash");
        plate = getArguments().getString("plate");
        userName = getArguments().getString("name");
        userTel =getArguments().getString("tel");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.sf_card_service, null);

        cardCode = (EditText)v.findViewById(R.id.plate_card_code);
        chargeMoney = (EditText)v.findViewById(R.id.plate_charge_money);
        ccType = (Spinner)v.findViewById(R.id.plate_cc_type);
        chargeType = (Spinner)v.findViewById(R.id.plate_charge_type);
        customerType = (Spinner)v.findViewById(R.id.plate_customer_type);

        mainPlate = (EditText)v.findViewById(R.id.plate_main_plate);
        custName = (EditText)v.findViewById(R.id.plate_cust_name);
        custTel = (EditText)v.findViewById(R.id.plate_cust_tel);
        memo = (EditText)v.findViewById(R.id.plate_create_c_memo);

        mainPlate.setText(plate);
        custName.setText(userName);
        custTel.setText(userTel);

/*        plate1 = (EditText)v.findViewById(R.id.sf_plate_no1);
        plate2 = (EditText)v.findViewById(R.id.sf_plate_no2);
        plate3 = (EditText)v.findViewById(R.id.sf_plate_no3);*/

        btn_s = (Button)v.findViewById(R.id.sf_create_c_submit);
        btn_b = (Button)v.findViewById(R.id.sf_create_c_back);

        btn_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlateCardSale pcs = new PlateCardSale();
                pcs.execute(IP);
            }
        });

        btn_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("开卡服务")
                .setView(v);

        return builder.create();
    }

    public class PlateCardSale extends AsyncTask<String, Void, String>{
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

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("cardCode", cardCode.getText().toString().toUpperCase());
            request.addProperty("ccType", getCCType(ccType.getSelectedItem().toString()));
            request.addProperty("userName", userName);
            request.addProperty("tel", userTel);
            request.addProperty("plate", plate);
            request.addProperty("opid", "test");
            request.addProperty("serviceType", getServiceType(customerType.getSelectedItem().toString()));
            Log.d("ServiceType",  getServiceType(customerType.getSelectedItem().toString()) + "");
            request.addProperty("chargeType", getChargeType(chargeType.getSelectedItem().toString()));
            Log.d("ChargeType", getChargeType(chargeType.getSelectedItem().toString()) + "");
            request.addProperty("chargeMoney", getChargeMoney(chargeMoney.getText().toString()));
            Log.d("ChargeMoney", getChargeMoney(chargeMoney.getText().toString()) + "");
            request.addProperty("memo", memo.getText().toString());

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

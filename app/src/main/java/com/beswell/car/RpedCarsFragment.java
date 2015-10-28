package com.beswell.car;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.beswell.common.ShowRedRecords;
import com.beswell.common.ShowWRRecords;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by beswell10 on 2015/8/14.
 */
public class RpedCarsFragment extends DialogFragment {

    String IP = "192.168.0.100";

    Bundle records;

/*    String nameSpace = "http://192.168.0.100";
    String url = "http://192.168.0.100/server.php";
    String methodName = "rped_record";
    String soapAction = "http://192.168.0.100/server.php/rped_record";*/

    DatePicker st;
    DatePicker et;

    Button query;
    Button back;

    String st_str;
    String et_str;

    String out;

    int stCode;
    int rsCount;

    public static RpedCarsFragment newInstance(String input){
        RpedCarsFragment rcf = new RpedCarsFragment();
        Bundle b = new Bundle();
        b.putString("hash", input);
        rcf.setArguments(b);

        return rcf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_rped_car, null);

        st = (DatePicker)v.findViewById(R.id.red_startTime);
        et = (DatePicker)v.findViewById(R.id.red_endTime);


        query = (Button)v.findViewById(R.id.red_query);
        back = (Button)v.findViewById(R.id.red_back);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int st_y = st.getYear();
                int st_m = st.getMonth() + 1;
                int st_d = st.getDayOfMonth();
                st_str = st_y + "-" + st_m + "-" + st_d;

                int et_y = et.getYear();
                int et_m = st.getMonth() + 1;
                int et_d = et.getDayOfMonth();
                et_str = et_y + "-" + et_m + "-" + et_d;
                boolean flag = false;
                if(st_y > et_y){
//                    System.out.println("Invalid date");
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage("起始日期应早于或等于截止日期").create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
                else if(st_y == et_y){
                    if(st_m > et_m){
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setMessage("起始日期应早于或等于截止日期").create();
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    }
                    else if(st_m == et_m){
                        if(st_d <= et_d){
                            st_str = st_y + "-" + st_m + "-" + st_d;
                            et_str = et_y + "-" + et_m + "-" + et_d;
                            flag =true;
                        }
                        else{
                            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage("起始日期应早于或等于截止日期").create();
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    }
                    else{
                        st_str = st_y + "-" + st_m + "-" + st_d;
                        et_str = et_y + "-" + et_m + "-" + et_d;
                        flag =true;
                    }
                }
                else{
                    st_str = st_y + "-" + st_m + "-" + st_d;
                    et_str = et_y + "-" + et_m + "-" + et_d;
                    flag =true;
                }
                if(flag) {
                    UserAsyncTask uat = new UserAsyncTask();
                    uat.execute(IP);
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("完工车辆查询")
                .setView(v);

        return builder.create();
    }

    public class UserAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(getActivity(), ShowRedRecords.class);
            intent.putExtra("rd_Records", records);
            intent.putExtra("Out", out);
            startActivity(intent);
            getDialog().dismiss();
        }

        @Override
        protected String doInBackground(String... params) {

            String nameSpace = "http://" + params[0];
            String url = "http://" + params[0] + "/server.php";
            String methodName = "rped_record";
            String soapAction = "http://" + params[0] + "/server.php/rped_record";

            records = new Bundle();
            hash = getArguments().getString("hash");

            SoapObject request = new SoapObject(nameSpace, methodName);
            request.addProperty("hashStr", hash);
            request.addProperty("startTime", st_str);
            request.addProperty("endTime", et_str);
            request.addProperty("stCode", stCode);
            request.addProperty("rsCount", rsCount);

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

            Log.d("Rped Debug", hash + "\n" + st_str + "\n" + et_str);
            SoapObject result = (SoapObject) envelope.bodyIn;

            //temp为所有记录的集合，其中每一项都是一条记录
            Vector<SoapObject> temp = (Vector<SoapObject>)result.getProperty("return");
            StringBuilder sb = new StringBuilder();

/*            out = temp.elementAt(2).toString();*/

     /*       t = t.replace("}", "");
            t = t.replace(";", " ");
            t = t.replace("=", " ");
            String[] rs = t.split(" ");*/

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


/*                        switch (rs.length){
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

                        }*/

                        if(rs[1].equals("plate") || rs[1].equals("CarMan")
                                || rs[1].equals("Tel") || rs[1].equals("Mobile")
                                || rs[1].equals("RequestDate") || rs[1].equals("CompletionDate")
                                || rs[1].equals("SettlementDate") || rs[1].equals("ShouldMoney")){
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
                records.putBundle("" + i, b);

            }
            out = sb.toString();

            return null;
        }
    }

    String hash;
}

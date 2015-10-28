package com.beswell.plateService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Vector;

public class PlateShowRmRecords extends ActionBarActivity {

    String IP = "192.168.0.100";

    Spinner sp;
    Button next;
    Button prev;
    int pos;

    TextView prm_id;
    TextView prm_carMan;
    TextView prm_brandName;
    TextView prm_carMileage;
    TextView prm_requestDate;
    TextView prm_serviceCost;
    TextView prm_settlementName;
    TextView prm_mainRepaire;
    Button prm_detailedInfo;

    ArrayAdapter<String> adapter;
    Intent intent;

    Bundle records;
    String serviceNo;

    String hash;
    String plate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plate_show_rm_records);

        intent = getIntent();
        records = intent.getBundleExtra("Rset");
        hash = intent.getStringExtra("hash");
        plate = intent.getStringExtra("plate");

        prm_id = (TextView)findViewById(R.id.prm_id);
        prm_carMan = (TextView)findViewById(R.id.prm_carman);
        prm_brandName = (TextView)findViewById(R.id.prm_brandName);
        prm_carMileage = (TextView)findViewById(R.id.prm_CarMileage);
        prm_requestDate = (TextView)findViewById(R.id.prm_RequestDate);
        prm_serviceCost = (TextView)findViewById(R.id.prm_ServiceCost);
        prm_settlementName = (TextView)findViewById(R.id.prm_SettlementName);
        prm_mainRepaire = (TextView)findViewById(R.id.prm_MainRepaire);
        prm_detailedInfo = (Button)findViewById(R.id.prm_detailedInfo);


        sp = (Spinner)findViewById(R.id.prm_item);
        next = (Button)findViewById(R.id.prm_nextPage);
        prev = (Button)findViewById(R.id.prm_prevPage);

        if(records.size() != 0) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            for (int i = 0; i < records.size(); i++) {
                int temp = i + 1;
                adapter.add("第" + temp + "页");
            }

            sp.setAdapter(adapter);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    setContent(records, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == 0) {
                        Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos--;
                        setContent(records, pos);
                        sp.setSelection(pos);
                    }
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pos == records.size() - 1) {
                        Toast.makeText(getApplicationContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                    } else {
                        pos++;
                        setContent(records, pos);
                        sp.setSelection(pos);
                    }
                }
            });

            prm_detailedInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailAsyncTask iat = new DetailAsyncTask();
                    iat.execute(IP);
                }
            });
        }
        else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                                                .setMessage("相关记录为空，没有记录")
                                                .create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plate_show_rm_records, menu);
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

    public void setContent(Bundle set, int serialNo){
        Bundle content = set.getBundle("" + serialNo);
        prm_id.setText("序号：" + (serialNo+1));
        prm_carMan.setText("送修人：" + content.getString("CarMan"));
        prm_brandName.setText("车型：" + content.getString("seriesName"));

        float cm_temp = Float.parseFloat(content.getString("CarMileage"));
        int icm_temp = (int)cm_temp;
        prm_carMileage.setText("行驶里程：" + icm_temp);

        prm_requestDate.setText("进场日期：" + content.getString("RequestDate"));

        float temp = Float.parseFloat(content.getString("ServiceCost"));
        int i_temp = (int)temp;
        prm_serviceCost.setText("金额：" + i_temp);

        prm_settlementName.setText("结算方式：" + content.getString("SettlementName"));
        if(content.getString("MainRepaire").equals("null")) {
            prm_mainRepaire.setText("主修人：" + "-");
        }
        else{
            prm_mainRepaire.setText("主修人：" + content.getString("MainRepaire"));
        }
        serviceNo = content.getString("ServiceNo");
    }

    class DetailAsyncTask extends AsyncTask<String, Void, String>{

        String retValue;
        Bundle overview;
        Bundle item;
        Bundle part;
        Bundle total;

        String item_t;
        String part_t;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent1 = new Intent(PlateShowRmRecords.this, ShowDetailRM.class);
            Log.d("Overview Debug", overview.toString());
            Log.d("Total Debug", total.toString());
            Log.d("Item Debug", item.toString());
            Log.d("Part Debug", part.toString());
            intent1.putExtra("Overview", overview);
            intent1.putExtra("Total", total);
            intent1.putExtra("Item", item);
            intent1.putExtra("Part", part);
            startActivity(intent1);
        }

        @Override
        protected String doInBackground(String... params) {
            Bundle selectedBundle = records.getBundle("" + pos);
            String serviceNo = selectedBundle.getString("ServiceNo");

            overview = new Bundle();
            item = new Bundle();
            part= new Bundle();
            total = new Bundle();

            overview.putString("ServiceNo", selectedBundle.getString("ServiceNo"));
            overview.putString("SettlementDate", selectedBundle.getString("SettlementDate"));
            overview.putString("SettlementOperator", selectedBundle.getString("SettlementOperator"));
            overview.putString("CarMan", selectedBundle.getString("CarMan"));
            overview.putString("CarCode", selectedBundle.getString("CarCode"));
            overview.putString("CarManMobile", selectedBundle.getString("CarManMobile"));
            overview.putString("RequestDate", selectedBundle.getString("RequestDate"));
            overview.putString("seriesName", selectedBundle.getString("seriesName"));
            overview.putString("CarMileage", selectedBundle.getString("CarMileage"));

            total.putString("AllMoney", selectedBundle.getString("AllMoney"));
            total.putString("AcceptMoney", selectedBundle.getString("AcceptMoney"));
            total.putString("CheapMoney", selectedBundle.getString("CheapMoney"));
            total.putString("LeaveMoney", selectedBundle.getString("LeaveMoney"));
            total.putString("ShouldMoney", selectedBundle.getString("ShouldMoney"));
            total.putString("SettlementName", selectedBundle.getString("SettlementName"));

            String i_nameSpace = "http://" + params[0];
            String i_url = "http://" + params[0] + "/server.php";
            String i_methodName = "plate_itemsRm";
            String i_soapAction = "http://" + params[0] + "/server.php/plate_itemsRm";

            SoapObject i_request = new SoapObject(i_nameSpace, i_methodName);
            i_request.addProperty("hashStr", hash);            // Replaced by hash String
            i_request.addProperty("serviceNo", serviceNo);         // Replaced by plate String

            SoapSerializationEnvelope i_envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            i_envelope.setOutputSoapObject(i_request);


            HttpTransportSE i_httpTransport = new HttpTransportSE(i_url);

            i_httpTransport.debug = true;
            try {
                i_httpTransport.call(i_soapAction, i_envelope);
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

            SoapObject result = (SoapObject) i_envelope.bodyIn;
            Vector<SoapObject> temp = (Vector<SoapObject>) result.getProperty("return");
            item_t = temp.size() + "\n" + temp.toString();
            Log.d("Item Debug", item_t);
            StringBuilder sb = new StringBuilder();

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


                        if (rs[1].equals("ItemsName") || rs[1].equals("ManHour") || rs[1].equals("Price")) {
                            switch (rs.length) {
                                case 4:
                                    t = rs[1] + " >> " + " - " + "\n";
                                    b.putString(rs[1], "-");
                                    break;
                                case 5:
                                    t = rs[1] + " >> " + rs[4] + "\n";
                                    b.putString(rs[1], rs[4]);
                                    break;
                                case 6:
                                    t = rs[1] + " >> " + rs[4] + " " + rs[5].substring(0, 5) + "\n";
                                    b.putString(rs[1], rs[4] + " " + rs[5].substring(0, 5));
                                    break;

                            }

                            sb.append(t + rs.length + "\n");
                        } else {
                            continue;
                        }

                    }

                }
                item.putBundle("" + i, b);

            }

            String p_nameSpace = "http://" + params[0];
            String p_url = "http://" + params[0] +"/server.php";
            String p_methodName = "plate_partRm";
            String p_soapAction = "http://" + params[0] +"/server.php/plate_partRm";

            SoapObject p_request = new SoapObject(p_nameSpace, p_methodName);
            p_request.addProperty("hashStr", hash);            // Replaced by hash String
            p_request.addProperty("serviceNo", serviceNo);         // Replaced by plate String

            SoapSerializationEnvelope p_envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            p_envelope.setOutputSoapObject(p_request);


            HttpTransportSE p_httpTransport = new HttpTransportSE(p_url);

            p_httpTransport.debug = true;
            try {
                i_httpTransport.call(p_soapAction, p_envelope);
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

            SoapObject result_oth = (SoapObject) p_envelope.bodyIn;
            Vector<SoapObject> temp_oth = (Vector<SoapObject>) result_oth.getProperty("return");
            part_t = temp_oth.size() + "\n" + temp_oth.toString();
            Log.d("Part Debug", part_t);
            for(int i = 0; i < temp_oth.size(); i++) {
                Bundle b = new Bundle();
                for(int j = 0; j < temp_oth.elementAt(i).getPropertyCount(); j++) {
                    if(j % 2 == 0) {
                        String t;

                        t = temp_oth.elementAt(i).getProperty(j).toString().replace("anyType{", "");
                        t = t.replace("}", "");
                        t = t.replace(";", " ");
                        t = t.replace("=", " ");
                        String[] rs = t.split(" ");


                        if (rs[1].equals("PartName") || rs[1].equals("Amount") || rs[1].equals("Price") || rs[1].equals("CostMoney")) {
                            switch (rs.length) {
                                case 4:
                                    t = rs[1] + " >> " + " - " + "\n";
                                    b.putString(rs[1], "-");
                                    break;
                                case 5:
                                    t = rs[1] + " >> " + rs[4] + "\n";
                                    b.putString(rs[1], rs[4]);
                                    break;
                                case 6:
                                    t = rs[1] + " >> " + rs[4] + rs[5] + "\n";
                                    b.putString(rs[1], rs[4] + rs[5]);
                                    break;

                            }

                        } else {
                            continue;
                        }

                    }

                }
                part.putBundle("" + i, b);

            }

            return null;
        }
    }
}

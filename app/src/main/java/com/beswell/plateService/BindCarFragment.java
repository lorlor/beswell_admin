package com.beswell.plateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.beswell.car.R;

/**
 * Created by beswell10 on 2015/8/14.
 */
public class BindCarFragment extends DialogFragment {

    Spinner voucher;
    EditText plate1;
    EditText plate2;
    EditText plate3;

    Button cp1;
    Button cp2;
    Button cp3;

    Button bc_y;
    Button bc_n;

    Bundle aw_voucher;
    Bundle sw_voucher;

    public static BindCarFragment newInstance(Bundle aw, Bundle sw){
        BindCarFragment bcf = new BindCarFragment();
        Bundle b = new Bundle();
        b.putBundle("Aw", aw);
        b.putBundle("Sw", sw);
        bcf.setArguments(b);

        return bcf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.sf_bind_car, null);

        voucher = (Spinner)v.findViewById(R.id.bc_voucher);
        plate1 = (EditText)v.findViewById(R.id.car_1);
        plate2 = (EditText)v.findViewById(R.id.car_2);
        plate3 = (EditText)v.findViewById(R.id.car_3);

        cp1 = (Button)v.findViewById(R.id.car1_clear);
        cp2 = (Button)v.findViewById(R.id.car2_clear);
        cp3 = (Button)v.findViewById(R.id.car3_clear);

        bc_y = (Button)v.findViewById(R.id.bc_btnYes);
        bc_n = (Button)v.findViewById(R.id.bc_btnNo);

        aw_voucher = getArguments().getBundle("Aw");
        sw_voucher = getArguments().getBundle("Sw");

        bc_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        builder.setTitle("绑定车辆")
                .setView(v);

        return builder.create();
    }
}

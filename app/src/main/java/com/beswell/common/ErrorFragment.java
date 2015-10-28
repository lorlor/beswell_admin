package com.beswell.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beswell.car.R;

/**
 * Created by beswell10 on 2015/8/17.
 */
public class ErrorFragment extends DialogFragment {

    public static ErrorFragment newInstance(int num){
        ErrorFragment ef = new ErrorFragment();
        Bundle b = new Bundle();

        b.putInt(mContent, num);
        ef.setArguments(b);

        return ef;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(this.getArguments().getInt(mContent));

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    static String mContent;
}

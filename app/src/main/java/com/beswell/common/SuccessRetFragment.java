package com.beswell.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by beswell10 on 2015/8/14.
 */
public class SuccessRetFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("修改成功").setMessage("操作成功");

        return super.onCreateDialog(savedInstanceState);
    }
}

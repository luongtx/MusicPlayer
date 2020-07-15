package com.example.mymusicapp.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentTimerPicker extends DialogFragment {

    Context context;

    public FragmentTimerPicker(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int hours = 0;
        int minutes = 0;
        TimePickerDialog.OnTimeSetListener timeSetListener = (TimePickerDialog.OnTimeSetListener) context;
        return new TimePickerDialog(context, timeSetListener, hours, minutes, true);
    }
}

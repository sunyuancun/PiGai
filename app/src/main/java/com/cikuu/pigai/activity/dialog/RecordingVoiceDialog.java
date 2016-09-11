package com.cikuu.pigai.activity.dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.cikuu.pigai.R;

public class RecordingVoiceDialog extends DialogFragment {

    public TextView mCountDownTextView;

    public RecordingVoiceDialog() {
    }

    public static RecordingVoiceDialog newInstance() {
        RecordingVoiceDialog f = new RecordingVoiceDialog();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_recording_voice, container);
        mCountDownTextView = (TextView) view.findViewById(R.id.counterDownTextView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
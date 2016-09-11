package com.cikuu.pigai.activity.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cikuu.pigai.R;

public class InputScoreDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    private Button btnOne;
    private Button btnTwo;
    private EditText mScoreEditText;
    public double mScore;

    public InputScoreDialog() {
        // Empty constructor required for DialogFragment
    }

    public static InputScoreDialog newInstance(int score) {
        InputScoreDialog f = new InputScoreDialog();

        // Supply score input as an argument.
        Bundle args = new Bundle();
        args.putInt("score", score);
        f.setArguments(args);

        return f;
    }

    //Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int score = getArguments().getInt("score");

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_input_score, container);

        mScoreEditText = (EditText) view.findViewById(R.id.inputScoreEditText);

        btnOne = (Button) view.findViewById(R.id.btnOne);
        btnOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mScore = Double.parseDouble(mScoreEditText.getText().toString());
                    mListener.onDialogPositiveClick(InputScoreDialog.this);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        btnTwo = (Button) view.findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onDialogNegativeClick(InputScoreDialog.this);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //set the bottom
        p.y = 0;
        p.windowAnimations = R.style.DialogAnimation;

        getDialog().getWindow().setAttributes(p);

        super.onResume();
    }
}
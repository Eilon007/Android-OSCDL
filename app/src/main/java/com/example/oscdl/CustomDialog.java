package com.example.oscdl;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends Dialog implements View.OnClickListener
{
    public Button send;
    public Button cancel;
    public Dialog d;
    public Activity act;
    public EditText ipEditText;
    public CustomDialogListener listener;

    public CustomDialog(Activity activity, CustomDialogListener listener)
    {
        super(activity);
        act = activity;
        this.listener = listener;
    }

    public interface CustomDialogListener {
        void onOkClicked(String ip);
        void onCancelClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_ip);

        ipEditText = (EditText) findViewById(R.id.ipET);
        send = (Button) findViewById(R.id.send);
        cancel = (Button) findViewById(R.id.cancel);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                String ip = ipEditText.getText().toString();
                listener.onOkClicked(ip);
                break;
            case R.id.cancel:
                listener.onCancelClicked();
                break;
            default:
                break;
        }
        dismiss();
    }
}

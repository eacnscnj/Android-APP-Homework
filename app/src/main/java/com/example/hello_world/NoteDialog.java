package com.example.hello_world;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NoteDialog extends Dialog implements View.OnClickListener {

    EditText et;
    Button cancleButton , ensureButton;

    public NoteDialog(Context context){
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_note);
        et=findViewById(R.id.dialog_note_editText);
        cancleButton=findViewById(R.id.dialog_button_cancel);
        ensureButton=findViewById(R.id.dialog_button_ensure);
        cancleButton.setOnClickListener(this);
        ensureButton.setOnClickListener(this);
    }
    public interface onEnsureListener{
        public void onEnsuer();
    }
    onEnsureListener onEnsureListener;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_button_ensure:
                if(onEnsureListener!=null){
                    onEnsureListener.onEnsuer();
                }
                break;

            case R.id.dialog_button_cancel:
                cancel();
                break;
        }
    }
}

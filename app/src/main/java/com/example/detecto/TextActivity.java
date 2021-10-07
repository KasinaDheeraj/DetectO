package com.example.detecto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.detecto.data.MyDatabase;

import java.util.Calendar;

public class TextActivity extends AppCompatActivity {
    EditText bodyET;
    EditText titleET;
    int p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_screen));
        setStatusBarGradiant(this);

        Intent intent=getIntent();
        Bundle extras=intent.getExtras();

        String t=extras.getString("TITLE");
        String msg=extras.getString("BODY");
        p=extras.getInt("id");

        titleET=findViewById(R.id.tv1);
        titleET.setText(t);
        bodyET=findViewById(R.id.tv);
        bodyET.setText(msg);
    }
    public void copyToCB(View v){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("From DetectO",bodyET.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,"Copied to Clipboard",Toast.LENGTH_SHORT).show();
    }
    public void saveToDB(View v){
        String s=String.valueOf(bodyET.getText());
        String t=String.valueOf(titleET.getText());
        if((!s.equals("No Text Detected!"))&&(!s.equals(""))) {
            Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            MyDatabase db = new MyDatabase(this);
            if(t.equals("")){
                t= Calendar.getInstance().getTime().toString();
                int n=t.indexOf("GMT");
                t=t.substring(0,n);
                t=t.trim();
                t=t.substring(0,n-4);
            }
            db.writeToDB(t,s);
            if(p!=-1){
                db.deleteFromDB(p);
            }
            finish();
        }else{
            Toast.makeText(this, "Not saved!", Toast.LENGTH_SHORT).show();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_screen);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}

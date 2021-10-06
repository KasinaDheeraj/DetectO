package com.example.detecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_forgot);
        getSupportActionBar().hide();

        emailEditText=findViewById(R.id.emailEditTextFP);
        auth=FirebaseAuth.getInstance();

    }
    public void gotosignin(View v){
        Intent intent=new Intent(ForgotActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void resetPass(View v){
        final View vv=v;
        String email=emailEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(email)) {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(vv.getContext(), "Reset mail sent", Toast.LENGTH_SHORT).show();
                                Log.d("reset", "Email sent.");
                                gotosignin(vv);
                            } else {
                                Toast.makeText(vv.getContext(), "Enter valid registered email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(this,"Enter non empty Credentials!",Toast.LENGTH_SHORT).show();
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

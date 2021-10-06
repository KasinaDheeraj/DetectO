package com.example.detecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText nameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        auth=FirebaseAuth.getInstance();

        nameEditText=findViewById(R.id.nameEditTextSU);
        emailEditText=findViewById(R.id.emailEditTextSU);
        passwordEditText=findViewById(R.id.passwordEditTextSU);
    }
    public void goToLogin(View v){
        Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void signUp(View v){
        final View vv=v;
        loading = new ProgressDialog(v.getContext());
        loading.setCancelable(false);
        loading.setMessage("Registering...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();

        String email=emailEditText.getText().toString().trim();
        String password=passwordEditText.getText().toString().trim();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(v.getContext(),"Enter non empty Credentials!",Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(vv.getContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(vv.getContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        loading.dismiss();
                        finish();
                    }else{
                        loading.dismiss();
                        Toast.makeText(vv.getContext(),task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

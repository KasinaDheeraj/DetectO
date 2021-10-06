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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    ProgressDialog loading=null;
    EditText emailEditText;
    EditText passwordEditText;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        emailEditText=findViewById(R.id.emailEditTextLI);
        passwordEditText=findViewById(R.id.passwordEditTextLI);
        auth=FirebaseAuth.getInstance();
    }
    public void loginToApp(View v){
        final View vv=v;
        loading = new ProgressDialog(v.getContext());
        loading.setCancelable(false);
        loading.setMessage("Authenticating...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();

        String email=emailEditText.getText().toString().trim();
        String password=passwordEditText.getText().toString().trim();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(v.getContext(),"Enter non empty Credentials!",Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(vv.getContext(),"Login success",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(vv.getContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        loading.dismiss();
                        finish();
                    }else{
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    }
    public void gotosignupscreen(View v){
        Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    public void guestLogin(View v){
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void forgotPassword(View v){
        Intent intent=new Intent(LoginActivity.this,ForgotActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}

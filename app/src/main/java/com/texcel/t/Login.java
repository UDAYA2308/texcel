package com.texcel.t;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    CardView cardView;
    TextInputLayout phone,otp,name,reg;
    TextView login,resend;
    SharedPreferences preferences;
    String code;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    String database;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences=getSharedPreferences(""+R.string.app_name,MODE_PRIVATE);

        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading.....");


        try {
            phone = findViewById(R.id.phone);
            login = findViewById(R.id.log_otp);
            name = findViewById(R.id.name);
            reg = findViewById(R.id.reg_no);
            cardView = findViewById(R.id.login);
            otp = findViewById(R.id.otp);
            auth = FirebaseAuth.getInstance();
            resend=findViewById(R.id.resend);

            name.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    name.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            phone.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    phone.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            reg.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    reg.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            otp.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    otp.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(phone.getEditText().getText().length()==10)
                    sendcode();
                }
            });

           cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validate_all())
                    if (login.getText().equals("GET OTP")) {
                        sendcode();

                    } else {

                        if(validate_all())
                        verify();
                    }
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this,"main  "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressDialog.cancel();
            otp.setVisibility(View.VISIBLE);
            otp.requestFocus();
            resend.setVisibility(View.VISIBLE);
            login.setText("LOGIN");
            Toast.makeText(Login.this,"Verification code sent ",Toast.LENGTH_LONG).show();
            code=s;
        }
    };

    public  void sendcode()
    {

        try{
            progressDialog.show();
            String phoneNumber="+91"+phone.getEditText().getText().toString();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS,this,mCallbacks);
        }catch (Exception e)
        {
            Toast.makeText(Login.this,"send"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent=new Intent(Login.this,Dashboard.class);
                            startActivity(intent);

                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putString(""+R.string.phone,phone.getEditText().getText().toString());
                            editor.putString(""+R.string.reg,reg.getEditText().getText().toString());
                            editor.putString(""+R.string.name,name.getEditText().getText().toString());
                            editor.putBoolean("access",false);

                            editor.commit();
                            progressDialog.cancel();


                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Login.this,"Invalid code",Toast.LENGTH_LONG).show();
                            resend.setVisibility(View.VISIBLE);
                            progressDialog.cancel();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }
                        }
                    }
                });
    }

    public  void  verify()
    {
        try{
            progressDialog.show();
            String seen=otp.getEditText().getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code,seen);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e)
        {
            Toast.makeText(Login.this,"verify"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public boolean validate_all()
    {
        if(name.getEditText().length()==0){
            name.setError("Field Can't be Empty");
            name.requestFocus();
            return  false;
        }

        if(reg.getEditText().length()==0){
            reg.setError("Field Can't be Empty");
            reg.requestFocus();
            return  false;
        }

        if(phone.getEditText().length()==0){
            phone.setError("Field Can't be Empty");
            phone.requestFocus();
            return false;
        }
        else if(phone.getEditText().length()!=10){
            phone.setError("Invalid Phone no");
            phone.requestFocus();
            return false;
        }

        if(otp.getEditText().getText().length()==0&&otp.getVisibility()==View.VISIBLE)
        {
            otp.setError("Field Can't be Empty");
            otp.requestFocus();
            return false;

        }
        if(reg.getEditText().length()==0){
            reg.setError("Field Can't be Empty");
            reg.requestFocus();
            return false;
        }
        else if(reg.getEditText().length()!=9){
            reg.setError("Invalid Register no");
            reg.requestFocus();
            return false;
        }

        return true;
    }



}

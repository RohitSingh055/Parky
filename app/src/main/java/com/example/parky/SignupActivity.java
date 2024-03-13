package com.example.parky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    TextView loginTextView, resendOtp;
    EditText et_username, et_email, et_number, et_password;
    Button btnSendOtp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken ResendingToken;
    String verificationCode,emailLogin, passwordLogin, username;
    Long timeoutSeconds = 30L;
    String prefix = "+91 ";
    String phoneNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loginTextView = findViewById(R.id.loginTextView);
        btnSendOtp = findViewById(R.id.sendOtp);
        et_username = findViewById(R.id.username);
        et_email = findViewById(R.id.email);
        et_number = findViewById(R.id.number);
        et_password = findViewById(R.id.password);


        btnSendOtp.setOnClickListener(v -> {

            //getting values from signup form.

            String originalPhoneNumber = et_number.getText().toString().trim();
            phoneNumber = prefix+originalPhoneNumber;
            emailLogin = et_email.getText().toString().trim();
            passwordLogin = et_password.getText().toString().trim();
            username = et_username.getText().toString().trim();
            
            if(username.isEmpty()) {
                Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(emailLogin.isEmpty()) {
                Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (originalPhoneNumber.length() != 10) {
                Toast.makeText(this, "Please enter valid Mobile", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordLogin.isEmpty()) {
                Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
                return;
            }else{
                System.out.println("Good to go");
            }

            // Dialog to enter the otp

            Dialog dialog = new Dialog(SignupActivity.this);
            dialog.setContentView(R.layout.otp_dialog_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();


            Button submit = dialog.findViewById(R.id.submitOtp);
            resendOtp = dialog.findViewById(R.id.resendOtp);

            submit.setOnClickListener(v1 -> {
                EditText otpInput = dialog.findViewById(R.id.entered_otp);
                String enteredOtp =otpInput.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
            });

            resendOtp.setOnClickListener(v12 -> sendOtp(phoneNumber, true));

            sendOtp(phoneNumber, false);

        });

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });


    }

    public void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.i("OTP", "onVerificationFailed: " + e.getMessage());
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        ResendingToken = forceResendingToken;
                        Toast.makeText(SignupActivity.this, "Otp sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        if(isResend)
        {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    public void signIn(PhoneAuthCredential phoneAuthCredential) {
        // login and go to next Activity
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(i);
                }else {
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startResendTimer(){
        resendOtp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> {
                    resendOtp.setText("Resend Otp in " + timeoutSeconds + " seconds.");
                });
                if(timeoutSeconds<=0)
                {
                    timeoutSeconds = 30L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtp.setText("Resend Now");
                        resendOtp.setEnabled(true);
                    });
                }
            }
        },0,1000);
    }

}
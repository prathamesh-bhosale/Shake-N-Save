package com.example.safetyalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginScreen extends AppCompatActivity {

    private ActivityResultLauncher<Intent> otpLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        otpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle successful OTP verification
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Home.class));
                        finish();
                    } else {
                        // Handle failed OTP verification
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.sendOtp).setOnClickListener(v -> {
            String phone = ((TextInputEditText) findViewById(R.id.loginphonenumber)).getText().toString();
            if (phone.length() == 10) {
                FirebaseFirestore.getInstance().collection("users").document(phone).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phone, 60, TimeUnit.SECONDS, this,
                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                                Intent intent = new Intent(LoginScreen.this, OtpActivity.class);
                                                intent.putExtra("phonenum", phone);
                                                intent.putExtra("otp", verificationId);
                                                otpLauncher.launch(intent);
                                            }
                                            // ... other callbacks
                                        });
                            } else {
                                Toast.makeText(LoginScreen.this, "This phone number is not registered", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        findViewById(R.id.gotoRegister).setOnClickListener(v -> {
            startActivity(new Intent(LoginScreen.this, RegisterActivity.class));
            finish();
        });
    }
}

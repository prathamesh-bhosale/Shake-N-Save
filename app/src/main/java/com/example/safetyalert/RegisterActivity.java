package com.example.safetyalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // This is where you would handle the result from the LoginScreen, if any.
                    // In this case, we just finish the RegisterActivity.
                    finish();
                });

        findViewById(R.id.registerbtn).setOnClickListener(v -> {
            String name = ((TextInputEditText) findViewById(R.id.entername)).getText().toString();
            String phone = ((TextInputEditText) findViewById(R.id.enterphonenumber)).getText().toString();
            String age = ((TextInputEditText) findViewById(R.id.enterage)).getText().toString();

            Map<String, Object> usermap = new HashMap<>();
            usermap.put("age", age);
            usermap.put("gender", "male");
            usermap.put("name", name);
            usermap.put("phonenumber", phone);

            FirebaseFirestore.getInstance().collection("users").document(phone).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().exists()) {
                            FirebaseFirestore.getInstance().collection("users").document(phone).set(usermap)
                                    .addOnCompleteListener(task1 -> {
                                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        loginLauncher.launch(new Intent(this, LoginScreen.class));
                                    });
                        } else {
                            Toast.makeText(this, "This phone number is already taken", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        findViewById(R.id.gotoLogin).setOnClickListener(v -> {
            loginLauncher.launch(new Intent(this, LoginScreen.class));
        });
    }
}

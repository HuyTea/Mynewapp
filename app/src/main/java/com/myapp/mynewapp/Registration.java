package com.myapp.mynewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration extends AppCompatActivity {

    EditText musername, memail, mpassword, mphone;
    TextView Status;
    Button register;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;




    Connection con;
    Statement stmt;
    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        musername = findViewById(R.id.username);
        memail = findViewById(R.id.Email);
        mpassword = findViewById(R.id.password);
        mphone = findViewById(R.id.repassword);
        register = findViewById(R.id.signupbtn);
        Status = findViewById(R.id.status);
        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        //Register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = memail.getText().toString().trim();
                String pass = mpassword.getText().toString().trim();
                String username = musername.getText().toString().trim();
                String phone = mphone.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    memail.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mpassword.setError("Password is empty. ");
                    return;
                }
                if(TextUtils.isEmpty(username)){
                    musername.setError("Username is empty. ");
                    return;
                }
                if(pass.length()<6){
                    mpassword.setError("Password must be >=6 charaters");

                    return;
                }
                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Registration.this,"User created.",Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName",username);
                            user.put("email",email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"onSuccess: "+ userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Main.class));
                            finish();
                        }else{
                            Toast.makeText(Registration.this,"Error !"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
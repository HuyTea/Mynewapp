package com.myapp.mynewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.snapshot.StringNode;
import com.hbb20.CountryCodePicker;
import com.myapp.mynewapp.Models.users;

import net.sourceforge.jtds.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Login extends AppCompatActivity {
    EditText text,memail,mPassword;
    Button googleBtn,LoginUser,SignupUser ;
    private GoogleSignInClient GSC;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progress;
    TextView forgotpass;

    DBSQL con;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//Googlelogin
        setContentView(R.layout.activity_login);
        googleBtn = findViewById(R.id.google_btn);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://mynewapp-3fd23-default-rtdb.firebaseio.com/");
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GSC = GoogleSignIn.getClient(this,options);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = GSC.getSignInIntent();
                startActivityForResult(i,123);
            }
        });
        progress=findViewById(R.id.wait);

        progress.setVisibility(View.GONE);


//Login with username password
        LoginUser=findViewById(R.id.loginbtn);
        SignupUser=findViewById(R.id.signup);
        memail=findViewById(R.id.username);
        mPassword=findViewById(R.id.password);
        forgotpass=findViewById(R.id.RSpass);

        LoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = memail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    memail.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    mPassword.setError("Password is empty. ");
                    return;
                }

                if(pass.length()<6){
                    mPassword.setError("Password must be >=6 charaters");
                    return;
                }
                // authenticate the user
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Login successfully.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Main.class));
                        }else{
                            Toast.makeText(Login.this,"Login fail !"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
//SignUp
        SignupUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Registration.class);
                startActivity(intent);
                finish();
            }
        });
//ResetPassword
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("RESET PASSWORD ?");
                passwordResetDialog.setMessage("Enter your email to received reset link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login.this,"Reset link sent to your email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error Reset link is not sent"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });





    }
//Google login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                    if (task.isSuccessful()){
                       FirebaseUser user = auth.getCurrentUser();
                       users users1 = new users();
                       assert user != null;
                       users1.setUserid(user.getUid());
                       users1.setUsername(user.getDisplayName());
                        users1.setProfilepic(user.getPhotoUrl().toString());
                        database.getReference().child(user.getDisplayName()).setValue(users1);
                        Intent intent = new Intent(Login.this, Main.class);
                       intent.putExtra("username",user.getDisplayName());
                        intent.putExtra("profilepic",user.getPhotoUrl().toString());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    }
                });
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

//    public Connection connectionclass()
//    {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        Connection connection =null;
//        String ConnectionURL=null;
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            ConnectionURL="";
//            connection = DriverManager.getConnection(ConnectionURL);
//        }catch (SQLException se)
//        {
//            Log.e("",se.getMessage());
//        }catch (ClassNotFoundException e)
//        {
//            Log.e("",e.getMessage());
//        }catch (Exception e)
//        {
//            Log.e("",e.getMessage());
//        }
//        return connection;
//    }

}
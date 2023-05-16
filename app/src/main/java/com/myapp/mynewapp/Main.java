package com.myapp.mynewapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class Main extends AppCompatActivity {

    ImageView imageView;
    TextView textView,verifymessage,fullname,email,phone;
     Button signout,verify,show;
    GoogleSignInOptions GSO;
    GoogleSignInClient GSC;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        signout = findViewById(R.id.Signout);


        String username = getIntent().getStringExtra("username");
        String Profilepic = getIntent().getStringExtra("profilepic");
        textView.setText(username);
        Picasso.get().load(Profilepic).into(imageView);

        GSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GSC = GoogleSignIn.getClient(this, GSO);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
//Verify email
        verify = findViewById(R.id.Veribtn);
        verifymessage = findViewById(R.id.Verifymail);
//
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fuser = auth.getCurrentUser();
//
        if(!fuser.isEmailVerified()){
            verifymessage.setVisibility(View.VISIBLE);
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Verification email sent.");
                                        verifymessage.setVisibility(View.INVISIBLE);
                                        verify.setVisibility(View.INVISIBLE);
                                    } else {
                                        Log.e("TAG", "Failed to send verification email.", task.getException());
                                    }
                                }
                            });
                }
            });
        }
//Loaduser
        show=findViewById(R.id.Show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullname.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
            }
        });
        fullname = findViewById(R.id.Yourname);
        email=findViewById(R.id.Youremail);
        phone=findViewById(R.id.Yourphonenum);


        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userId=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                phone.setText(documentSnapshot.getString("phone"));
                fullname.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });

    }

    private void signOut() {
        GSC.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(Main.this, Login.class));
                    }
                });
    }

}

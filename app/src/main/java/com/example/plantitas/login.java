package com.example.plantitas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class login extends AppCompatActivity {
    private static int AUTH_REQUEST_CODE=0707;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;
    ImageButton google,anonymous;
    TextView signup,forgotpassword;
    EditText lgn_email,lgn_password;
    Button lgn_button;
    ProgressBar progressBar;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lgn_email = findViewById(R.id.lgn_email);
        lgn_password = findViewById(R.id.lgn_password);
        firebaseAuth = FirebaseAuth.getInstance();
        signup = (TextView) findViewById(R.id.textsignup);
        lgn_button = findViewById(R.id.loginbtn);
        progressBar = findViewById(R.id.progressBar2);
        forgotpassword = findViewById(R.id.forgotPass);






        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });
        //check if user is still logged in if yes go to main activity(HOME)
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
            lgn_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = lgn_email.getText().toString().trim();
                    String password = lgn_password.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        lgn_email.setError("Email is Required.");
                        return;

                    }
                    if (TextUtils.isEmpty(password)) {
                        lgn_password.setError("Email is Required.");
                        return;

                    }
                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });


                }
            });

            forgotpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText resetMail = new EditText(v.getContext());
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset Password");
                    passwordResetDialog.setMessage("Enter your email");
                    passwordResetDialog.setView(resetMail);

                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //extract the email and send link
                            String mail = resetMail.getText().toString();
                            firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(login.this, "Reset link is sent to your Email", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(login.this, "Reset Link not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close dialog
                        }
                    });
                    passwordResetDialog.create().show();
                }
            });

        }


    }


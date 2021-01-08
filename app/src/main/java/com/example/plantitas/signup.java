package com.example.plantitas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    //Initialize Variables Here
    public static final String TAG = "TAG";
    EditText sgn_fullname,sgn_email,sgn_address,sgn_password,sgn_contact;
    Button sgn_signupBtn;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Set the ID to the corresponding variable
        radioGroup=findViewById(R.id.radiogroup);
        sgn_fullname=findViewById(R.id.sgn_fullname);
        sgn_email=findViewById(R.id.sgn_email);
        sgn_address=findViewById(R.id.sgn_address);
        sgn_password=findViewById(R.id.sgn_password);
        sgn_contact=findViewById(R.id.contact);
        sgn_signupBtn=findViewById(R.id.btn_sgn_login);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();


        progressBar=findViewById(R.id.progressBar);
        //check if user is still logged in if yes go to main activity(HOME)
        if (firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        //button onclicklistner getting the inputted values and validating
        sgn_signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=sgn_email.getText().toString().trim();
                String password=sgn_password.getText().toString().trim();
                final String fullname=sgn_fullname.getText().toString();
                final String address=sgn_address.getText().toString();
                final String phone=sgn_contact.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                final String gender=radioButton.getText().toString();


                if(TextUtils.isEmpty(email)){
                    sgn_email.setError("Email is Required.");
                    return;

                }
                if (TextUtils.isEmpty(fullname)){
                    sgn_fullname.setError("Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    sgn_address.setError("Address is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    sgn_password.setError("Password is Required.");
                    return;
                }
                if(password.length()<8){
                    sgn_password.setError("Password must be at least 8 Characters. ");
                    return;
                }
                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(signup.this, "Please choose a Gender", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(phone))
                {
                    sgn_contact.setError("Contact number is required");
                }

                progressBar.setVisibility(View.VISIBLE);

                //if sign up is successful store the user's information
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //verify email

                                FirebaseUser user=firebaseAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(signup.this, "Email Verification link has been sent to your Email address", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Error:Email not sent"+e.getMessage());
                                    }
                                });



                                UserHelperClass helperClass=new UserHelperClass(email,fullname,address,phone,gender);
                                Toast.makeText(signup.this, "User is Created", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(helperClass);




                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                            else{
                                Toast.makeText(signup.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                    }
                });

            }
        });




    }
}
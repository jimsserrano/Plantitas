package com.example.plantitas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class edit_user_profile extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName() ;
    ImageView cancel,edit_user_profile;
    EditText  edit_user_name,edit_user_address,edit_user_contact,edit_user_email;
    RadioButton edit_radioMale,edit_radioFemale;
    RadioGroup radioGroup;
    Button edit_save;
    FragmentManager fragmentManager;
    FirebaseAuth firebaseauth;
    RadioButton radioButton;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    int TAKE_IMAGE_CODE=0707;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        cancel=findViewById(R.id.cancel_btn);
        edit_user_profile=findViewById(R.id.edit_user_profile);
        edit_user_name=findViewById(R.id.edit_user_name);
        edit_user_address=findViewById(R.id.edit_user_address);
        edit_user_contact=findViewById(R.id.edit_user_contact);
        edit_user_email=findViewById(R.id.edit_user_email);
        radioGroup=findViewById(R.id.edit_gender);

        edit_radioMale=findViewById(R.id.edit_radioMale);
        edit_radioFemale=findViewById(R.id.edit_radioFemale);
        edit_save=findViewById(R.id.edit_save);
        firebaseDatabase=FirebaseDatabase.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=firebaseDatabase.getReference("users");
        UserID=user.getUid();
        if(user.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(edit_user_profile);
        }
        databaseReference.child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass=snapshot.getValue(UserHelperClass.class);

                if(userHelperClass!=null) {
                    String fullname = userHelperClass.fullname;
                    String address = userHelperClass.address;
                    String contact = userHelperClass.contact;
                    String email = userHelperClass.email;
                    String gender=userHelperClass.gender;

                    edit_user_name.setText(fullname);
                    edit_user_address.setText(address);
                    edit_user_contact.setText(contact);
                    edit_user_email.setText(email);
                    if(gender.equalsIgnoreCase("Male")){
                        edit_radioMale.setChecked(true);
                    }else{
                        edit_radioFemale.setChecked(true);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(edit_user_profile.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=edit_user_email.getText().toString().trim();

                final String fullname=edit_user_name.getText().toString();
                final String address=edit_user_address.getText().toString();
                final String phone=edit_user_contact.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                final String gender=radioButton.getText().toString();


                if(TextUtils.isEmpty(email)){
                    edit_user_email.setError("Email is Required.");
                    return;

                }
                if (TextUtils.isEmpty(fullname)){
                    edit_user_name.setError("Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(address)){
                    edit_user_address.setError("Address is required.");
                    return;
                }

                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(edit_user_profile.this, "Please choose a Gender", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(phone))
                {
                    edit_user_contact.setError("Contact number is required");
                }
                UserHelperClass userHelperClass=new UserHelperClass(email,fullname,address,phone,gender);
                FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userHelperClass);

                Fragment fragment;
                fragment=new ProfileFragment();
                if(fragment!=null)
                {
                    fragmentManager=getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
                    Toast.makeText(edit_user_profile.this, "Updated details Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.e(TAG,"Error in Creating Fragment");
                }
            }

        });


    }

    public void cancelbtn(View view) {
        Fragment fragment;
        fragment=new ProfileFragment();
        if(fragment!=null)
        {
            fragmentManager=getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
        }
        else
        {
            Log.e(TAG,"Error in Creating Fragment");
        }
    }

    public void editProfilePhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),TAKE_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        edit_user_profile.setImageBitmap(bitmap);
                        handleUpload(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(edit_user_profile.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }

    }



    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream output= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,output);
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid+".jpeg");
        reference.putBytes(output.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure:",e.getCause());
                    }
                });
    }
    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e(TAG,"onSuccess:"+uri);
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri)
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(edit_user_profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(edit_user_profile.this, "Profile image failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
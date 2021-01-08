package com.example.plantitas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class SellFragment extends Fragment {
    private Button mButtonChooseImage;
    private static final String TAG =MainActivity.class.getSimpleName() ;
    private Button mButtonUpload;
    int TAKE_IMAGE_CODE=0707;
    RadioGroup radioGroup;
    View view;
    RecyclerView rcvPhoto;
    PhotoAdapter photoAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView item_photo;
    EditText plant_name,product_price,product_description;
    RadioGroup radioGroup_type1,radioGroup_deal;
    RadioButton radioButton1,radioButton3;
    public SellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sell, container, false);
        mButtonChooseImage = view.findViewById(R.id.add_image);
        item_photo = view.findViewById(R.id.item_photo);
        mButtonUpload = view.findViewById(R.id.upload_button);
        plant_name = view.findViewById(R.id.plant_name);
        product_price = view.findViewById(R.id.product_price);
        product_description = view.findViewById(R.id.product_description);
        radioGroup_type1=view.findViewById(R.id.radiogroup_type);

        radioGroup_deal=view.findViewById(R.id.radiogroup_deal);




        //Pick images from Gallery
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });


        return view;
    }


    private void requestPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImageFromGallery();
            }

            @Override
            public void onPermissionDenied(@NonNull List<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),TAKE_IMAGE_CODE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        item_photo.setImageBitmap(bitmap);
                        final Bitmap finalBitmap = bitmap;
                        mButtonUpload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handleUpload(finalBitmap);
                                uploadDetails();

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void uploadDetails(){}

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream output= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,output);
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference= FirebaseStorage.getInstance().getReference()
                .child(uid)
                .child(System.currentTimeMillis()+".jpeg");
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
                String url=String.valueOf(uri);
                setUserProfileUrl(url);

            }
        });
    }


    private void setUserProfileUrl(String url)
    {
        final String plantname=plant_name.getText().toString().trim();
        String price=product_price.getText().toString().trim();

        final String description=product_description.getText().toString();

        int selectedId1 = radioGroup_type1.getCheckedRadioButtonId();

        int selectedId3= radioGroup_deal.getCheckedRadioButtonId();

        radioButton1 = view.findViewById(selectedId1);

        radioButton3 = view.findViewById(selectedId3);

        final String plant_type=radioButton1.getText().toString();
        final String deal_method=radioButton3.getText().toString();

        if(TextUtils.isEmpty(plantname)){
            plant_name.setError("Plant Name is Required.");
            return;

        }
        if (TextUtils.isEmpty(description)){
            product_description.setError("Plant Description is Required.");
            return;
        }
        if (TextUtils.isEmpty(price)){
            product_price.setError("Price is required.");
            return;
        }

        if (radioGroup_type1.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(getActivity(), "Please choose a plant type", Toast.LENGTH_SHORT).show();
        }
        if (radioGroup_deal.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(getActivity(), "Please choose a deal method", Toast.LENGTH_SHORT).show();
        }


        DetailsClass helperClass=new DetailsClass(plantname,price,plant_type,description,deal_method);
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference newdatabaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("itemphoto").child("details");




        HashMap<String, String> hashMap=new HashMap<>();
        hashMap.put("ItemPhoto",url);

        newdatabaseReference.setValue(hashMap)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Image failed to upload.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
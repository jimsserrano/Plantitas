package com.example.plantitas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


public class ProfileFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    View view;
    ImageView user_profile;
    TextView user_name;
    Button edit_profile,logout_btn;
    FirebaseAuth firebaseauth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    private String UserID;
    public ProfileFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_profile, container, false);
        tabLayout=view.findViewById(R.id.profile_tablayout);
        viewPager=view.findViewById(R.id.viewpager);
        user_profile=view.findViewById(R.id.user_profile);
        user_name=view.findViewById(R.id.user_name);
        edit_profile=view.findViewById(R.id.edit_profile);
        logout_btn=view.findViewById(R.id.logout_btn);

        firebaseDatabase=FirebaseDatabase.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        databaseReference=firebaseDatabase.getReference("users");
        UserID=user.getUid();
        if(user.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(user_profile);
        }

        databaseReference.child(UserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userHelperClass=snapshot.getValue(UserHelperClass.class);

                    if(userHelperClass!=null) {
                        String fullname = userHelperClass.fullname;

                        user_name.setText(fullname);
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseauth.getInstance().signOut();
                startActivity(new Intent(getContext(),login.class));

            }
        });


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), edit_user_profile.class);
                startActivity(intent);
            }
        });
        return  view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        pagerAdapter adapter=new pagerAdapter(getChildFragmentManager());
        adapter.addFragment(new listing_fragment(),"Listings");
        adapter.addFragment(new feedback_fragment(),"Feedbacks");
        viewPager.setAdapter(adapter);
    }
}
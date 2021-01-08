package com.example.plantitas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName() ;
    private static final int REQUEST_EXTERNAL_STORAGE = 10;
    private Toast backtoast;
    FragmentManager fragmentManager;

    private long backPressedTime;
    ChipNavigationBar nav_bottom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav_bottom=findViewById(R.id.bottomNav);



        if(savedInstanceState==null)
        {
            nav_bottom.setItemSelected(R.id.nav_home,true);
            fragmentManager=getSupportFragmentManager();
            HomeFragment homeFragment=new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,homeFragment).commit();
        }

       nav_bottom.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
           @Override
           public void onItemSelected(int id) {
               Fragment fragment=null;
               switch (id) {
                   case R.id.nav_home:
                       fragment=new HomeFragment();

                        break;



                   case R.id.nav_sell:
                       fragment=new SellFragment();

                       break;
                   case R.id.nav_messages:
                       fragment=new MessageFragment();

                       break;

                   case R.id.nav_profile:
                       fragment=new ProfileFragment();

                       break;
               }
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
       });


    }
    @Override
    public void onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()){
            backtoast.cancel();
            super.onBackPressed();
            return;
        }
        else
        {
            backtoast=Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backtoast.show();
        }
        backPressedTime=System.currentTimeMillis();
    }


    }



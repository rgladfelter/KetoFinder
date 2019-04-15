package edu.radford.cerj.ketofinder;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends FragmentActivity
        implements AccountFragment.OnFragmentInteractionListener{

    private AccountFragment acc_frag = new AccountFragment();
    private edu.radford.cerj.ketofinder.MapFragment map_frag = new edu.radford.cerj.ketofinder.MapFragment();

    private DatabaseReference database;
    private FirebaseAuth mAuth;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_account:
                    setTitle("Account");
                    android.support.v4.app.FragmentTransaction frag_trans1
                            = getSupportFragmentManager().beginTransaction();
                    frag_trans1.replace(R.id.frame, acc_frag, "Fragment");
                    frag_trans1.commit();
                    return true;
                case R.id.navigation_map:
                    setTitle("Map");
                    android.support.v4.app.FragmentTransaction frag_trans2
                            = getSupportFragmentManager().beginTransaction();
                    frag_trans2.replace(R.id.frame, map_frag, "Fragment");
                    frag_trans2.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle userData = this.getIntent().getExtras();




        acc_frag.setArguments(userData);
        setTitle("Account");
        android.support.v4.app.FragmentTransaction frag_trans1
                = getSupportFragmentManager().beginTransaction();
        frag_trans1.replace(R.id.frame, acc_frag, "Fragment");
        frag_trans1.commit();

    }


    public void editAccount(View v){
        acc_frag.editAccount(v);
    }

    public void signOut(View v){
        acc_frag.signOut(v);
    }
}
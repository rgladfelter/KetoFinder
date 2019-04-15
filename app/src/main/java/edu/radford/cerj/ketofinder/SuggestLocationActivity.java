package edu.radford.cerj.ketofinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuggestLocationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_location);

        Button finalizeAcc = findViewById(R.id.finalize_account);

        finalizeAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
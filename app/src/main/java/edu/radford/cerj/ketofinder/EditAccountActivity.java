package edu.radford.cerj.ketofinder;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        final EditText name = findViewById(R.id.enter_name);
        final EditText about = findViewById(R.id.enter_about);
        final EditText pic = findViewById(R.id.enter_pic);



        Button confirm = findViewById(R.id.finalize_account);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FirebaseUser user = mAuth.getCurrentUser();
                if(!name.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("Name").setValue(name.getText().toString());
                if(!about.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("About").setValue(about.getText().toString());
                if(!pic.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("Picture").setValue(pic.getText().toString());
                finish();
            }

        });
    }
}

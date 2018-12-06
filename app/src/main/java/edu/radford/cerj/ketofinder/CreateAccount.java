package edu.radford.cerj.ketofinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private EditText email;
    private EditText pass;
    private EditText firstName;
    private EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //mAuth = FirebaseAuth.getInstance();
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //mDatabase = database.getReference();
        mDatabase.setValue("Hello WOrld");

        email = findViewById(R.id.enter_email);
        pass = findViewById(R.id.enter_pass);
        firstName = findViewById(R.id.enter_first_name);
        lastName = findViewById(R.id.enter_last_name);
        TextView mlinkLogin = (TextView) findViewById(R.id.link_login);

        Button mSubmitButton = findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser(email.getText().toString(), pass.getText().toString());
            }
        });

        mlinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }


    private void createNewUser(String userEmail, String userPass){
        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("Users").child(user.getUid()).child(firstName.getText().toString());
                            mDatabase.child("Users").child(user.getUid()).child(lastName.getText().toString());
                            updateUI(user);
                            startActivity(new Intent(CreateAccount.this, LoginActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    private Bundle updateUI(FirebaseUser user) {
        Bundle userInfo = new Bundle();
        if(user != null) {
            userInfo.putString("name", user.getDisplayName());
            userInfo.putString("email", user.getEmail());
            if(user.getPhotoUrl() != null)
                userInfo.putString("profile_pic", user.getPhotoUrl().toString());
        }


        return userInfo;
    }

}

package edu.radford.cerj.ketofinder;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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
        FirebaseUser user = mAuth.getCurrentUser();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!name.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("Name").setValue(name.getText().toString());
                if(!about.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("About").setValue(about.getText().toString());
                if(!pic.getText().toString().equals(""))
                    mDatabase.child("Users").child(user.getUid()).child("Picture").setValue(pic.getText().toString());
                finish();
            }

        });

        Button deleteButton = findViewById(R.id.delete_account);
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (d, w) -> {
                        assert user != null;
                        mDatabase.child("Users").child(user.getUid()).removeValue((a, b) -> {
                            Objects.requireNonNull(mAuth.getCurrentUser()).delete();
                            LoginManager.getInstance().logOut();

                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}

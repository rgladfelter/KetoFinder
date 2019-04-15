package edu.radford.cerj.ketofinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;



//    @BindView(R.id.email) EditText mEmailField;
//    @BindView(R.id.password) EditText mPasswordField;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        TextView mSignupLink = findViewById(R.id.link_signup);
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        Button mSignInButton = findViewById(R.id.sign_in_button);
//        Button mCreateAccButton = findViewById(R.id.create_acc_button);
        mSignInButton.setOnClickListener(view -> signIn(mEmailField.getText().toString(), mPasswordField.getText().toString()));

//        mCreateAccButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
//            }
//        });

        mSignupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

//        mEmailField.setHintTextColor(Color.WHITE);
//        mPasswordField.setHintTextColor(Color.WHITE);
        this.signIn("test5@test.com", "password");
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm(email, password)) {
            return;
        }


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {

                        }

                    }
                });
        // [END sign_in_with_email]
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

}

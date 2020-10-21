package com.example.nitdurgapur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import java.util.prefs.Preferences;

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText mEmail, mPassword;
    private TextView mForgetPassword;
    private Button mSignIn, mSkip;

    public static boolean skip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = (Login.this).getSharedPreferences(getString(R.string.sharedPreferences), MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);
        mSignIn = (Button) findViewById(R.id.login_sign_in);
        mSkip = (Button) findViewById(R.id.login_skip);
        mForgetPassword = (TextView) findViewById(R.id.login_forget_password);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(email.isEmpty()) {
                    mEmail.setError("Please enter your Email ID");
                    mEmail.requestFocus();
                }
                else if(password.isEmpty()) {
                    mPassword.setError("Please enter your password");
                    mPassword.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty()) {
                    mEmail.setError("Please enter your Email ID");
                    mPassword.setError("Please enter your password");
                    mEmail.requestFocus();
                    mPassword.requestFocus();
                }
                else if(!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed. Try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = (Login.this).getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.shared_skip), true);
                editor.apply();
                updateUI("GUEST");
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                if(email.isEmpty()) {
                    mEmail.setError("Please enter your Email ID");
                    mEmail.requestFocus();
                }
                else {
                    showPopup();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if user came from HomePage
        Intent intent = getIntent();
        if(!intent.getBooleanExtra("NotSignIn", false)) {
            //Check if previous skip
            SharedPreferences sharedPreferences = (Login.this).getPreferences(Context.MODE_PRIVATE);
            boolean valSkip = sharedPreferences.getBoolean(getString(R.string.shared_skip),false);
            if(valSkip) {
                updateUI("GUEST");
            }
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI(String string) {
        if(string == "GUEST") {
            Intent intent = new Intent(Login.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void showPopup() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.popup, null);

        dialogBuilder.setView(popUpView);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}
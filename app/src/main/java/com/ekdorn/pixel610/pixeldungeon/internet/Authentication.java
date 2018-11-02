package com.ekdorn.pixel610.pixeldungeon.internet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ekdorn.pixel610.R;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by User on 30.03.2017.
 */

public class Authentication extends Dialog {
    private LinearLayout main;

    public Authentication(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = new LinearLayout(this.getContext());
        main.setGravity(Gravity.CENTER);
        LayoutInflater linf = LayoutInflater.from(this.getContext());
        main.addView(linf.inflate(R.layout.signin_primary, null));
        main.addView(linf.inflate(R.layout.signin_log_in, null));
        main.addView(linf.inflate(R.layout.signin_sign_in, null));
        setContentView(main);

        main.getChildAt(1).setVisibility(View.GONE);
        main.getChildAt(2).setVisibility(View.GONE);

        configureDialog();
    }

    private void configureDialog() {
        this.setCancelable(false);


        Button button1 = (Button) findViewById(R.id.signin_button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getChildAt(0).setVisibility(View.GONE);
                main.getChildAt(1).setVisibility(View.VISIBLE);
                Authentication.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });
        Button button2 = (Button) findViewById(R.id.signin_button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getChildAt(0).setVisibility(View.GONE);
                main.getChildAt(2).setVisibility(View.VISIBLE);
                Authentication.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });


        EditText inputName = (EditText) findViewById(R.id.setusername);
        EditText inputInviter = (EditText) findViewById(R.id.setinviter);
        EditText inputEmail = (EditText) findViewById(R.id.setemail);
        EditText inputPassword = (EditText) findViewById(R.id.setpassword);
        EditText inputConfirmation = (EditText) findViewById(R.id.setconfirmation);
        Button submitButton = (Button) findViewById(R.id.submitbuttom);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString();
                final String inviter = inputInviter.getText().toString();
                final String password = inputPassword.getText().toString();
                final String email = inputEmail.getText().toString();

                if (name.equals("")) {
                    inputName.setError("This field must be filled");
                } else if (email.equals("")) {
                    inputEmail.setError("Email is required");
                } else if (password.equals("")) {
                    inputPassword.setError("This field is empty!");
                } else if (!password.equals(inputConfirmation.getText().toString())) {
                    inputConfirmation.setError("password mismatch"); //password validity check
                } else if (name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]") || name.contains("@")) {
                    inputName.setError("symbols error"); //forbidden symbols check
                } else if (!email.contains("@") || !email.contains(".")) {
                    inputEmail.setError("Email malformed");
                } else if (name.contains(" ")) {
                    inputPassword.setError("Invalid spaces"); //space check
                } else if (name.length() <= 5) {
                    inputName.setError("Too short"); //ID length check
                } else if (password.length() <= 7) {
                    inputPassword.setError("Too short"); //password length check
                } else {
                    FireBaser.checkIfUserExists(name, new FireBaser.OnBooleanResult() {
                        @Override
                        public void onResult(boolean result) {
                            if (!result) {
                                authenticate(email, password, name, inviter);
                            } else {
                                inputName.setError("Already taken");
                            }
                        }
                    });
                }
            }
        });


        EditText sInputName = (EditText) findViewById(R.id.getuid);
        EditText sInputPassword = (EditText) findViewById(R.id.getpassword);
        Button sSubmitButton = (Button) findViewById(R.id.submittbutton1);
        sSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = sInputName.getText().toString();
                final String password = sInputPassword.getText().toString();

                if (name.contains("@") && name.contains(".")) {

                    FireBaser.findIdByEmail(name, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(final String id) {
                            if (!id.equals("")) {
                                login(id, name, password);
                            } else {
                                Toast.makeText(Authentication.this.getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    FireBaser.findEmailById(name, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(final String email) {
                            if (!email.equals("")) {
                                login(name, email, password);
                            } else {
                                Toast.makeText(Authentication.this.getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void login(final String id, String email, String password) {
        Log.e("TAG", "authenticate: " + password + " " + email );
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    PXL610.user_name(id);
                    InDev.loadSuperuserName();
                    FireBaser.updateUser(id, email);
                    if (InDev.isDeveloper()) Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("super_access"), Toast.LENGTH_SHORT).show();
                    FireBaser.loadBonus(PXL610.user_name());
                    Authentication.this.dismiss();
                } else {
                    Toast.makeText(Authentication.this.getContext(), "password_incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void authenticate(String email, String password, final String name, final String inviter) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.e("TAG", "onComplete: ", task.getException());
                    Toast.makeText(Authentication.this.getContext(), "email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    PXL610.user_name(name);
                    InDev.loadSuperuserName();
                    FireBaser.updateUser(name, email);
                    if (InDev.isDeveloper()) Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("super_access"), Toast.LENGTH_SHORT).show();
                    FireBaser.invite(inviter, new FireBaser.OnVoidResult() {
                        @Override
                        public void onResult() {
                            Authentication.this.dismiss();
                            FireBaser.loadBonus(PXL610.user_name());
                        }
                    });
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        main.getChildAt(0).setVisibility(View.VISIBLE);
        main.getChildAt(1).setVisibility(View.GONE);
        main.getChildAt(2).setVisibility(View.GONE);
    }
}
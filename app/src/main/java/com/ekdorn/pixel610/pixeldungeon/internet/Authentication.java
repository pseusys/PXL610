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
import android.widget.TextView;
import android.widget.Toast;

import com.ekdorn.pixel610.R;
import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.TextureFilm;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.scenes.TitleScene;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.pixeldungeon.windows.WndSettings;
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


        TextView welcome = (TextView) findViewById(R.id.welcome_text);
        welcome.setText(Babylon.get().getFromResources("inviter_welcome"));

        Button button1 = (Button) findViewById(R.id.signin_button);
        button1.setText(Babylon.get().getFromResources("inviter_signin"));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getChildAt(0).setVisibility(View.GONE);
                main.getChildAt(2).setVisibility(View.VISIBLE);
                Authentication.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });

        Button button2 = (Button) findViewById(R.id.login_button);
        button2.setText(Babylon.get().getFromResources("inviter_login"));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.getChildAt(0).setVisibility(View.GONE);
                main.getChildAt(1).setVisibility(View.VISIBLE);
                Authentication.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });


        TextView setWelcome = (TextView) findViewById(R.id.signin_welcome);
        setWelcome.setText(Babylon.get().getFromResources("inviter_signin_welcome"));

        TextView setNameText = (TextView) findViewById(R.id.signin_setuid_text);
        setNameText.setText(Babylon.get().getFromResources("inviter_signin_setuid"));
        EditText setName = (EditText) findViewById(R.id.signin_setuid);
        setName.setHint(Babylon.get().getFromResources("inviter_signin_setuid_hint"));

        TextView setInviterText = (TextView) findViewById(R.id.signin_setinviter_text);
        setInviterText.setText(Babylon.get().getFromResources("inviter_signin_setinviter"));
        EditText setInviter = (EditText) findViewById(R.id.signin_setinviter);
        setInviter.setHint(Babylon.get().getFromResources("inviter_signin_setinviter_hint"));

        TextView setEmailText = (TextView) findViewById(R.id.signin_setemail_text);
        setEmailText.setText(Babylon.get().getFromResources("inviter_signin_setemail"));
        EditText setEmail = (EditText) findViewById(R.id.signin_setemail);
        setEmail.setHint(Babylon.get().getFromResources("inviter_signin_setemail_hint"));

        TextView setPasswordText = (TextView) findViewById(R.id.signin_setpassword_text);
        setPasswordText.setText(Babylon.get().getFromResources("inviter_signin_setpassword"));
        EditText setPassword = (EditText) findViewById(R.id.signin_setpassword);
        setPassword.setHint(Babylon.get().getFromResources("inviter_signin_setpassword_hint"));

        TextView setConfirmationText = (TextView) findViewById(R.id.signin_setconfirmation_text);
        setConfirmationText.setText(Babylon.get().getFromResources("inviter_signin_setconfirmation"));
        EditText setConfirmation = (EditText) findViewById(R.id.signin_setconfirmation);
        setConfirmation.setHint(Babylon.get().getFromResources("inviter_signin_setconfirmation_hint"));

        Button setButton = (Button) findViewById(R.id.signin_submitbuttom);
        setButton.setText(Babylon.get().getFromResources("inviter_signin_button"));
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = setName.getText().toString();
                final String inviter = setInviter.getText().toString();
                final String password = setPassword.getText().toString();
                final String email = setEmail.getText().toString();

                if (name.equals("")) {
                    setName.setError(Babylon.get().getFromResources("inviter_runtime_mustbefilled"));
                } else if (email.equals("")) {
                    setEmail.setError(Babylon.get().getFromResources("inviter_runtime_noemail"));
                } else if (password.equals("")) {
                    setPassword.setError(Babylon.get().getFromResources("inviter_runtime_mustbefilled"));
                } else if (!password.equals(setConfirmation.getText().toString())) {
                    setConfirmation.setError(Babylon.get().getFromResources("inviter_runtime_mismatch")); // password validity check
                } else if (symbolCatch(name) != 'p') {
                    setName.setError(Utils.format(Babylon.get().getFromResources("inviter_runtime_letternotexists"), Character.toString(symbolCatch(name))));
                } else if (strangeSymbolCatch(name) != 'p') {
                    setName.setError(Utils.format(Babylon.get().getFromResources("inviter_runtime_strangeletters"), Character.toString(strangeSymbolCatch(name)))); //forbidden symbols check
                } else if (!email.contains("@") || !email.contains(".")) {
                    setEmail.setError(Babylon.get().getFromResources("inviter_runtime_bademail"));
                } else if (name.contains(" ")) {
                    setPassword.setError(Babylon.get().getFromResources("inviter_runtime_namespaces")); //space check
                } else if (name.length() <= 5) {
                    setName.setError(Babylon.get().getFromResources("inviter_runtime_name2short")); //ID length check
                } else if (password.length() <= 5) {
                    setPassword.setError(Babylon.get().getFromResources("inviter_runtime_password2short")); //password length check
                } else {
                    FireBaser.checkIfUserExists(name, new FireBaser.OnBooleanResult() {
                        @Override
                        public void onResult(boolean result) {
                            if (!result) {
                                authenticate(email, password, name, inviter);
                            } else {
                                setName.setError(Babylon.get().getFromResources("inviter_runtime_nametaken"));
                            }
                        }
                    });
                }
            }
        });


        TextView getWelcome = (TextView) findViewById(R.id.login_welcome);
        getWelcome.setText(Babylon.get().getFromResources("inviter_login_welcome"));

        TextView getNameText = (TextView) findViewById(R.id.login_getuid_text);
        getNameText.setText(Babylon.get().getFromResources("inviter_login_getuid"));
        EditText getName = (EditText) findViewById(R.id.login_getuid);
        getName.setHint(Babylon.get().getFromResources("inviter_login_getuid_hint"));

        TextView getPasswordText = (TextView) findViewById(R.id.login_getpassword_text);
        getPasswordText.setText(Babylon.get().getFromResources("inviter_login_getpassword"));
        EditText getPassword = (EditText) findViewById(R.id.login_getpassword);
        getPassword.setHint(Babylon.get().getFromResources("inviter_login_getpassword_hint"));

        Button getButton = (Button) findViewById(R.id.login_submittbutton);
        getButton.setText(Babylon.get().getFromResources("inviter_login_button"));
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = getName.getText().toString();
                final String password = getPassword.getText().toString();

                if (name.equals("")) {
                    getName.setError(Babylon.get().getFromResources("inviter_runtime_mustbefilled"));
                } else if (password.equals("")) {
                    getPassword.setError(Babylon.get().getFromResources("inviter_runtime_mustbefilled"));
                } else if (name.contains("@") && name.contains(".")) {

                    FireBaser.findIdByEmail(name, new FireBaser.OnStringResult() {
                        @Override
                        public void onResult(final String id) {
                            if (!id.equals("")) {
                                login(id, name, password);
                            } else {
                                Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_error_name"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_error_email"), Toast.LENGTH_SHORT).show();
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
                    if (InDev.isDeveloper()) Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_superaccess"), Toast.LENGTH_SHORT).show();
                    FireBaser.loadBonus(PXL610.user_name());
                    Authentication.this.dismiss();
                    Game.scene().add(new WndSettings(false));
                } else {
                    Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_error_passwordincorrect"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_error_emailexists"), Toast.LENGTH_SHORT).show();
                } else {
                    PXL610.user_name(name);
                    InDev.loadSuperuserName();
                    FireBaser.updateUser(name, email);
                    if (InDev.isDeveloper()) Toast.makeText(Authentication.this.getContext(), Babylon.get().getFromResources("inviter_superaccess"), Toast.LENGTH_SHORT).show();
                    FireBaser.invite(inviter, new FireBaser.OnVoidResult() {
                        @Override
                        public void onResult() {
                            Authentication.this.dismiss();
                            Game.scene().add(new WndSettings(false));

                            FireBaser.loadBonus(PXL610.user_name());
                        }
                    });
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (main.getChildAt(0).getVisibility() == View.VISIBLE) {
            this.dismiss();
            Game.scene().add(new WndSettings(false));
        } else {
            main.getChildAt(0).setVisibility(View.VISIBLE);
            main.getChildAt(1).setVisibility(View.GONE);
            main.getChildAt(2).setVisibility(View.GONE);
        }
    }

    private char symbolCatch(String name) {
        char wrongchar = 'p';
        for (int i = 0; i < name.length(); i++) {
            if (BitmapText.Font.FULL.indexOf(name.charAt(i)) == -1) {
                wrongchar = name.charAt(i);
            }
        }
        return wrongchar;
    }

    private char strangeSymbolCatch(String name) {
        char wrongchar = 'p';
        if (name.contains(".")) {
            wrongchar = '.';
        } else if (name.contains("#")) {
            wrongchar = '#';
        } else if (name.contains("$")) {
            wrongchar = '$';
        } else if (name.contains("[")) {
            wrongchar = '[';
        } else if (name.contains("]")) {
            wrongchar = ']';
        } else if (name.contains("@")) {
            wrongchar = '@';
        }
        return wrongchar;
    }
}
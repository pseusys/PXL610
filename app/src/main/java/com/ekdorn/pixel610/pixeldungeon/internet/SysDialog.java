package com.ekdorn.pixel610.pixeldungeon.internet;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.windows.WndSettings;

public class SysDialog {
    public static void createInviteAdd() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable dialog = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Game.instance);
                builder.setTitle(Babylon.get().getFromResources("inviter_addinvites"));
                builder.setCancelable(false);

                final EditText input = new EditText(Game.instance);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText("-");
                input.setSelection(input.getText().length());
                input.setFocusable(true);
                builder.setView(input);

                builder.setPositiveButton(Babylon.get().getFromResources("inviter_addinvites"), null);
                final AlertDialog act = builder.show();
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                input.requestFocus();
                act.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!input.getText().toString().equals("")) {
                            String pseudoname = input.getText().toString();
                            try {
                                Integer i = Integer.parseInt(pseudoname);
                                FireBaser.invite(PXL610.user_name(), i, new FireBaser.OnVoidResult() {
                                    @Override
                                    public void onResult() {
                                        act.dismiss();
                                        Game.scene().add(new WndSettings(false));
                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_notnumber"), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            act.dismiss();
                            Game.scene().add(new WndSettings(false));
                        }
                    }
                });
            }
        };
        mainHandler.post(dialog);
    }
}

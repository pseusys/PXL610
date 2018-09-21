package com.ekdorn.pixel610.pixeldungeon.windows;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.internet.InDev;
import com.ekdorn.pixel610.pixeldungeon.internet.Inviter;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class SysDialog {
    public static void createNameWrite(boolean onLoad) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable dialog = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Game.instance);
                builder.setTitle(Babylon.get().getFromResources("name_change_dialog_title"));
                builder.setCancelable(false);

                final EditText input = new EditText(Game.instance);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(PXL610.user_name());
                input.setSelection(input.getText().length());
                input.setFocusable(true);
                builder.setView(input);

                builder.setPositiveButton(Babylon.get().getFromResources("name_change_dialog_agreed"), null);
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
                            boolean nameSuggest = true;
                            char wrongChar = 'I';
                            for (int i = 0; i < pseudoname.length(); i++) {
                                if (!BitmapText.Font.FULL.contains(Character.toString(pseudoname.charAt(i)))) {
                                    nameSuggest = false;
                                    wrongChar = pseudoname.charAt(i);
                                }
                            }
                            if (nameSuggest) {
                                PXL610.user_name(pseudoname);
                                if (InDev.isDeveloper()) Toast.makeText(Game.instance, Babylon.get().getFromResources("super_access"), Toast.LENGTH_SHORT).show();
                                if (onLoad) {
                                    act.dismiss();
                                } else {
                                    act.dismiss();
                                    Game.scene().add(new WndSettings(false));
                                }
                            } else {
                                Toast.makeText(Game.instance, Utils.format(Babylon.get().getFromResources("name_change_dialog_error"), Character.toString(wrongChar)), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        mainHandler.post(dialog);
    }

    public static void createInviteWrite() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable dialog = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Game.instance);
                builder.setTitle(Babylon.get().getFromResources("inviter_write_firstofall"));
                builder.setMessage(Babylon.get().getFromResources("inviter_write_empty"));
                builder.setCancelable(false);

                final EditText input = new EditText(Game.instance);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText("");
                input.setSelection(input.getText().length());
                input.setFocusable(true);
                builder.setView(input);

                builder.setPositiveButton(Babylon.get().getFromResources("inviter_write_enter"), null);
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
                            if (pseudoname.startsWith(Inviter.prefix)) {
                                Inviter.invite(pseudoname, new Inviter.OnTransactionSuccess() {
                                    @Override
                                    public void OnSuccess() {
                                        act.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(Game.instance, Babylon.get().getFromResources("dialog_wrongformat"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            PXL610.invited(false);
                            act.dismiss();
                        }
                    }
                });
            }
        };
        mainHandler.post(dialog);
    }

    public static void createInviteAdd() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable dialog = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Game.instance);
                builder.setTitle(Babylon.get().getFromResources("settings_addinvites"));
                builder.setCancelable(false);

                final EditText input = new EditText(Game.instance);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText("-");
                input.setSelection(input.getText().length());
                input.setFocusable(true);
                builder.setView(input);

                builder.setPositiveButton(Babylon.get().getFromResources("settings_add_invites"), null);
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
                                Inviter.invite(PXL610.user_id(), i, new Inviter.OnTransactionSuccess() {
                                    @Override
                                    public void OnSuccess() {
                                        act.dismiss();
                                        Game.scene().add(new WndSettings(false));
                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(Game.instance, Babylon.get().getFromResources("settings_notnumber"), Toast.LENGTH_SHORT).show();
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

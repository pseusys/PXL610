package com.ekdorn.pixel610.pixeldungeon.internet;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Random;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class Inviter {
    private static final String COLLECTION  = "users";
    private static final String INVITATIONS    = "invitations";

    private static int length = 8;
    private static final String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLBNOPQRSTUVWXYZ";
    public static final String prefix = "user:";

    private static ListenerRegistration lr;

    public static String updateID() {
        StringBuilder s = new StringBuilder();
        s.append(prefix);
        for (int i = 0; i < length; i++) {
            s.append(alphabet.charAt(Random.NormalIntRange(0, alphabet.length()-1)));
        }
        return s.toString();
    }

    public static void publishID(String id) {
        Map<String, Integer> data = new HashMap<>();
        data.put(INVITATIONS, 0);
        createDB().collection(COLLECTION).document(id).set(data);
    }

    public static void invite(String code) {
        if (PXL610.invited()) {
            Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_already_invited"), Toast.LENGTH_SHORT).show();
        } else if (code.equals(PXL610.user_id())) {
            Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_selfinvite"), Toast.LENGTH_SHORT).show();
        } else {
            createDB().collection(COLLECTION).document(code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        long total = ((Number) task.getResult().get(INVITATIONS)).intValue() + 1;

                        Map<String, Object> data = new HashMap<>();
                        data.put(INVITATIONS, total);

                        createDB().collection(COLLECTION).document(code)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Object>() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Log.d("TAG", "DocumentSnapshot added with ID: " + o);
                                        PXL610.invited(true);
                                        Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_invited"), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error adding document", e);
                                        Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_error"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                        Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_notvalid_code"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void loadBonus(String id) {
        lr = createDB().collection(COLLECTION).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Current data: " + snapshot.getData());
                    int bonus = ((Number) snapshot.get(INVITATIONS)).intValue();
                    PXL610.bonus(bonus);
                    Toast.makeText(Game.instance, Utils.format(Babylon.get().getFromResources("inviter_code_used"), bonus), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
    }

    public static void unloadBonus() {
        lr.remove();
    }

    public static int idLength() {
        return length + prefix.length();
    }

    private static FirebaseFirestore createDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        return db;
    }
}

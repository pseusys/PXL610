package com.ekdorn.pixel610.pixeldungeon.internet;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class FireBaser {
    public static final String USER_REF = "users";
    public static final String EMAIL_REF = "email";
    private static final String INVITATIONS = "invitations";

    // SEARCHING DATA:

    public static void checkIfUserExists(String id, final OnBooleanResult result) {
        createDB().collection(USER_REF).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()) {
                    result.onResult(false);
                } else {
                    result.onResult(true);
                }
            }
        });
    }

    public static void findIdByEmail(String email, final OnStringResult result) {
        createDB().collection(USER_REF).whereEqualTo(EMAIL_REF, email).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (!querySnapshot.isEmpty()) {
                    result.onResult(querySnapshot.getDocuments().get(0).getId());
                } else {
                    result.onResult("");
                }
            }
        });
    }

    public static void findEmailById(String id, final OnStringResult result) {
        createDB().collection(USER_REF).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    result.onResult(documentSnapshot.get(EMAIL_REF).toString());
                } else {
                    result.onResult("");
                }
            }
        });
    }

    // WORKING WITH USER DATA:

    public static void updateUser(String id, String email) {
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put(INVITATIONS, 0);
        data.put(EMAIL_REF, email);
        ff.collection(USER_REF).document(id).set(data);
    }

    // INVITING

    public static void invite(String code, OnVoidResult result) {
        if (PXL610.invited()) {
            Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_already_invited"), Toast.LENGTH_SHORT).show();
        } else if (code.equals(PXL610.user_name())) {
            Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_selfinvite"), Toast.LENGTH_SHORT).show();
        } else if (!code.equals("")) {
            invite(code, 1, result);
        } else {
            result.onResult();
        }
    }

    public static void invite(String code, int number, OnVoidResult result) {
        DocumentReference sfDocRef = createDB().collection(USER_REF).document(code);
        createDB().runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                long total = ((Number) snapshot.get(INVITATIONS)).intValue() + number;
                Map<String, Object> data = new HashMap<>();
                data.put(INVITATIONS, total);

                transaction.update(sfDocRef, data);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "Transaction success!");
                PXL610.invited(true);
                result.onResult();
                if (code.equals(PXL610.user_name())) {
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_adding_done"), Toast.LENGTH_SHORT).show();
                    loadBonus(PXL610.user_name());
                } else {
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_invited"), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Transaction failure.", e);
                if (code.equals(PXL610.user_name())) {
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_error"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_notvalid_code"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void loadBonus(String id) {
        createDB().collection(USER_REF).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    int bonus = ((Number) task.getResult().get(INVITATIONS)).intValue();
                    PXL610.bonus(bonus);
                    Toast.makeText(Game.instance, Utils.format(Babylon.get().getFromResources("inviter_code_used"), bonus), Toast.LENGTH_SHORT).show();
                } else {
                    Log.w("TAG", "Error getting documents.", task.getException());
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("inviter_error"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static FirebaseFirestore createDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        return db;
    }


    public interface OnVoidResult {
        void onResult();
    }

    public interface OnStringResult {
        void onResult(String result);
    }

    public interface OnBooleanResult {
        void onResult(boolean result);
    }
}

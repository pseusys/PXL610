package com.ekdorn.pixel610.pixeldungeon.internet;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class InDev {
    public static final String developer_key = "ekdornadmin";
    private static final String COLLECTION  = "superusers";
    private static final String DOCUMENT  = "admin";
    private static final String NAME    = "name";

    public static boolean isDeveloper() {
        return (PXL610.user_name().equals(PXL610.superuser_name()));
    }

    public static void loadSuperuserName() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection(COLLECTION).document(DOCUMENT).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    PXL610.superuser_name((String) task.getResult().get(NAME));
                } else {
                    Log.w("TAG", "Error getting documents.", task.getException());
                    Toast.makeText(Game.instance, Babylon.get().getFromResources("indev_error"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

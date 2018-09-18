/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 EK DORN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ekdorn.pixel610.classicdungeon;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OnlineRatinger {
    INSTANCE;

    private static final String COLLECTION  = "ranks";
    private static final String RANK_DOCUMENT    = "five_winners_%s";
    private static final String SYS_DOCUMENT    = "stats";

    private static final String NUMBER    = "number";
    private static final String TOTAL    = "total";

    public static final String CLASS        = "class";
    public static final String INFO         = "info";
    public static final String WIN		    = "win";
    public static final String SCORE	    = "score";
    public static final String LEVEL        = "level";
    public static final String DURATION     = "duration";
    public static final String TIER         = "tier";

    public static final String SENDER       = "sender";
    public static final String ID           = "id";

    private List<Map<String, Object>> topData;
    private long bestGlobal;
    private long countGlobal;
    private FirebaseFirestore db;

    private OnLoadedListener lister;

    OnlineRatinger() {
        topData = new ArrayList<Map<String, Object>>();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }


    // PUSH LINE //

    public static void send(boolean win) {
        check(createRate(win), 0);
    }

    private static void check(final Map<String, Object> rec, final int repet) {
        INSTANCE.db.collection(COLLECTION).document(Utils.format(RANK_DOCUMENT, repet))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                if (((Number) (task.getResult().getData().get(SCORE))).longValue() < ((Number) (rec.get(SCORE))).longValue()) {
                                    Log.e("TAG", "onComplete: Exchanged with score number " + repet);
                                    push(rec, repet);
                                    if (repet < 5) check(task.getResult().getData(), repet + 1);
                                } else if (repet < 5) {
                                    check(rec, repet + 1);
                                }
                            } else {
                                push(rec, repet);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private static void push(final Map<String, Object> rec, int place) {
        INSTANCE.db.collection(COLLECTION).document(Utils.format(RANK_DOCUMENT, place))
                .set( rec )
                .addOnSuccessListener(new OnSuccessListener<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + o);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

        INSTANCE.db.collection(COLLECTION).document(SYS_DOCUMENT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            long total = ((Number) rec.get(SCORE)).longValue();
                            long number = 1L;
                            if (task.getResult().exists()) {
                                total = ((Number) task.getResult().getData().get(TOTAL)).longValue() + ((Number) rec.get(SCORE)).longValue();
                                number = ((Number) task.getResult().getData().get(NUMBER)).intValue() + 1L;
                            }
                            Map<String, Object> sys = new HashMap<>();
                            sys.put(TOTAL, total);
                            sys.put(NUMBER, number);

                            INSTANCE.db.collection(COLLECTION).document(Utils.format(SYS_DOCUMENT))
                                    .set( sys )
                                    .addOnSuccessListener(new OnSuccessListener<Object>() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Log.d("TAG", "DocumentSnapshot added with ID: " + o);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error adding document", e);
                                        }
                                    });

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // GET //

    public void setListener(OnLoadedListener listener) {
        this.lister = listener;
    }

    public void get() {
        topData = new ArrayList<Map<String, Object>>();
        bestGlobal = countGlobal = 0;

        for (int i = 0; i < 7; i++) {
            db.collection(COLLECTION).document(Utils.format(RANK_DOCUMENT, i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean exists = task.getResult().exists();
                                if (exists && topData.size() < 6) {
                                    topData.add(task.getResult().getData());
                                    System.out.println("gotit");
                                } else {
                                    lister.scream();
                                    System.out.println("screaming");
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

        db.collection(COLLECTION).document(SYS_DOCUMENT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = task.getResult().exists();
                            if (exists) {
                                countGlobal = ((Number) task.getResult().getData().get(NUMBER)).longValue();
                                bestGlobal = ((Number) task.getResult().getData().get(TOTAL)).longValue() / countGlobal;
                            }
                            lister.cry();
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public List<Map<String, Object>> getTopData() {
        return INSTANCE.topData;
    }

    public long getBestGlobal() {
        return INSTANCE.bestGlobal;
    }

    public long getCountGlobal() {
        return INSTANCE.countGlobal;
    }


    public interface OnLoadedListener {
        public void scream();
        public void cry();
    }

    // CREATE //

    private static Map<String, Object> createRate(boolean win) {
        Map<String, Object> rate = new HashMap<>();

        String heroClass = Dungeon.hero.heroClass.tag();

        rate.put(CLASS, heroClass);
        rate.put(INFO, Dungeon.resultDescription);
        rate.put(WIN, win);
        rate.put(SCORE, Rankings.score(win));
        rate.put(LEVEL, Dungeon.hero.lvl);
        rate.put(DURATION, Statistics.duration);
        rate.put(TIER, Dungeon.hero.tier());
        rate.put(SENDER, PXL610.user_name());
        rate.put(ID, PXL610.user_id());

        return rate;
    }
}

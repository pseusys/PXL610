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

package com.ekdorn.pixel610.pixeldungeon;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OnlineRatinger {
    INSTANCE;

    private static final String mage = "mage";
    private static final String warrior = "warrior";
    private static final String rogue = "rogue";
    private static final String huntress = "huntress";

    private static final String COLLECTION  = "ranks";
    private static final String DOCUMENT    = "five_winners_%s";

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

    private OnLoadedListener lister;

    OnlineRatinger() {
        topData = new ArrayList<Map<String, Object>>();
    }


    // PUSH LINE //

    public static void send(boolean win) {
        check(createRate(win), 0);
    }

    private static void check(final Map<String, Object> rec, final int repet) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION).document(Utils.format(DOCUMENT, repet))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println(task.getResult().getData().get(SCORE).getClass() + "\n" + rec.get(SCORE).getClass());
                            if (((Number) (task.getResult().getData().get(SCORE))).longValue() < ((Number) (rec.get(SCORE))).longValue()) {
                                Log.e("TAG", "onComplete: Exchanged with score number " + repet );
                                push(rec, repet);
                                if (repet < 5) check(task.getResult().getData(), repet+1);
                            } else if (repet < 5) {
                                check(rec, repet+1);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private static void push(Map<String, Object> rec, int place) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION).document(Utils.format(DOCUMENT, place))
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
    }

    // GET //

    public void setListener(OnLoadedListener listener) {
        this.lister = listener;
    }

    public void get() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i < 6; i++) {
            db.collection(COLLECTION).document(Utils.format(DOCUMENT, i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                INSTANCE.topData.add(task.getResult().getData());
                                if (INSTANCE.topData.size() == 6) {
                                    lister.scream(INSTANCE.topData);
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }


    public interface OnLoadedListener {
        public void scream(List<Map<String, Object>> data);
    }

    // CREATE //

    private static Map<String, Object> createRate(boolean win) {
        Map<String, Object> rate = new HashMap<>();

        String heroClass = "";
        switch (Dungeon.hero.heroClass) {
            case MAGE: heroClass = mage; break;
            case ROGUE: heroClass = rogue; break;
            case WARRIOR: heroClass = warrior; break;
            case HUNTRESS: heroClass = huntress; break;
        }

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

    private static final Comparator<Rankings.Record> scoreComparator = new Comparator<Rankings.Record>() {
        @Override
        public int compare(Rankings.Record lhs, Rankings.Record rhs ) {
            return (int)Math.signum( rhs.score - lhs.score );
        }
    };

    // OTHER //
    public static HeroClass getClassById(String id) {
        switch(id) {
            case mage:
                return HeroClass.MAGE;
            case warrior:
                return HeroClass.WARRIOR;
            case rogue:
                return HeroClass. ROGUE;
            case huntress:
                return HeroClass.HUNTRESS;
            default:
                return null;
        }
    }
}

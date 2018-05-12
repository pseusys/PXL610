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

import android.util.Log;

import com.ekdorn.pixel610.pixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by User on 13.02.2018.
 */

public class Babylon {

    private static final String RESOURCE_NAME = "strings";

    private static Babylon instance;

    private HashMap<String, String> resources;
    private ArrayList<Locale> localisations = new ArrayList<>();
    private Locale inUse;

    public static Babylon get() {
        if (instance == null) {
            instance = new Babylon();
        }
        return instance;
    }

    private Babylon() {
        localisations.add(Locale.ENGLISH);
        localisations.add(new Locale("ru"));
        localisations.add(Locale.GERMAN);
        localisations.add(new Locale("la"));

        String lock = PXL610.localisation();

        for (int i = 0; i < localisations.size(); i++) {
            if (lock.equals(localisations.get(i).getLanguage())) inUse = localisations.get(i);
        }

        load();
    }

    public void load() {
        Log.e("TAG", "load: " + inUse );
        resources = new HashMap<>();
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_NAME, inUse);
        Enumeration<String> keys = bundle.getKeys();

        try {
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = bundle.getString(key);

                resources.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("lol not loaded");
        }
    }

    public void updateLocale() {
        Locale current = Locale.getDefault();

        if (localisations.contains(current)) {
            PXL610.localisation(current.getLanguage());
            inUse = current;
        } else {
            PXL610.localisation(Locale.ENGLISH.getLanguage());
            inUse = Locale.ENGLISH;
        }
        GLog.i("resources loaded");
    }

    public void changeLocale(int loc) {
        int current = localisations.indexOf(inUse);

        current += loc;
        if (current > 1 /*3 in finale*/) {
            current = 0;
        } else if (current < 0) {
            current = 1 /*0 in finale*/;
        }

        inUse = localisations.get(current);
        PXL610.localisation(localisations.get(current).getLanguage());

        load();
    }

    public String getFromResources(String tag) {
        return resources.get(tag);
    }

    public String getLanguageName() {
        return getFromResources("language_name");
    }

    public Locale getCurrent() {
        return inUse;
    }
}

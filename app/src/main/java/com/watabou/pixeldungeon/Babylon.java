package com.watabou.pixeldungeon;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.watabou.pixeldungeon.utils.GLog;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
        resources = new HashMap<>();
        Enumeration<String> keys = ResourceBundle.getBundle(RESOURCE_NAME).getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            resources.put(key, ResourceBundle.getBundle(RESOURCE_NAME).getString(key));
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
        if (current > 3) {
            current = 0;
        } else if (current < 0) {
            current = 3;
        }

        inUse = localisations.get(current);
        PXL610.localisation(localisations.get(current).getLanguage());

        if ((current == 2) || (current == 3)) {
            Toast.makeText(PXL610.instance.getApplicationContext(), "This language's coming soon", Toast.LENGTH_SHORT).show();
        }
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

package com.ekdorn.pixel610.pixeldungeon;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.scenes.TitleScene;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.windows.WndError;
import com.ekdorn.pixel610.pixeldungeon.windows.WndSaver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
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

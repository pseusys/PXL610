package com.watabou.pixeldungeon;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by User on 13.02.2018.
 */

public class Babylon{
    private static Babylon instance;

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
    }

    public void updateLocale() {
        Locale current = Locale.getDefault();

        if (localisations.contains(current)) {
            PXL610.localisation(current.getLanguage());
            inUse = current;
        } else {
            PXL610.localisation(Locale.ENGLISH.getLanguage());
            inUse = Locale.ENGLISH; //
        }
    }

    public String changeLocale(int loc) {
        int current = localisations.indexOf(inUse);

        current += loc;
        if (current > 3) {
            current = 0;
        } else if (current < 0) {
            current = 3;
        }

        inUse = localisations.get(current);
        PXL610.localisation(localisations.get(current).getLanguage());
        return "language_name";
    }

    public String getFromResources(String tag) {
        return ResourceBundle.getBundle("strings", inUse).getString(tag);
    }

    public String getLanguageName() {
        return ResourceBundle.getBundle("strings", inUse).getString("language_name");
    }

    public Locale getCurrent() {
        return inUse;
    }
}

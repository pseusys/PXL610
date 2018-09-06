package com.ekdorn.pixel610.pixeldungeon.additional;

import com.ekdorn.pixel610.pixeldungeon.Babylon;

public enum GameMode {
    WARRIOR(Babylon.get().getFromResources("hero_cl_warrior")),
    MAGE(Babylon.get().getFromResources("hero_cl_mage")),
    ROGUE(Babylon.get().getFromResources("hero_cl_rogue")),
    HUNTRESS(Babylon.get().getFromResources("hero_cl_huntress"));

    private String title;

    private GameMode(String title ) {
        this.title = title;
    }

    public void switchMode() {

    }
}

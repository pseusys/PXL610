package com.ekdorn.pixel610.pixeldungeon;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.pixeldungeon.windows.WndSaver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Updater {
    public static void update(Context cntxt) {
        Toast.makeText(cntxt, Babylon.get().getFromResources("updater_syncing"), Toast.LENGTH_SHORT).show();
        //version 1.2 deepening

        /*File [] files = new File(cntxt.getApplicationInfo().dataDir + "/files").listFiles();
        for (int i = 0; i < files.length; i++) {
            Log.e("FILES", "transferGames: " + files[i] );
        }*/

        transferGames();
        // version...
        Toast.makeText(cntxt, Babylon.get().getFromResources("updater_synced"), Toast.LENGTH_SHORT).show();
    }

    private static void transferGames() {
        String [] OLD_AUTOSAVES = new String [] {"warrior.dat", "mage.dat", "game.dat", "ranger.dat"};
        String [] OLD_AUTODEPTHS	= new String [] {"warrior%d.dat", "mage%d.dat","depth%d.dat", "ranger%d.dat"};

        String [] OLD_SAVES	= new String [] {"warrior_saveslot%d.dat", "mage_saveslot%d.dat", "rogue_saveslot%d.dat", "huntress_saveslot%d.dat"};
        String [] OLD_DEPTHS	= new String [] {"warrior_saveslot%s_depthcopy%d.dat", "mage_saveslot%s_depthcopy%d.dat", "rogue_saveslot%s_depthcopy%d.dat", "huntress_saveslot%s_depthcopy%d.dat"};

        for (int i = 0; i < OLD_AUTOSAVES.length; i++) {
            try {
                WndSaver.copy(OLD_AUTOSAVES[i], Dungeon.gameFile(HeroClass.values()[i]));
                File f = new File(OLD_AUTOSAVES[i]);
                f.delete();
                for (int j = 0; j < Dungeon.gameBundle(OLD_AUTOSAVES[i]).getInt( Dungeon.DEPTH ); j++) {
                    Log.e("TAG", "transferGames: " + Dungeon.depthFile(HeroClass.values()[i]));
                    WndSaver.copy(Utils.format( OLD_AUTODEPTHS[i], j ),
                            Utils.format( Dungeon.depthFile(HeroClass.values()[i]), j ));
                    File g = new File(Utils.format( OLD_AUTODEPTHS[i], j ));
                    g.delete();
                }
            } catch (Exception e) {
                Log.d("TAG", "transferGames: " + e.getMessage());
            }
        }
        for (int i = 0; i < OLD_SAVES.length; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    WndSaver.copy(Utils.format( OLD_SAVES[i], j), WndSaver.gameFile(HeroClass.values()[i], j));
                    File f = new File(Utils.format( OLD_SAVES[i], j));
                    f.delete();
                    for (int h = 0; h < Dungeon.gameBundle(Utils.format( OLD_SAVES[i], j)).getInt( Dungeon.DEPTH ); h++) {
                        WndSaver.copy(Utils.format( OLD_DEPTHS[i], j , h ), WndSaver.depthFile(HeroClass.values()[i], j, h));
                        File g = new File(Utils.format( OLD_DEPTHS[i], j , h ));
                        g.delete();
                    }
                } catch (Exception e) {
                    Log.d("TAG", "transferGames: " + e.getMessage());
                }
            }
        }
    }

}

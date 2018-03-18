package com.watabou.pixeldungeon.windows;

import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Babylon;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.GamesInProgress;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.scenes.StartScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.Utils;

import java.io.File;

/**
 * Created by User on 17.03.2018.
 */

public class WndSaver extends Window {
    public static final String [] WR = {"warrior_saveslot1.dat", "warrior_saveslot2.dat", "warrior_saveslot3.dat"};

    public static final String [] MG = {"mage_saveslot1.dat", "mage_saveslot2.dat", "mage_saveslot3.dat"};

    public static final String [] RG = {"rogue_saveslot1.dat", "rogue_saveslot2.dat", "rogue_saveslot3.dat"};

    public static final String [] HN = {"huntress_saveslot1.dat", "huntress_saveslot2.dat", "huntress_saveslot3.dat"};

    private static final int WIDTH		= 112;
    private static final int BTN_HEIGHT	= 20;
    private static final int GAP 		= 2;

    private float pos;

    private StartScene.GameButton lastPlayed;

    private static String gameFile(HeroClass cl, int index) {
        switch (cl) {
            case WARRIOR:
                return WR[index];
            case MAGE:
                return MG[index];
            case HUNTRESS:
                return HN[index];
            default:
                return RG[index];
        }
    }

    public WndSaver(String title, final boolean toSave, final HeroClass cl, final boolean inGame) {
        super();

        BitmapTextMultiline tfTitle = PixelScene.createMultiline( title, 9 );
        tfTitle.hardlight( TITLE_COLOR );
        tfTitle.x = tfTitle.y = GAP;
        tfTitle.maxWidth = WIDTH - GAP * 2;
        tfTitle.measure();
        add( tfTitle );

        if (inGame) {
            BitmapTextMultiline tfMesage = PixelScene.createMultiline("NB! Your current progress will be autosaved.", 8);
            tfMesage.maxWidth = WIDTH - GAP * 2;
            tfMesage.measure();
            tfMesage.x = GAP;
            tfMesage.y = tfTitle.y + tfTitle.height() + GAP;
            add(tfMesage);

            pos = tfMesage.y + tfMesage.height() + GAP;
        } else {
            pos = tfTitle.y + tfTitle.height() + GAP;
        }

        lastPlayed = new StartScene.GameButton("Autosave - your last game") {
            @Override
            protected void onClick() {
                hide();
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                InterlevelScene.loadingFileName = Dungeon.gameFile(cl);
                Game.switchScene( InterlevelScene.class );
            }
        };
        GamesInProgress.Info asinfo = GamesInProgress.check(Dungeon.gameFile(cl));
        if (asinfo != null) {
            lastPlayed.secondary( Utils.format( Babylon.get().getFromResources("startscene_depth"), asinfo.depth, asinfo.level ), asinfo.challenges );
        } else {
            lastPlayed.secondary( null, false );
        }
        lastPlayed.setRect(GAP, pos, WIDTH - GAP * 2, BTN_HEIGHT);
        add(lastPlayed);
        if (toSave) {
            lastPlayed.enable(false);
            //lastPlayed.visible = false; //TODO: resize, make invisible;
        }
        pos += BTN_HEIGHT + GAP;

        for (int i = 0; i < 3; i++) {
            final int index = i;

            StartScene.GameButton slot = new StartScene.GameButton("Game save slot " + index) {
                @Override
                protected void onClick() {
                    if (!toSave) {

                        if (inGame) {
                            try {
                                Dungeon.saveAll();
                            } catch (Exception e) {
                                // not saved so what?
                            }
                        }
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        InterlevelScene.loadingFileName = WndSaver.gameFile(cl, index);
                        Game.switchScene(InterlevelScene.class);

                    } else {

                        try {
                            Actor.fixTime();
                            Dungeon.saveGame(WndSaver.gameFile(cl, index));
                            Dungeon.saveLevel();
                            GamesInProgress.set(Dungeon.hero.heroClass, Dungeon.depth, Dungeon.hero.lvl, Dungeon.challenges != 0);
                        } catch (Exception e) {
                            add( new WndError( "can not be saved" ) {
                                public void onBackPressed() {
                                    super.onBackPressed();
                                    this.hide();
                                    WndSaver.this.hide();
                                }
                            } );
                        }
                        WndSaver.this.hide();
                    }
                }
            };
            GamesInProgress.Info slinfo = GamesInProgress.check(WndSaver.gameFile(cl, index));
            if (slinfo != null) {
                slot.secondary(Utils.format(Babylon.get().getFromResources("startscene_depth"), slinfo.depth, slinfo.level), slinfo.challenges);
            } else {
                slot.secondary(null, false);
                if (!toSave) slot.enable(false);
            }
            slot.setRect(GAP, pos, WIDTH - GAP * 2, BTN_HEIGHT);
            add(slot);

            //File save = Game.instance.getFileStreamPath(WndSaver.gameFile(cl, index));
            //if (!save.exists() && !toSave) {
            //}

            pos += BTN_HEIGHT + GAP;
        }

        resize(WIDTH, (int) pos);
    }
}

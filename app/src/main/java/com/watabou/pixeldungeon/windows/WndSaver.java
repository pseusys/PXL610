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
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by User on 17.03.2018.
 */

public class WndSaver extends Window {
    private static final String WR = "warrior_saveslot%d.dat";
    private static final String WR_D = "warrior_saveslot%s_depthcopy%d.dat";

    private static final String MG = "mage_saveslot%d.dat";
    private static final String MG_D = "mage_saveslot%s_depthcopy%d.dat";

    private static final String RG = "rogue_saveslot%d.dat";
    private static final String RG_D = "rogue_saveslot%s_depthcopy%d.dat";

    private static final String HN = "huntress_saveslot%d.dat";
    private static final String HN_D = "huntress_saveslot%s_depthcopy%d.dat";

    private static final int WIDTH		= 112;
    private static final int BTN_HEIGHT	= 20;
    private static final int GAP 		= 2;

    private float pos;

    private StartScene.GameButton lastPlayed;

    private static String gameFile(HeroClass cl, int index) {
        switch (cl) {
            case WARRIOR:
                return Utils.format(WR, index);
            case MAGE:
                return Utils.format(MG, index);
            case HUNTRESS:
                return Utils.format(HN, index);
            default:
                return Utils.format(RG, index);
        }
    }

    public static String depthFile( HeroClass cl, int slot, int index ) {
        switch (cl) {
            case WARRIOR:
                return Utils.format(WR_D, slot, index);
            case MAGE:
                return Utils.format(MG_D, slot, index);
            case HUNTRESS:
                return Utils.format(HN_D, slot, index);
            default:
                return Utils.format(RG_D, slot, index);
        }
    }

    public void copy(String src, String dst) throws IOException {
        FileInputStream inStream = Game.instance.openFileInput(src);
        FileOutputStream outStream = Game.instance.openFileOutput(dst, Game.MODE_PRIVATE);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public WndSaver(String title, final boolean toSave, final HeroClass cl, final boolean inGame) {
        super();

        BitmapTextMultiline tfTitle = PixelScene.createMultiline( title, 9 );
        tfTitle.hardlight( TITLE_COLOR );
        tfTitle.x = tfTitle.y = GAP;
        tfTitle.maxWidth = WIDTH - GAP * 2;
        tfTitle.measure();
        add( tfTitle );

        if (inGame && !toSave) {
            BitmapTextMultiline tfMesage = PixelScene.createMultiline(Babylon.get().getFromResources("save_notabene"), 8);
            tfMesage.maxWidth = WIDTH - GAP * 2;
            tfMesage.measure();
            tfMesage.x = GAP;
            tfMesage.y = tfTitle.y + tfTitle.height() + GAP;
            add(tfMesage);

            pos = tfMesage.y + tfMesage.height() + GAP;
        } else {
            pos = tfTitle.y + tfTitle.height() + GAP;
        }


        final GamesInProgress.Info asinfo = GamesInProgress.check(Dungeon.gameFile(cl));
        lastPlayed = new StartScene.GameButton(Babylon.get().getFromResources("save_autosave")) {
            @Override
            protected void onClick() {
                hide();
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                InterlevelScene.loadingFileName = Dungeon.gameFile(cl);
                InterlevelScene.loadingFilePathName = Utils.format( Dungeon.depthFile( StartScene.curClass ), ((asinfo != null) ? asinfo.depth : 1) );
                Game.switchScene( InterlevelScene.class );
            }
        };
        if (asinfo != null) {
            lastPlayed.secondary( Utils.format( Babylon.get().getFromResources("startscene_depth"), asinfo.depth, asinfo.level ), asinfo.challenges );
        } else {
            lastPlayed.secondary( null, false );
        }
        lastPlayed.setRect(GAP, pos, WIDTH - GAP * 2, BTN_HEIGHT);
        add(lastPlayed);
        if (inGame) {
            lastPlayed.enable(false);
            lastPlayed.visible = false;
        } else {
            pos += BTN_HEIGHT + GAP;
        }


        for (int i = 0; i < 3; i++) {
            final int index = i;

            StartScene.GameButton slot = new StartScene.GameButton(Babylon.get().getFromResources("save_slot") + index) {
                @Override
                protected void onClick() {
                    if (!toSave) {

                        try {
                            int depth = GamesInProgress.check(WndSaver.gameFile(cl, index)).depth;

                            for (int j = 1; j < depth; j++) {
                                copy( WndSaver.depthFile(cl, index, j), Utils.format(Dungeon.depthFile(cl), j) );
                            }

                            InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                            InterlevelScene.loadingFileName = WndSaver.gameFile(cl, index);
                            InterlevelScene.loadingFilePathName = WndSaver.depthFile(cl, index, depth);
                            Game.switchScene(InterlevelScene.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                        add( new WndError(Babylon.get().getFromResources("save_err_notsaved")) {
                            public void onBackPressed() {
                                super.onBackPressed();
                                this.hide();
                                WndSaver.this.hide();
                            }
                        } );
                    }

                    } else {

                        try {
                            Actor.fixTime();
                            Dungeon.saveGame(WndSaver.gameFile(cl, index));
                            Dungeon.saveLevel();
                            for (int j = 1; j <= Dungeon.depth; j++) {
                                copy( Utils.format( Dungeon.depthFile( cl ), j ), WndSaver.depthFile( cl, index, j ) );
                            }
                            GamesInProgress.set(Dungeon.hero.heroClass, Dungeon.depth, Dungeon.hero.lvl, Dungeon.challenges != 0);

                        } catch (Exception e) {
                            e.printStackTrace();
                            add( new WndError(Babylon.get().getFromResources("save_err_notloaded")) {
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

            pos += BTN_HEIGHT + GAP;
        }

        resize(WIDTH, (int) pos);
    }
}

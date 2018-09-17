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

package com.ekdorn.pixel610.classicdungeon.windows;

import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.GamesInProgress;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.classicdungeon.scenes.InterlevelScene;
import com.ekdorn.pixel610.classicdungeon.scenes.PixelScene;
import com.ekdorn.pixel610.classicdungeon.scenes.StartScene;
import com.ekdorn.pixel610.classicdungeon.ui.Window;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;

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

    public static boolean saveCheck(HeroClass cl) {
        return (Game.instance.getFileStreamPath(Dungeon.gameFile(cl)).exists() ||
                Game.instance.getFileStreamPath(gameFile(cl, 0)).exists() ||
                Game.instance.getFileStreamPath(gameFile(cl, 1)).exists() ||
                Game.instance.getFileStreamPath(gameFile(cl, 2)).exists());
    }

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

        if (inGame && !toSave && Dungeon.hero.isAlive()) {
            //if (Dungeon.hero.isAlive()) {
                BitmapTextMultiline tfMesage = PixelScene.createMultiline(Babylon.get().getFromResources("save_notabene"), 8);
                tfMesage.maxWidth = WIDTH - GAP * 2;
                tfMesage.measure();
                tfMesage.x = GAP;
                tfMesage.y = tfTitle.y + tfTitle.height() + GAP;
                add(tfMesage);

                pos = tfMesage.y + tfMesage.height() + GAP;
            //}
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
            lastPlayed.enable(false);
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

            StartScene.GameButton slot = new StartScene.GameButton(Babylon.get().getFromResources("save_slot") + " " + index) {
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

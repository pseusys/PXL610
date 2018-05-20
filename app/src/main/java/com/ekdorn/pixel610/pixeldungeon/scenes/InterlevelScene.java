/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.ekdorn.pixel610.pixeldungeon.scenes;

import java.io.FileNotFoundException;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.audio.Music;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.Statistics;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.items.Generator;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.ui.GameLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.pixeldungeon.windows.WndError;
import com.ekdorn.pixel610.pixeldungeon.windows.WndStory;

public class InterlevelScene extends PixelScene {

	private static final float TIME_TO_FADE = 0.3f;
	
	public static enum Mode {
		DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, NONE
	};
	public static Mode mode;
	public static String loadingFileName; // added by EK DORN ent.
	public static String loadingFilePathName; // added by EK DORN ent.
	
	public static int returnDepth;
	public static int returnPos;
	
	public static boolean noStory = false;
	
	public static boolean fallIntoPit;
	
	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	};
	private Phase phase;
	private float timeLeft;
	
	private BitmapText message;
	
	private Thread thread;
	private String error = null;
	
	@Override
	public void create() {
		super.create();
		
		String text = "";
		switch (mode) {
		case DESCEND:
			text = Babylon.get().getFromResources("interlevelscene_descending");
			break;
		case ASCEND:
			text = Babylon.get().getFromResources("interlevelscene_ascending");
			break;
		case CONTINUE:
			text = Babylon.get().getFromResources("interlevelscene_loading");
			break;
		case RESURRECT:
			text = Babylon.get().getFromResources("interlevelscene_resurrecting");
			break;
		case RETURN:
			text = Babylon.get().getFromResources("interlevelscene_returning");
			break;
		case FALL:
			text = Babylon.get().getFromResources("interlevelscene_falling");
			break;
		default:
		}
		
		message = PixelScene.createText( text, 9 );
		message.measure();
		message.x = (Camera.main.width - message.width()) / 2; 
		message.y = (Camera.main.height - message.height()) / 2;
		add( message );
		
		phase = Phase.FADE_IN;
		timeLeft = TIME_TO_FADE;
		
		thread = new Thread() {
			@Override
			public void run() {
				
				try {
					
					Generator.reset();
					
					switch (mode) {
					case DESCEND:
						descend();
						break;
					case ASCEND:
						ascend();
						break;
					case CONTINUE:
						restore(loadingFileName, loadingFilePathName);
						break;
					case RESURRECT:
						resurrect();
						break;
					case RETURN:
						returnTo();
						break;
					case FALL:
						fall();
						break;
					default:
					}

					if ((Dungeon.depth % 5) == 0) {
						Sample.INSTANCE.load( Assets.SND_BOSS );
					}
					
				} catch (FileNotFoundException e) {

					error = Babylon.get().getFromResources("interlevelscene_err_filenotfound");

				} catch (Exception e ) {
					
					error = Babylon.get().getFromResources("interlevelscene_err_other");

				}
				
				if (phase == Phase.STATIC && error == null) {
					phase = Phase.FADE_OUT;
					timeLeft = TIME_TO_FADE;
				}
			}
		};
		thread.start();
	}
	
	@Override
	public void update() {
		super.update();
		
		float p = timeLeft / TIME_TO_FADE;
		
		switch (phase) {
		
		case FADE_IN:
			message.alpha( 1 - p );
			if ((timeLeft -= Game.elapsed) <= 0) {
				if (!thread.isAlive() && error == null) {
					phase = Phase.FADE_OUT;
					timeLeft = TIME_TO_FADE;
				} else {
					phase = Phase.STATIC;
				}
			}
			break;
			
		case FADE_OUT:
			message.alpha( p );
			if (mode == Mode.CONTINUE || (mode == Mode.DESCEND && Dungeon.depth == 1)) {
				Music.INSTANCE.volume( p );
			}
			if ((timeLeft -= Game.elapsed) <= 0) {
				Game.switchScene( GameScene.class );
			}
			break;
			
		case STATIC:
			if (error != null) {
				add( new WndError( error ) {
					public void onBackPressed() {
						super.onBackPressed();
						Game.switchScene( StartScene.class );
					};
				} );
				error = null;
			}
			break;
		}
	}
	
	private void descend() throws Exception {
		
		Actor.fixTime();
		if (Dungeon.hero == null) {
			Dungeon.init();
			if (noStory) {
				Dungeon.chapters.add( WndStory.ID_SEWERS );
				noStory = false;
			}
			GameLog.wipe();
		} else {
			Dungeon.saveLevel();
		}
		
		Level level;
		if (Dungeon.depth >= Statistics.deepestFloor) {
			level = Dungeon.newLevel();
		} else {
			Dungeon.depth++;
			level = Dungeon.loadLevel( Utils.format( Dungeon.depthFile( Dungeon.hero.heroClass ), Dungeon.depth ) );
		}
		Dungeon.switchLevel( level, level.entrance );
	}
	
	private void fall() throws Exception {
		
		Actor.fixTime();
		Dungeon.saveLevel();
		
		Level level;
		if (Dungeon.depth >= Statistics.deepestFloor) {
			level = Dungeon.newLevel();
		} else {
			Dungeon.depth++;
			level = Dungeon.loadLevel( Utils.format( Dungeon.depthFile( Dungeon.hero.heroClass ), Dungeon.depth ) );
		}
		Dungeon.switchLevel( level, fallIntoPit ? level.pitCell() : level.randomRespawnCell() );
	}
	
	private void ascend() throws Exception {
		Actor.fixTime();
		
		Dungeon.saveLevel();
		Dungeon.depth--;
		Level level = Dungeon.loadLevel( Utils.format( Dungeon.depthFile( Dungeon.hero.heroClass ), Dungeon.depth ) );
		Dungeon.switchLevel( level, level.exit );
	}
	
	private void returnTo() throws Exception {
		
		Actor.fixTime();
		
		Dungeon.saveLevel();
		Dungeon.depth = returnDepth;
		Level level = Dungeon.loadLevel( Utils.format( Dungeon.depthFile( Dungeon.hero.heroClass ), Dungeon.depth ) );
		Dungeon.switchLevel( level, Level.resizingNeeded ? level.adjustPos( returnPos ) : returnPos );
	}
	
	private void restore(String filename, String loadingFilePathName) throws Exception {
		
		Actor.fixTime();
		
		GameLog.wipe();
		
		Dungeon.loadGame( filename, true );
		if (Dungeon.depth == -1) {
			Dungeon.depth = Statistics.deepestFloor;
			Dungeon.switchLevel( Dungeon.loadLevel( loadingFilePathName ), -1 );
		} else {
			Level level = Dungeon.loadLevel( loadingFilePathName );
			Dungeon.switchLevel( level, Level.resizingNeeded ? level.adjustPos( Dungeon.hero.pos ) : Dungeon.hero.pos );
		}
	}
	
	private void resurrect() throws Exception {
		
		Actor.fixTime(); 
		
		if (Dungeon.bossLevel()) {
			Dungeon.hero.resurrect( Dungeon.depth );
			Dungeon.depth--;
			Level level = Dungeon.newLevel();
			Dungeon.switchLevel( level, level.entrance );
		} else {
			Dungeon.hero.resurrect( -1 );
			Dungeon.resetLevel();
		}
	}
	
	@Override
	protected void onBackPressed() {
		// Do nothing
	}
}

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
package com.watabou.pixeldungeon.windows;

import java.io.IOException;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Babylon;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PXL610;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.RankingsScene;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.ui.Icons;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndGame extends Window {
	
	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;
	
	private int pos;
	
	public WndGame() {
		
		super();

		if (Dungeon.hero.isAlive()) {
			addButtons(
					new RedButton(Babylon.get().getFromResources("save_button_save")) {
						@Override
						protected void onClick() {
							hide();
							GameScene.show(new WndSaver(Babylon.get().getFromResources("save_title_save"), true, Dungeon.hero.heroClass, true));
						}
					}, new RedButton(Babylon.get().getFromResources("save_buttton_load")) {
						@Override
						protected void onClick() {
							hide();
							GameScene.show(new WndSaver(Babylon.get().getFromResources("save_title_load"), false, Dungeon.hero.heroClass, true));
						}
					}
			);
		} else {
			addButton(
					new RedButton(Babylon.get().getFromResources("save_buttton_load")) {
						@Override
						protected void onClick() {
							hide();
							GameScene.show(new WndSaver(Babylon.get().getFromResources("save_title_load"), false, Dungeon.hero.heroClass, true));
						}
					}
			);
		}
		
		addButton( new RedButton( Babylon.get().getFromResources("wnd_game_settings") ) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show( new WndSettings( true ) );
			}
		} );
		
		if (Dungeon.challenges > 0) {
			addButton( new RedButton( Babylon.get().getFromResources("wnd_game_challenges") ) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show( new WndChallenges( Dungeon.challenges, false ) );
				}
			} );
		}
		
		if (!Dungeon.hero.isAlive()) {
			
			RedButton btnStart;
			addButton( btnStart = new RedButton( Babylon.get().getFromResources("wnd_game_start") ) {
				@Override
				protected void onClick() {
					Dungeon.hero = null;
					PXL610.challenges( Dungeon.challenges );
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					InterlevelScene.noStory = true;
					Game.switchScene( InterlevelScene.class );
				}
			} );
			btnStart.icon( Icons.get( Dungeon.hero.heroClass ) );
			
			addButton( new RedButton( Babylon.get().getFromResources("wnd_game_rankings") ) {
				@Override
				protected void onClick() {
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene( RankingsScene.class );
				}
			} );
		}
				
		addButtons( 
			new RedButton( Babylon.get().getFromResources("wnd_game_menu") ) {
				@Override
				protected void onClick() {
					try {
						Dungeon.saveAll();
					} catch (IOException e) {
						// Do nothing
					}
					Game.switchScene( TitleScene.class );
				}
			}, new RedButton( Babylon.get().getFromResources("wnd_game_exit") ) {
				@Override
				protected void onClick() {
					Game.instance.finish();
				}
			} 
		);
		
		addButton( new RedButton( Babylon.get().getFromResources("wnd_game_return") ) {
			@Override
			protected void onClick() {
				hide();
			}
		} );
		
		resize( WIDTH, pos );
	}
	
	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
	
	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
}

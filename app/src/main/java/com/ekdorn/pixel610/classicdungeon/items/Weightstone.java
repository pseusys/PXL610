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
package com.ekdorn.pixel610.classicdungeon.items;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.items.weapon.Weapon;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.scenes.PixelScene;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.ui.RedButton;
import com.ekdorn.pixel610.classicdungeon.ui.Window;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.classicdungeon.windows.IconTitle;
import com.ekdorn.pixel610.classicdungeon.windows.WndBag;

public class Weightstone extends Item {
	
	private static final float TIME_TO_APPLY = 2;

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("weightstone_name");
		image = ItemSpriteSheet.WEIGHT;
		
		stackable = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( Babylon.get().getFromResources("weightstone_acapply") );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action == Babylon.get().getFromResources("weightstone_acapply")) {

			curUser = hero;
			GameScene.selectItem( itemSelector, WndBag.Mode.WEAPON, Babylon.get().getFromResources("weightstone_selectweapon") );
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private void apply( Weapon weapon, boolean forSpeed ) {
		
		detach( curUser.belongings.backpack );
		
		weapon.fix();
		if (forSpeed) {
			weapon.imbue = Weapon.Imbue.SPEED;
			GLog.p( Babylon.get().getFromResources("weightstone_fast"), weapon.name() );
		} else {
			weapon.imbue = Weapon.Imbue.ACCURACY;
			GLog.p( Babylon.get().getFromResources("weightstone_accurate"), weapon.name() );
		}
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.SND_MISS );
		
		curUser.spend( TIME_TO_APPLY );
		curUser.busy();
	}
	
	@Override
	public int price() {
		return 40 * quantity;
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("weightstone_info");
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				GameScene.show( new WndBalance( (Weapon)item ) );
			}
		}
	};
	
	public class WndBalance extends Window {
		
		private static final int WIDTH			= 120;
		private static final int MARGIN 		= 2;
		private static final int BUTTON_WIDTH	= WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT	= 20;
		
		public WndBalance( final Weapon weapon ) {
			super();
			
			IconTitle titlebar = new IconTitle( weapon );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			BitmapTextMultiline tfMesage = PixelScene.createMultiline( Utils.format( Babylon.get().getFromResources("weightstone_wnd_choice"), weapon.name() ), 8 );
			tfMesage.maxWidth = WIDTH - MARGIN * 2;
			tfMesage.measure();
			tfMesage.x = MARGIN;
			tfMesage.y = titlebar.bottom() + MARGIN;
			add( tfMesage );
			
			float pos = tfMesage.y + tfMesage.height();
			
			if (weapon.imbue != Weapon.Imbue.SPEED) {
				RedButton btnSpeed = new RedButton( Babylon.get().getFromResources("weightstone_wnd_speed") ) {
					@Override
					protected void onClick() {
						hide();
						Weightstone.this.apply( weapon, true );
					}
				};
				btnSpeed.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
				add( btnSpeed );
				
				pos = btnSpeed.bottom();
			}
			
			if (weapon.imbue != Weapon.Imbue.ACCURACY) {
				RedButton btnAccuracy = new RedButton( Babylon.get().getFromResources("weightstone_wnd_accuracy") ) {
					@Override
					protected void onClick() {
						hide();
						Weightstone.this.apply( weapon, false );
					}
				};
				btnAccuracy.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
				add( btnAccuracy );
				
				pos = btnAccuracy.bottom();
			}
			
			RedButton btnCancel = new RedButton( Babylon.get().getFromResources("weightstone_wnd_nevermind") ) {
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
			add( btnCancel );
			
			resize( WIDTH, (int)btnCancel.bottom() + MARGIN );
		}
		
		protected void onSelect( int index ) {};
	}
}

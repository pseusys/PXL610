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

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.effects.particles.ShaftParticle;
import com.ekdorn.pixel610.classicdungeon.sprites.CharSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSprite.Glowing;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;

public class DewVial extends Item {

	private static final int MAX_VOLUME	= 10;
	
	private static final float TIME_TO_DRINK = 1f;

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("item_dewvial");
		image = ItemSpriteSheet.VIAL;
		
		defaultAction = Babylon.get().getFromResources("item_dewvial_acdrink");
		
		unique = true;
	}
	
	private int volume = 0;
	
	private static final String VOLUME	= "volume";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( VOLUME, volume );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		volume	= bundle.getInt( VOLUME );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (volume > 0) {
			actions.add( Babylon.get().getFromResources("potion_action") );
		}
		return actions;
	}
	
	private static final double NUM = 20;
	private static final double POW = Math.log10( NUM );
	
	@Override
	public void execute( final Hero hero, String action ) {
		if (action.equals( Babylon.get().getFromResources("potion_action") )) {
			
			if (volume > 0) {

				int value = (int)Math.ceil( Math.pow( volume, POW ) / NUM * hero.HT );
				int effect = Math.min( hero.HT - hero.HP, value );
				if (effect > 0) {
					hero.HP += effect;
					hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), volume > 5 ? 2 : 1 );
					hero.sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("item_dewvial_value"), effect );
				}
				
				volume = 0;
				
				hero.spend( TIME_TO_DRINK );
				hero.busy();
				
				Sample.INSTANCE.play( Assets.SND_DRINK );
				hero.sprite.operate( hero.pos );
				
				updateQuickslot();
				
			} else {
				GLog.w( Babylon.get().getFromResources("item_dewvial_empty") );
			}
			
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
	
	public boolean isFull() {
		return volume >= MAX_VOLUME;
	}
	
	public void collectDew( Dewdrop dew ) {
		
		GLog.i( Babylon.get().getFromResources("item_dewvial_collected") );
		volume += dew.quantity;
		if (volume >= MAX_VOLUME) {
			volume = MAX_VOLUME;
			GLog.p( Babylon.get().getFromResources("item_dewvial_full") );
		}
		
		updateQuickslot();
	}
	
	public void fill() {
		volume = MAX_VOLUME;
		updateQuickslot();
	}
	
	public static void autoDrink( Hero hero ) {
		DewVial vial = hero.belongings.getItem( DewVial.class );
		if (vial != null && vial.isFull()) {
			vial.execute( hero );
			hero.sprite.emitter().start( ShaftParticle.FACTORY, 0.2f, 3 );
			
			GLog.w( Babylon.get().getFromResources("item_dewvial_autodrink") );
		}
	}
	
	private static final Glowing WHITE = new Glowing( 0xFFFFCC );
	
	@Override
	public Glowing glowing() {
		return isFull() ? WHITE : null;
	}
	
	@Override
	public String status() {
		return Utils.format( Babylon.get().getFromResources("item_dewvial_status"), volume, MAX_VOLUME );
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("item_dewvial_desc");
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + status() +  ")" ;
	}
}

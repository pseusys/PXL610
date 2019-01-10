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
package com.ekdorn.pixel610.pixeldungeon.items;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfBlink;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.scenes.InterlevelScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite.Glowing;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;

public class LloydsBeacon extends Item {
	
	public static final float TIME_TO_USE = 1;
	
	private int returnDepth	= -1;
	private int returnPos;

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("beacon_name");
		image = ItemSpriteSheet.BEACON;
		
		unique = true;
	}
	
	private static final String DEPTH	= "depth";
	private static final String POS		= "pos";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DEPTH, returnDepth );
		if (returnDepth != -1) {
			bundle.put( POS, returnPos );
		}
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		returnDepth	= bundle.getInt( DEPTH );
		returnPos	= bundle.getInt( POS );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( Babylon.get().getFromResources("beacon_acset") );
		if (returnDepth != -1) {
			actions.add( Babylon.get().getFromResources("beacon_acreturn") );
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		
		if (action == Babylon.get().getFromResources("beacon_acset") || action == Babylon.get().getFromResources("beacon_acreturn")) {
			
			if (Dungeon.bossLevel()) {
				hero.spend( LloydsBeacon.TIME_TO_USE );
				GLog.w( Babylon.get().getFromResources("beacon_error") );
				return;
			}
			
			for (int i=0; i < Level.NEIGHBOURS8.length; i++) {
				if (Actor.findChar( hero.pos + Level.NEIGHBOURS8[i] ) != null) {
					GLog.w( Babylon.get().getFromResources("beacon_creatures") );
					return;
				}
			}
		}
		
		if (action == Babylon.get().getFromResources("beacon_acset")) {
			
			returnDepth = Dungeon.depth;
			returnPos = hero.pos;
			
			hero.spend( LloydsBeacon.TIME_TO_USE );
			hero.busy();
			
			hero.sprite.operate( hero.pos );
			Sample.INSTANCE.play( Assets.SND_BEACON );
			
			GLog.i( Babylon.get().getFromResources("beacon_return") );
			
		} else if (action == Babylon.get().getFromResources("beacon_acreturn")) {
			
			if (returnDepth == Dungeon.depth) {
				reset();
				WandOfBlink.appear( hero, returnPos );
				Dungeon.level.press( returnPos, hero );
				Dungeon.observe();
			} else {
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;
				InterlevelScene.returnDepth = returnDepth;
				InterlevelScene.returnPos = returnPos;
				reset();
				Game.switchScene( InterlevelScene.class );
			}
			
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	public void reset() {
		returnDepth = -1;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private static final Glowing WHITE = new Glowing( 0xFFFFFF );
	
	@Override
	public Glowing glowing() {
		return returnDepth != -1 ? WHITE : null;
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("beacon_info") + (returnDepth == -1 ? "" : Utils.format( Babylon.get().getFromResources("beacon_set"), returnDepth ) );
	}
}

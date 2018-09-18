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
package com.ekdorn.pixel610.pixeldungeon.actors.buffs;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.ResultDescriptions;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.Blob;
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.Fire;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Thief;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ElmoParticle;
import com.ekdorn.pixel610.pixeldungeon.items.Heap;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.food.ChargrilledMeat;
import com.ekdorn.pixel610.pixeldungeon.items.food.MysteryMeat;
import com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfElements.Resistance;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.Scroll;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.ui.BuffIndicator;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Burning extends Buff implements Hero.Doom {
	
	private static final float DURATION = 8f;
	
	private float left;
	
	private static final String LEFT	= "left";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat( LEFT );
	}
	
	@Override
	public boolean act() {
		
		if (target.isAlive()) {
			
			if (target instanceof Hero) {
				Buff.prolong( target, Light.class, TICK * 1.01f );
			}

			target.damage( Random.Int( 1, 5 ), this );
			
			if (target instanceof Hero) {
				
				Item item = ((Hero)target).belongings.randomUnequipped();
				if (item instanceof Scroll) {
					
					item = item.detach( ((Hero)target).belongings.backpack );
					GLog.w( Babylon.get().getFromResources("burns_up"), item.toString() );
					
					Heap.burnFX( target.pos );
					
				} else if (item instanceof MysteryMeat) {
					
					item = item.detach( ((Hero)target).belongings.backpack );
					ChargrilledMeat steak = new ChargrilledMeat(); 
					if (!steak.collect( ((Hero)target).belongings.backpack )) {
						Dungeon.level.drop( steak, target.pos ).sprite.drop();
					}
					GLog.w( Babylon.get().getFromResources("burns_up"), item.toString() );
					
					Heap.burnFX( target.pos );
					
				}
				
			} else if (target instanceof Thief && ((Thief)target).item instanceof Scroll) {
				
				((Thief)target).item = null;
				target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
			}

		} else {
			detach();
		}
		
		if (Level.flamable[target.pos]) {
			GameScene.add( Blob.seed( target.pos, 4, Fire.class ) );
		}
		
		spend( TICK );
		left -= TICK;
		
		if (left <= 0 ||
			Random.Float() > (2 + (float)target.HP / target.HT) / 3 ||
			(Level.water[target.pos] && !target.flying)) {
			
			detach();
		}

		return true;
	}
	
	public void reignite( Char ch ) {
		left = duration( ch );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}
	
	@Override
	public String toString() {
		return Babylon.get().getFromResources("buff_burn");
	}

	public static float duration( Char ch ) {
		Resistance r = ch.buff( Resistance.class );
		return r != null ? r.durationFactor() * DURATION : DURATION;
	}

	@Override
	public void onDeath() {
		
		Badges.validateDeathFromFire();
		
		Dungeon.fail( Utils.format( ResultDescriptions.BURNING, Dungeon.depth ) );
		GLog.n( Babylon.get().getFromResources("death_burn") );
	}
}

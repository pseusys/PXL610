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
package com.ekdorn.pixel610.pixeldungeon.plants;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ShaftParticle;
import com.ekdorn.pixel610.pixeldungeon.items.potions.PotionOfHealing;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.ui.BuffIndicator;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;

public class Sungrass extends Plant {
	
	{
		image = 4;
		plantName = Utils.capitalize(Babylon.get().getFromResources("plants_plantof")) + " " + Babylon.get().getFromResources("plants_sungrass");
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		if (ch != null) {
			Buff.affect( ch, Health.class );
		}
		
		if (Dungeon.visible[pos]) {
			CellEmitter.get( pos ).start( ShaftParticle.FACTORY, 0.2f, 3 );
		}
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("plants_sungrass_desc");
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = Babylon.get().getFromResources("plants_sungrass");
			
			name = Babylon.get().getFromResources("plants_seedof") + " " + plantName;
			image = ItemSpriteSheet.SEED_SUNGRASS;
			
			plantClass = Sungrass.class;
			alchemyClass = PotionOfHealing.class;
		}
		
		@Override
		public String desc() {
			return Babylon.get().getFromResources("plants_sungrass_desc");
		}
	}
	
	public static class Health extends Buff {
		
		private static final float STEP = 5f;
		
		private int pos;
		
		@Override
		public boolean attachTo( Char target ) {
			pos = target.pos;
			return super.attachTo( target );
		}
		
		@Override
		public boolean act() {
			if (target.pos != pos || target.HP >= target.HT) {
				detach();
			} else {
				target.HP = Math.min( target.HT, target.HP + target.HT / 10 );
				target.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			}
			spend( STEP );
			return true;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.HEALING;
		}
		
		@Override
		public String toString() {
			return Babylon.get().getFromResources("plants_sungrass_herbalhealing");
		}
		
		private static final String POS	= "pos";
		
		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( POS, pos );
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			pos = bundle.getInt( POS );
		}
	}
}

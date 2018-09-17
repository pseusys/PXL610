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
package com.ekdorn.pixel610.classicdungeon.plants;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Poison;
import com.ekdorn.pixel610.classicdungeon.effects.CellEmitter;
import com.ekdorn.pixel610.classicdungeon.effects.particles.PoisonParticle;
import com.ekdorn.pixel610.classicdungeon.items.potions.PotionOfToxicGas;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;

public class Sorrowmoss extends Plant {
	
	{
		image = 2;
		plantName = Utils.capitalize(Babylon.get().getFromResources("plants_plantof")) + " " + Babylon.get().getFromResources("plants_sorrowmoss");
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		if (ch != null) {
			Buff.affect( ch, Poison.class ).set( Poison.durationFactor( ch ) * (4 + Dungeon.depth / 2) );
		}
		
		if (Dungeon.visible[pos]) {
			CellEmitter.center( pos ).burst( PoisonParticle.SPLASH, 3 );
		}
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("plants_sorrowmoss_desc");
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = Babylon.get().getFromResources("plants_sorrowmoss");
			
			name = Babylon.get().getFromResources("plants_seedof") + " " + plantName;
			image = ItemSpriteSheet.SEED_SORROWMOSS;
			
			plantClass = Sorrowmoss.class;
			alchemyClass = PotionOfToxicGas.class;
		}
		
		@Override
		public String desc() {
			return Babylon.get().getFromResources("plants_sorrowmoss_desc");
		}
	}
}

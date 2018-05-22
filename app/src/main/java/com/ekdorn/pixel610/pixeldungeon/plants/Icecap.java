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
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.Fire;
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.Freezing;
import com.ekdorn.pixel610.pixeldungeon.items.potions.PotionOfFrost;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.BArray;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.PathFinder;

public class Icecap extends Plant {
	
	{
		image = 1;
		plantName = Utils.capitalize(Babylon.get().getFromResources("plants_plantof")) + " " + Babylon.get().getFromResources("plants_icecap");
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		PathFinder.buildDistanceMap( pos, BArray.not( Level.losBlocking, null ), 1 );
		
		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		
		for (int i=0; i < Level.LENGTH; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Freezing.affect( i, fire );
			}
		}
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("plants_icecap_desc");
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = Babylon.get().getFromResources("plants_icecap");
			
			name = Babylon.get().getFromResources("plants_seedof") + " " + plantName;
			image = ItemSpriteSheet.SEED_ICECAP;
			
			plantClass = Icecap.class;
			alchemyClass = PotionOfFrost.class;
		}
		
		@Override
		public String desc() {
			return Babylon.get().getFromResources("plants_icecap_desc");
		}
	}
}

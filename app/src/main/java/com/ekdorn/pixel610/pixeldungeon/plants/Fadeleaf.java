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
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.items.potions.PotionOfMindVision;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class Fadeleaf extends Plant {
	
	{
		image = 6;
		plantName = Utils.capitalize(Babylon.get().getFromResources("plants_plantof")) + " " + Babylon.get().getFromResources("plants_fadeleaf");
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		if (ch instanceof Hero) {
			
			ScrollOfTeleportation.teleportHero( (Hero)ch );
			((Hero)ch).curAction = null;
			
		} else if (ch instanceof Mob) {
			
			int count = 10;
			int newPos;
			do {
				newPos = Dungeon.level.randomRespawnCell();
				if (count-- <= 0) {
					break;
				}
			} while (newPos == -1);
			
			if (newPos != -1) {
			
				ch.pos = newPos;
				ch.sprite.place( ch.pos );
				ch.sprite.visible = Dungeon.visible[pos];
				
			}
						
		}
		
		if (Dungeon.visible[pos]) {
			CellEmitter.get( pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
		}
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("plants_fadeleaf_desc");
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = Babylon.get().getFromResources("plants_fadeleaf");
			
			name = Babylon.get().getFromResources("plants_seedof") + " " + plantName;
			image = ItemSpriteSheet.SEED_FADELEAF;
			
			plantClass = Fadeleaf.class;
			alchemyClass = PotionOfMindVision.class;
		}
		
		@Override
		public String desc() {
			return Babylon.get().getFromResources("plants_fadeleaf_desc");
		}
	}
}

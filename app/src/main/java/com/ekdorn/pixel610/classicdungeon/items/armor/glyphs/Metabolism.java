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
package com.ekdorn.pixel610.classicdungeon.items.armor.glyphs;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Hunger;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor.Glyph;
import com.ekdorn.pixel610.classicdungeon.sprites.CharSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSprite.Glowing;
import com.ekdorn.pixel610.classicdungeon.ui.BuffIndicator;
import com.ekdorn.pixel610.utils.Random;

public class Metabolism extends Glyph {
	
	private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0xCC0000 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max( 0, armor.effectiveLevel() );
		if (Random.Int( level / 2 + 5 ) >= 4) {
			
			int healing = Math.min( defender.HT - defender.HP, Random.Int( 1, defender.HT / 5 ) );
			if (healing > 0) {
				
				Hunger hunger = defender.buff( Hunger.class );
				
				if (hunger != null && !hunger.isStarving()) {
					
					hunger.satisfy( -Hunger.STARVING / 10 );
					BuffIndicator.refreshHero();
					
					defender.HP += healing;
					defender.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
					defender.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( healing ) );
				}
			}

		}
		
		return damage;
	}
	
	@Override
	public String name( String weaponName) {
		return String.format( Babylon.get().getFromResources("glyph_metabolism"), weaponName );
	}

	@Override
	public Glowing glowing() {
		return RED;
	}
}

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

import java.util.ArrayList;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs.MirrorImage;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor.Glyph;
import com.ekdorn.pixel610.classicdungeon.items.wands.WandOfBlink;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSprite.Glowing;
import com.ekdorn.pixel610.utils.Random;

public class Multiplicity extends Glyph {
	
	private static ItemSprite.Glowing PINK = new ItemSprite.Glowing( 0xCCAA88 );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage) {

		int level = Math.max( 0, armor.effectiveLevel() );
		
		if (Random.Int( level / 2 + 6 ) >= 5) {
			
			ArrayList<Integer> respawnPoints = new ArrayList<Integer>();
			
			for (int i=0; i < Level.NEIGHBOURS8.length; i++) {
				int p = defender.pos + Level.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && (Level.passable[p] || Level.avoid[p])) {
					respawnPoints.add( p );
				}
			}
			
			if (respawnPoints.size() > 0) {
				MirrorImage mob = new MirrorImage();
				mob.duplicate( (Hero)defender );
				GameScene.add( mob );
				WandOfBlink.appear( mob, Random.element( respawnPoints ) );
				
				defender.damage( Random.IntRange( 1, defender.HT / 6 ), this );
				checkOwner( defender );
			}
			
		}
		
		return damage;
	}
	
	@Override
	public String name( String weaponName) {
		return String.format( Babylon.get().getFromResources("glyph_multiplicity"), weaponName );
	}

	@Override
	public Glowing glowing() {
		return PINK;
	}
}

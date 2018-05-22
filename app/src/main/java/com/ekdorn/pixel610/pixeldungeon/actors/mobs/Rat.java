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
package com.ekdorn.pixel610.pixeldungeon.actors.mobs;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.Ghost;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.RatSprite;
import com.ekdorn.pixel610.pixeldungeon.windows.WndScript;
import com.ekdorn.pixel610.utils.Random;

public class Rat extends Mob {

	{
		name = Babylon.get().getFromResources("mob_rat");
		spriteClass = RatSprite.class;
		
		HP = HT = 8;
		defenseSkill = 3;
		
		maxLvl = 5;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 8;
	}
	
	@Override
	public int dr() {
		return 1;
	}
	
	@Override
	public void die( Object cause ) {
		Ghost.Quest.processSewersKill( pos );
		
		super.die( cause );
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_rat_desc");
	}
}

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
package com.ekdorn.pixel610.classicdungeon.actors.mobs;

import java.util.HashSet;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Amok;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Sleep;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Terror;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs.Imp;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfPsionicBlast;
import com.ekdorn.pixel610.classicdungeon.sprites.GolemSprite;
import com.ekdorn.pixel610.utils.Random;

public class Golem extends Mob {
	
	{
		name = Babylon.get().getFromResources("mob_golem");
		spriteClass = GolemSprite.class;
		
		HP = HT = 85;
		defenseSkill = 18;
		
		EXP = 12;
		maxLvl = 22;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 20, 40 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	protected float attackDelay() {
		return 1.5f;
	}
	
	@Override
	public int dr() {
		return 12;
	}
	
	@Override
	public String defenseVerb() {
		return Babylon.get().getFromResources("defmod_block");
	}
	
	@Override
	public void die( Object cause ) {
		Imp.Quest.process( this );
		
		super.die( cause );
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_golem_desc");
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Amok.class );
		IMMUNITIES.add( Terror.class );
		IMMUNITIES.add( Sleep.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}

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
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Amok;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Terror;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs.Imp;
import com.ekdorn.pixel610.classicdungeon.items.KindOfWeapon;
import com.ekdorn.pixel610.classicdungeon.items.food.Food;
import com.ekdorn.pixel610.classicdungeon.items.weapon.melee.Knuckles;
import com.ekdorn.pixel610.classicdungeon.sprites.MonkSprite;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Random;

public class Monk extends Mob {
	
	{
		name = Babylon.get().getFromResources("mob_monk");
		spriteClass = MonkSprite.class;
		
		HP = HT = 70;
		defenseSkill = 30;
		
		EXP = 11;
		maxLvl = 21;
		
		loot = new Food();
		lootChance = 0.083f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 12, 16 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 30;
	}
	
	@Override
	protected float attackDelay() {
		return 0.5f;
	}
	
	@Override
	public int dr() {
		return 2;
	}
	
	@Override
	public String defenseVerb() {
		return Babylon.get().getFromResources("defmod_parried");
	}
	
	@Override
	public void die( Object cause ) {
		Imp.Quest.process( this );
		
		super.die( cause );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		
		if (Random.Int( 6 ) == 0 && enemy == Dungeon.hero) {
			
			Hero hero = Dungeon.hero;
			KindOfWeapon weapon = hero.belongings.weapon;
			
			if (weapon != null && !(weapon instanceof Knuckles) && !weapon.cursed) {
				hero.belongings.weapon = null;
				Dungeon.level.drop( weapon, hero.pos ).sprite.drop();
				GLog.w( Babylon.get().getFromResources("mob_monk_disarm"), name, weapon.name() );
			}
		}
		
		return damage;
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_monk_desc");
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Amok.class );
		IMMUNITIES.add( Terror.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}

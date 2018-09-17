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

import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.ResultDescriptions;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.effects.particles.SparkParticle;
import com.ekdorn.pixel610.classicdungeon.items.Generator;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.levels.traps.LightningTrap;
import com.ekdorn.pixel610.classicdungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.classicdungeon.sprites.CharSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.ShamanSprite;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Callback;
import com.ekdorn.pixel610.utils.Random;

public class Shaman extends Mob implements Callback {

	private static final float TIME_TO_ZAP	= 2f;
	
	{
		name = Babylon.get().getFromResources("mob_shaman");
		spriteClass = ShamanSprite.class;
		
		HP = HT = 18;
		defenseSkill = 8;
		
		EXP = 6;
		maxLvl = 14;
		
		loot = Generator.Category.SCROLL;
		lootChance = 0.33f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 11;
	}
	
	@Override
	public int dr() {
		return 4;
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return Ballistica.cast( pos, enemy.pos, false, true ) == enemy.pos;
	}
	
	@Override
	protected boolean doAttack( Char enemy ) {

		if (Level.distance( pos, enemy.pos ) <= 1) {
			
			return super.doAttack( enemy );
			
		} else {
			
			boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos]; 
			if (visible) {
				((ShamanSprite)sprite).zap( enemy.pos );
			}
			
			spend( TIME_TO_ZAP );
			
			if (hit( this, enemy, true )) {
				int dmg = Random.Int( 2, 12 );
				if (Level.water[enemy.pos] && !enemy.flying) {
					dmg *= 1.5f;
				}
				enemy.damage( dmg, LightningTrap.LIGHTNING );
				
				enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
				enemy.sprite.flash();
				
				if (enemy == Dungeon.hero) {
					
					Camera.main.shake( 2, 0.3f );
					
					if (!enemy.isAlive()) {
						Dungeon.fail( Utils.format( ResultDescriptions.MOB, 
							Utils.indefinite( name ), Dungeon.depth ) );
						GLog.n( Babylon.get().getFromResources("mob_shaman_kills"), name );
					}
				}
			} else {
				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}
			
			return !visible;
		}
	}
	
	@Override
	public void call() {
		next();
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_shaman_desc");
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( LightningTrap.Electricity.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}

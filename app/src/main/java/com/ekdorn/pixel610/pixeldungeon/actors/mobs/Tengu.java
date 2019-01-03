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

import java.util.ArrayList;
import java.util.HashSet;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Statistics;
import com.ekdorn.pixel610.pixeldungeon.Badges.Badge;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.ToxicGas;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Poison;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroSubClass;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.items.TomeOfMastery;
import com.ekdorn.pixel610.pixeldungeon.items.keys.SkeletonKey;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.enchantments.Death;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.levels.Terrain;
import com.ekdorn.pixel610.pixeldungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.TenguSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Random;

public class Tengu extends Mob {

	private AiState DANCING = new Dancing();
	
	{
		name = Dungeon.depth == Statistics.deepestFloor ? Babylon.get().getFromResources("mob_tengu") : Babylon.get().getFromResources("mob_tengumemory");
		spriteClass = TenguSprite.class;
		
		HP = HT = 120;
		EXP = 20;
		defenseSkill = 20;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 8, 15 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}
	
	@Override
	public int dr() {
		return 5;
	}
	
	@Override
	public void die( Object cause ) {
		
		Badges.Badge badgeToCheck = null;
		switch (Dungeon.hero.heroClass) {
		case WARRIOR:
			badgeToCheck = Badge.MASTERY_WARRIOR;
			break;
		case MAGE:
			badgeToCheck = Badge.MASTERY_MAGE;
			break;
		case ROGUE:
			badgeToCheck = Badge.MASTERY_ROGUE;
			break;
		case HUNTRESS:
			badgeToCheck = Badge.MASTERY_HUNTRESS;
			break;
		}
		if (!Badges.isUnlocked( badgeToCheck ) || Dungeon.hero.subClass != HeroSubClass.NONE) {
			Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		}
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey(), pos ).sprite.drop();
		super.die( cause );
		
		Badges.validateBossSlain();
		
		yell(Babylon.get().getFromResources("mob_tengu_death"));
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return Ballistica.cast( pos, enemy.pos, false, true ) == enemy.pos;
	}
	
	@Override
	public void notice() {
		super.notice();
		yell(Utils.format(Babylon.get().getFromResources("mob_tengu_notice"), Dungeon.hero.heroClass.title()) );
		this.state = DANCING;
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_tengu_desc");
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	private class Dancing implements AiState {

		public static final String TAG	= "DANCING";
		private int direction = 0;

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			int oldPos = pos;

			if (direction == 0) {
				//attac();
				direction = Random.NormalIntRange(5, 7);
			} else {
				pos += Level.NEIGHBOURS8[direction];
			}

			switch (direction) {
				case 1:
					pos += Level.NEIGHBOURS8[5];
				case 2:
					pos += Level.NEIGHBOURS8[4];
				case 3:
					pos += Level.NEIGHBOURS8[7];
				case 4:
					pos += Level.NEIGHBOURS8[6];
				case 0:
					direction = Random.NormalIntRange(5, 7);
			}
			ArrayList<Integer> candidates = new ArrayList<Integer>();

			for (int i=0; i < Level.NEIGHBOURS8.length; i++) {
				int p = pos + Level.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && (Level.passable[p] || Level.avoid[p])) {
					candidates.add( p );
				}
			}

			return doAttack( enemy );

			/*if (enemyInFOV && canAttack( enemy )) {


			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				}

				//int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					spend( TICK );
					state = WANDERING;
					target = Dungeon.level.randomDestination();
					return true;
				}
			}*/
		}

		@Override
		public String status() {
			return Utils.format(Babylon.get().getFromResources("mob_status_hunting"), name );
		}
	}
}

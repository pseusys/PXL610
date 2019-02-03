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

import java.util.HashSet;

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
import com.ekdorn.pixel610.pixeldungeon.items.Heap;
import com.ekdorn.pixel610.pixeldungeon.items.TomeOfMastery;
import com.ekdorn.pixel610.pixeldungeon.items.keys.SkeletonKey;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.enchantments.Death;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.missiles.MissileWeapon;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.levels.PrisonBossLevel;
import com.ekdorn.pixel610.pixeldungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.CharSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.sprites.TenguSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.pixeldungeon.windows.WndScript;
import com.ekdorn.pixel610.utils.Random;

public class Tengu extends Mob {

	public AiState DANCING = new Dancing();
	
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
		return 25;
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
		//if (!Badges.isUnlocked( badgeToCheck ) || Dungeon.hero.subClass != HeroSubClass.NONE) {
			//Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		//}
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey(), pos ).sprite.drop();
		super.die( cause );
		
		Badges.validateBossSlain();
		
		yell(Babylon.get().getFromResources("mob_tengu_death"));

		GameScene.show(new WndScript(Tengu.this, Babylon.get().getFromResources("mob_tengu_plot")));
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
		return Babylon.get().getFromResources("mob_tengu_desc");
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
				direction = Random.NormalIntRange(4, 7);
			} else {
				pos += Level.NEIGHBOURS8[direction];
			}

			if (!(Actor.findChar( pos ) == null && (Level.passable[pos] || Level.avoid[pos]))) {
				pos = oldPos;

				switch (direction) {
					case 4:
					case 6:
						int left = pos + Level.NEIGHBOURS4[2];
						if (!(Actor.findChar( left ) == null && Level.passable[left])) {
							direction++;
						} else {
							direction += (direction > 5) ? -2 : 2;
						}
						break;
					case 5:
					case 7:
						int right = pos + Level.NEIGHBOURS4[0];
						if (!(Actor.findChar( right ) == null && Level.passable[right])) {
							direction--;
						} else {
							direction += (direction > 6) ? -2 : 2;
						}
						break;
				}
			}

			if (pos != oldPos) {
                if (enemyInFOV && canAttack( enemy )) attack();
                heal(pos);

                return moveSprite(oldPos, pos);
            } else {
			    return Tengu.this.state.act(enemyInFOV, justAlerted);
            }
		}

		private void attack() {
			if (Random.IntRange(0, 14) == 0) {
				for (int i = 0; i < 2; i++) {
					int aim = ((PrisonBossLevel) Dungeon.level).roomExit.random();
					Dungeon.level.drop(new Tengu.Button(), aim).sprite.drop(aim);
				}
				sprite.showStatus(CharSprite.NEGATIVE, "!!!");
			} else {
				doAttack(enemy);
			}
		}

		private void heal(int pos) {
			Heap heap;
			if (((heap = Dungeon.level.heaps.get(pos)) != null) && (heap.peek() instanceof Button)) {
				heap.pickUp();
				yell(Babylon.get().getFromResources("mob_tengu_heal"));
				Tengu.this.HP = HT;
			}
		}

		@Override
		public String status() {
			return Utils.format(Babylon.get().getFromResources("mob_status_dancing"), name );
		}
	}

	public static class Button extends MissileWeapon {
		@Override
		public void finish() {
			name = Babylon.get().getFromResources("weapon_button");
			image = ItemSpriteSheet.BUTTON;

			STR = 10;

			stackable = true;
		}

		@Override
		public int min() {
			return 3;
		}

		@Override
		public int max() {
			return 10;
		}

		@Override
		public String desc() {
			return Babylon.get().getFromResources("weapon_button_desc");
		}
	}
}

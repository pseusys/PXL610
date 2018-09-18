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

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.Statistics;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.blobs.ToxicGas;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Paralysis;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Vertigo;
import com.ekdorn.pixel610.pixeldungeon.effects.Flare;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.items.ArmorKit;
import com.ekdorn.pixel610.pixeldungeon.items.keys.SkeletonKey;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfBlink;
import com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfDisintegration;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.enchantments.Death;
import com.ekdorn.pixel610.pixeldungeon.levels.CityBossLevel;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.KingSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.UndeadSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.PathFinder;
import com.ekdorn.pixel610.utils.Random;

public class King extends Mob {
	
	private static final int MAX_ARMY_SIZE	= 5;
	
	{
		name = Dungeon.depth == Statistics.deepestFloor ? Babylon.get().getFromResources("mob_king") : Babylon.get().getFromResources("mob_kingundead");
		spriteClass = KingSprite.class;
		
		HP = HT = 300;
		EXP = 40;
		defenseSkill = 25;
		
		Undead.count = 0;
	}
	
	private boolean nextPedestal = true;
	
	private static final String PEDESTAL = "pedestal";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( PEDESTAL, nextPedestal );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		nextPedestal = bundle.getBoolean( PEDESTAL );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 20, 38 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 32;
	}
	
	@Override
	public int dr() {
		return 14;
	}
	
	@Override
	public String defenseVerb() {
		return Babylon.get().getFromResources("defmod_parried");
	}
	
	@Override
	protected boolean getCloser( int target ) {
		return canTryToSummon() ? 
			super.getCloser( CityBossLevel.pedestal( nextPedestal ) ) : 
			super.getCloser( target );
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return canTryToSummon() ? 
			pos == CityBossLevel.pedestal( nextPedestal ) : 
			Level.adjacent( pos, enemy.pos );
	}
	
	private boolean canTryToSummon() {
		if (Undead.count < maxArmySize()) {
			Char ch = Actor.findChar( CityBossLevel.pedestal( nextPedestal ) );
			return ch == this || ch == null;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean attack( Char enemy ) {
		if (canTryToSummon() && pos == CityBossLevel.pedestal( nextPedestal )) {
			summon();
			return true;
		} else {
			if (Actor.findChar( CityBossLevel.pedestal( nextPedestal ) ) == enemy) {
				nextPedestal = !nextPedestal;
			}
			return super.attack(enemy);
		}
	}
	
	@Override
	public void die( Object cause ) {
		GameScene.bossSlain();
		Dungeon.level.drop( new ArmorKit(), pos ).sprite.drop();
		Dungeon.level.drop( new SkeletonKey(), pos ).sprite.drop();
		
		super.die( cause );
		
		Badges.validateBossSlain();
		
		yell( Utils.format(Babylon.get().getFromResources("mob_king_death"), Dungeon.hero.heroClass.title()) );
	}
	
	private int maxArmySize() {
		return 1 + MAX_ARMY_SIZE * (HT - HP) / HT;
	}
	
	private void summon() {

		nextPedestal = !nextPedestal;
		
		sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.4f, 2 );		
		Sample.INSTANCE.play( Assets.SND_CHALLENGE );
		
		boolean[] passable = Level.passable.clone();
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char)actor).pos] = false;
			}
		}

		int undeadsToSummon = maxArmySize() - Undead.count;
		PathFinder.buildDistanceMap( pos, passable, undeadsToSummon );
		PathFinder.distance[pos] = Integer.MAX_VALUE;
		int dist = 1;
		
	undeadLabel:
		for (int i=0; i < undeadsToSummon; i++) {
			do {
				for (int j=0; j < Level.LENGTH; j++) {
					if (PathFinder.distance[j] == dist) {

						Undead undead = new Undead();
						undead.pos = j;
						GameScene.add( undead );

						WandOfBlink.appear( undead, j );
						new Flare( 3, 32 ).color( 0x000000, false ).show( undead.sprite, 2f ) ;

						PathFinder.distance[j] = Integer.MAX_VALUE;
						
						continue undeadLabel;
					}
				}
				dist++;
			} while (dist < undeadsToSummon);
		}
		
		yell(Babylon.get().getFromResources("mob_king_summon"));
	}
	
	@Override
	public void notice() {
		super.notice();
		yell(Babylon.get().getFromResources("mob_king_notice"));
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_king_desc");
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
		RESISTANCES.add( WandOfDisintegration.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Paralysis.class );
		IMMUNITIES.add( Vertigo.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
	
	public static class Undead extends Mob {

		public static int count = 0;
		
		{
			name = Babylon.get().getFromResources("mob_kings_dwarf");
			spriteClass = UndeadSprite.class;
			
			HP = HT = 28;
			defenseSkill = 15;
			
			EXP = 0;
			
			state = WANDERING;
		}
		
		@Override
		protected void onAdd() {
			count++;
			super.onAdd();
		}
		
		@Override
		protected void onRemove() {
			count--;
			super.onRemove();
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 12, 16 );
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 16;
		}
		
		@Override
		public int attackProc( Char enemy, int damage ) {
			if (Random.Int( MAX_ARMY_SIZE ) == 0) {
				Buff.prolong( enemy, Paralysis.class, 1 );
			}
			
			return damage;
		}
		
		@Override
		public void damage( int dmg, Object src ) {
			super.damage( dmg, src );
			if (src instanceof ToxicGas) {		
				((ToxicGas)src).clear( pos );
			}
		}
		
		@Override
		public void die( Object cause ) {
			super.die( cause );
			
			if (Dungeon.visible[pos]) {
				Sample.INSTANCE.play( Assets.SND_BONES );
			}
		}
		
		@Override
		public int dr() {
			return 5;
		}
		
		@Override
		public String defenseVerb() {
			return Babylon.get().getFromResources("defmod_block");
		}
		
		@Override
		public String description() {
			return
					Babylon.get().getFromResources("mob_kings_dwarf_desc");
		}
		
		private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
		static {
			IMMUNITIES.add( Death.class );
			IMMUNITIES.add( Paralysis.class );
		}
		
		@Override
		public HashSet<Class<?>> immunities() {
			return IMMUNITIES;
		}
	}
}

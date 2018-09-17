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
package com.ekdorn.pixel610.classicdungeon.actors.hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Badges;
import com.ekdorn.pixel610.classicdungeon.Bones;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.GamesInProgress;
import com.ekdorn.pixel610.classicdungeon.ResultDescriptions;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Barkskin;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Bleeding;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Blindness;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Combo;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Cripple;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Fury;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.GasesImmunity;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Hunger;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Light;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Ooze;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Paralysis;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Poison;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Regeneration;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Charm;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.SnipersMark;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Vertigo;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Weakness;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs.NPC;
import com.ekdorn.pixel610.classicdungeon.effects.CheckedCell;
import com.ekdorn.pixel610.classicdungeon.effects.Flare;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.items.Amulet;
import com.ekdorn.pixel610.classicdungeon.items.Ankh;
import com.ekdorn.pixel610.classicdungeon.items.DewVial;
import com.ekdorn.pixel610.classicdungeon.items.Dewdrop;
import com.ekdorn.pixel610.classicdungeon.items.Heap;
import com.ekdorn.pixel610.classicdungeon.items.Heap.Type;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.items.KindOfWeapon;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor;
import com.ekdorn.pixel610.classicdungeon.items.keys.GoldenKey;
import com.ekdorn.pixel610.classicdungeon.items.keys.Key;
import com.ekdorn.pixel610.classicdungeon.items.keys.SkeletonKey;
import com.ekdorn.pixel610.classicdungeon.items.keys.IronKey;
import com.ekdorn.pixel610.classicdungeon.items.potions.Potion;
import com.ekdorn.pixel610.classicdungeon.items.potions.PotionOfMight;
import com.ekdorn.pixel610.classicdungeon.items.potions.PotionOfStrength;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfAccuracy;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfDetection;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfElements;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfEvasion;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfHaste;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfShadows;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfThorns;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.Scroll;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfMagicMapping;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfRecharging;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfUpgrade;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfEnchantment;
import com.ekdorn.pixel610.classicdungeon.items.wands.Wand;
import com.ekdorn.pixel610.classicdungeon.items.weapon.melee.MeleeWeapon;
import com.ekdorn.pixel610.classicdungeon.items.weapon.missiles.MissileWeapon;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.levels.Terrain;
import com.ekdorn.pixel610.classicdungeon.levels.features.AlchemyPot;
import com.ekdorn.pixel610.classicdungeon.levels.features.Chasm;
import com.ekdorn.pixel610.classicdungeon.levels.features.Sign;
import com.ekdorn.pixel610.classicdungeon.plants.Earthroot;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.scenes.InterlevelScene;
import com.ekdorn.pixel610.classicdungeon.scenes.SurfaceScene;
import com.ekdorn.pixel610.classicdungeon.sprites.CharSprite;
import com.ekdorn.pixel610.classicdungeon.sprites.HeroSprite;
import com.ekdorn.pixel610.classicdungeon.ui.AttackIndicator;
import com.ekdorn.pixel610.classicdungeon.ui.BuffIndicator;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.windows.WndMessage;
import com.ekdorn.pixel610.classicdungeon.windows.WndResurrect;
import com.ekdorn.pixel610.classicdungeon.windows.WndTradeItem;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Hero extends Char {
	
	private static final String TXT_WAIT	= "...";
	
	public static final int STARTING_STR = 10;
	
	private static final float TIME_TO_REST		= 1f;
	private static final float TIME_TO_SEARCH	= 2f;
	
	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;
	
	private int attackSkill = 10;
	private int defenseSkill = 5;
	

	public boolean ready = false;

	public HeroAction curAction = null;
	public HeroAction lastAction = null;
	
	private Char enemy;
	
	public Armor.Glyph killerGlyph = null;
	
	private Item theKey;
	
	public boolean restoreHealth = false;
	
	public MissileWeapon rangedWeapon = null;
	public Belongings belongings;
	
	public int STR;
	public boolean weakened = false;
	
	public float awareness;
	
	public int lvl = 1;
	public int exp = 0;
	
	private ArrayList<Mob> visibleEnemies; 
	
	public Hero() {
		super();
		name = Babylon.get().getFromResources("hero_name");
		
		HP = HT = 20;
		STR = STARTING_STR;
		awareness = 0.1f;
		
		belongings = new Belongings( this );
		
		visibleEnemies = new ArrayList<Mob>();
	}

	public int STR() {
		return weakened ? STR - 2 : STR;
	}

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String STRENGTH	= "STR";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		
		heroClass.storeInBundle( bundle );
		subClass.storeInBundle( bundle );
		
		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );
		
		bundle.put( STRENGTH, STR );
		
		bundle.put( LEVEL, lvl );
		bundle.put( EXPERIENCE, exp );
		
		belongings.storeInBundle( bundle );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		heroClass = HeroClass.restoreInBundle( bundle );
		subClass = HeroSubClass.restoreInBundle( bundle );
		
		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );
		
		STR = bundle.getInt( STRENGTH );
		updateAwareness();
		
		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );
		
		belongings.restoreFromBundle( bundle );
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.level = bundle.getInt( LEVEL );
	}
	
	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title() : subClass.title();
	}
	
	public void live() {
		Buff.affect( this, Regeneration.class );	
		Buff.affect( this, Hunger.class );
	}
	
	public int tier() {
		return belongings.armor == null ? 0 : belongings.armor.tier;
	}
	
	public boolean shoot( Char enemy, MissileWeapon wep ) {
		
		rangedWeapon = wep;
		boolean result = attack( enemy );
		rangedWeapon = null;
		
		return result;
	}
	
	@Override
	public int attackSkill( Char target ) {
		
		int bonus = 0;
		for (Buff buff : buffs( RingOfAccuracy.Accuracy.class )) {
			bonus += ((RingOfAccuracy.Accuracy)buff).level;
		}
		float accuracy = (bonus == 0) ? 1 : (float)Math.pow( 1.4, bonus );
		if (rangedWeapon != null && Level.distance( pos, target.pos ) == 1) {
			accuracy *= 0.5f;
		}
		
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		if (wep != null) {
			return (int)(attackSkill * accuracy * wep.acuracyFactor( this ));
		} else {
			return (int)(attackSkill * accuracy);
		}
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		
		int bonus = 0;
		for (Buff buff : buffs( RingOfEvasion.Evasion.class )) {
			bonus += ((RingOfEvasion.Evasion)buff).level;
		}
		float evasion = bonus == 0 ? 1 : (float)Math.pow( 1.2, bonus );
		if (paralysed) {
			evasion /= 2;
		}
		
		int aEnc = belongings.armor != null ? belongings.armor.STR - STR() : 0;
		
		if (aEnc > 0) {
			return (int)(defenseSkill * evasion / Math.pow( 1.5, aEnc ));
		} else {
			
			if (heroClass == HeroClass.ROGUE) {
				
				if (curAction != null && subClass == HeroSubClass.FREERUNNER && !isStarving()) {
					evasion *= 2;
				}
				
				return (int)((defenseSkill - aEnc) * evasion);
			} else {
				return (int)(defenseSkill * evasion);
			}
		}
	}
	
	@Override
	public int dr() {
		int dr = belongings.armor != null ? Math.max( belongings.armor.DR(), 0 ) : 0;
		Barkskin barkskin = buff( Barkskin.class );
		if (barkskin != null) {
			dr += barkskin.level();
		}
		return dr;
	}
	
	@Override
	public int damageRoll() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		int dmg;
		if (wep != null) {	
			dmg = wep.damageRoll( this );
		} else {
			dmg = STR() > 10 ? Random.IntRange( 1, STR() - 9 ) : 1;
		}
		return buff( Fury.class ) != null ? (int)(dmg * 1.5f) : dmg;
	}
	
	@Override
	public float speed() {
		
		int aEnc = belongings.armor != null ? belongings.armor.STR - STR() : 0;
		if (aEnc > 0) {
			
			return (float)(super.speed() * Math.pow( 1.3, -aEnc ));
			
		} else {
			
			float speed = super.speed();
			return ((HeroSprite)sprite).sprint( subClass == HeroSubClass.FREERUNNER && !isStarving() ) ? 1.6f * speed : speed;
			
		}
	}
	
	public float attackDelay() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		if (wep != null) {
			
			return wep.speedFactor( this );
						
		} else {
			return 1f;
		}
	}
	
	@Override
	public void spend( float time ) {
		int hasteLevel = 0;
		for (Buff buff : buffs( RingOfHaste.Haste.class )) {
			hasteLevel += ((RingOfHaste.Haste)buff).level;
		}
		super.spend( hasteLevel == 0 ? time : (float)(time * Math.pow( 1.1, -hasteLevel )) );
	};
	
	public void spendAndNext( float time ) {
		busy();
		spend( time );
		next();
	}
	
	@Override
	public boolean act() {
		
		super.act();
		
		if (paralysed) {
			
			curAction = null;
			
			spendAndNext( TICK );
			return false;
		}
		
		checkVisibleMobs();
		AttackIndicator.updateState();
		
		if (curAction == null) {
			
			if (restoreHealth) {
				if (isStarving() || HP >= HT) {
					restoreHealth = false;
				} else {
					spend( TIME_TO_REST ); next();
					return false;
				}
			}
			
			ready();
			return false;
			
		} else {
			
			restoreHealth = false;
			
			ready = false;
			
			if (curAction instanceof HeroAction.Move) {
				
				return actMove( (HeroAction.Move)curAction );
				
			} else 
			if (curAction instanceof HeroAction.Interact) {
				
				return actInteract( (HeroAction.Interact)curAction );
				
			} else 
			if (curAction instanceof HeroAction.Buy) {
				
				return actBuy( (HeroAction.Buy)curAction );
				
			}else 
			if (curAction instanceof HeroAction.PickUp) {
				
				return actPickUp( (HeroAction.PickUp)curAction );
				
			} else 
			if (curAction instanceof HeroAction.OpenChest) {
				
				return actOpenChest( (HeroAction.OpenChest)curAction );
				
			} else 
			if (curAction instanceof HeroAction.Unlock) {
				
				return actUnlock( (HeroAction.Unlock)curAction );
				
			} else 
			if (curAction instanceof HeroAction.Descend) {
				
				return actDescend( (HeroAction.Descend)curAction );
				
			} else
			if (curAction instanceof HeroAction.Ascend) {
				
				return actAscend( (HeroAction.Ascend)curAction );
				
			} else
			if (curAction instanceof HeroAction.Attack) {

				return actAttack( (HeroAction.Attack)curAction );
				
			} else
			if (curAction instanceof HeroAction.Cook) {

				return actCook( (HeroAction.Cook)curAction );
				
			}
		}
		
		return false;
	}
	
	public void busy() {
		ready = false;
	}
	
	private void ready() {
		sprite.idle();
		curAction = null;
		ready = true;
		
		GameScene.ready();
	}
	
	public void interrupt() {
		if (isAlive() && curAction != null && curAction.dst != pos) {
			lastAction = curAction;
		}
		curAction = null;
	}
	
	public void resume() {
		curAction = lastAction;
		lastAction = null;
		act();
	}
	
	private boolean actMove( HeroAction.Move action ) {

		if (getCloser( action.dst )) {
			
			return true;
			
		} else {
			if (Dungeon.level.map[pos] == Terrain.SIGN) {
				Sign.read( pos );
			}
			ready();
			
			return false;
		}
	}
	
	private boolean actInteract( HeroAction.Interact action ) {
		
		NPC npc = action.npc;

		if (Level.adjacent( pos, npc.pos )) {
			
			ready();
			sprite.turnTo( pos, npc.pos );
			npc.interact();
			return false;
			
		} else {
			
			if (Level.fieldOfView[npc.pos] && getCloser( npc.pos )) {
				
				return true;
				
			} else {
				ready();
				return false;
			}
			
		}
	}
	
	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst || Level.adjacent( pos, dst )) {

			ready();
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
				GameScene.show( new WndTradeItem( heap, true ) );
			}
			
			return false;
			
		} else if (getCloser( dst )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actCook( HeroAction.Cook action ) {
		int dst = action.dst;
		if (Dungeon.visible[dst]) {

			ready();
			AlchemyPot.operate( this, dst );
			return false;
			
		} else if (getCloser( dst )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {				
				Item item = heap.pickUp();
				if (item.doPickUp( this )) {
					
					if (item instanceof Dewdrop) {
						// Do nothing
					} else {
						boolean important = 
							((item instanceof ScrollOfUpgrade || item instanceof ScrollOfEnchantment) && ((Scroll)item).isKnown()) ||
							((item instanceof PotionOfStrength || item instanceof PotionOfMight) && ((Potion)item).isKnown());
						if (important) {
							GLog.p( Babylon.get().getFromResources("hero_you_have"), item.name() );
						} else {
							GLog.i( Babylon.get().getFromResources("hero_you_have"), item.name() );
						}
					}
					
					if (!heap.isEmpty()) {
						GLog.i(Babylon.get().getFromResources("hero_smth_else"));
					}
					curAction = null;
				} else {
					Dungeon.level.drop( item, pos ).sprite.drop();
					ready();
				}
			} else {
				ready();
			}
			
			return false;
			
		} else if (getCloser( dst )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Level.adjacent( pos, dst ) || pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Type.HEAP && heap.type != Type.FOR_SALE)) {
				
				theKey = null;
				
				if (heap.type == Type.LOCKED_CHEST || heap.type == Type.CRYSTAL_CHEST) {

					theKey = belongings.getKey( GoldenKey.class, Dungeon.depth );
					
					if (theKey == null) {
						GLog.w(Babylon.get().getFromResources("hero_chest_locked"));
						ready();
						return false;
					}
				}
				
				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.SND_TOMB );
					Camera.main.shake( 1, 0.5f );
					break;
				case SKELETON:
					break;
				default:
					Sample.INSTANCE.play( Assets.SND_UNLOCK );
				}
				
				spend( Key.TIME_TO_UNLOCK );
				sprite.operate( dst );
				
			} else {
				ready();
			}
			
			return false;
			
		} else if (getCloser( dst )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Level.adjacent( pos, doorCell )) {
			
			theKey = null;
			int door = Dungeon.level.map[doorCell];
			
			if (door == Terrain.LOCKED_DOOR) {
				
				theKey = belongings.getKey( IronKey.class, Dungeon.depth );
				
			} else if (door == Terrain.LOCKED_EXIT) {
				
				theKey = belongings.getKey( SkeletonKey.class, Dungeon.depth );
				
			}
			
			if (theKey != null) {
				
				spend( Key.TIME_TO_UNLOCK );
				sprite.operate( doorCell );
				
				Sample.INSTANCE.play( Assets.SND_UNLOCK );
				
			} else {
				GLog.w(Babylon.get().getFromResources("hero_door_locked"));
				ready();
			}
			
			return false;
			
		} else if (getCloser( doorCell )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actDescend( HeroAction.Descend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.exit) {
			
			curAction = null;
			
			Hunger hunger = buff( Hunger.class );
			if (hunger != null && !hunger.isStarving()) {
				hunger.satisfy( -Hunger.STARVING / 10 );
			}
			
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			Game.switchScene( InterlevelScene.class );
			
			return false;
			
		} else if (getCloser( stairs )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAscend( HeroAction.Ascend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.entrance) {
			
			if (Dungeon.depth == 1) {
				
				if (belongings.getItem( Amulet.class ) == null) {
					GameScene.show( new WndMessage(Babylon.get().getFromResources("hero_no_leave")) );
					ready();
				} else {
					Dungeon.win( ResultDescriptions.WIN );
					Dungeon.deleteGame( Dungeon.hero.heroClass, true );
					Game.switchScene( SurfaceScene.class );
				}
				
			} else {
				
				curAction = null;
				
				Hunger hunger = buff( Hunger.class );
				if (hunger != null && !hunger.isStarving()) {
					hunger.satisfy( -Hunger.STARVING / 10 );
				}
				
				InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
				Game.switchScene( InterlevelScene.class );
			}
			
			return false;
			
		} else if (getCloser( stairs )) {
			
			return true;
			
		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAttack( HeroAction.Attack action ) {

		enemy = action.target;

		if (Level.adjacent( pos, enemy.pos ) && enemy.isAlive() && !isCharmedBy( enemy )) {
			
			spend( attackDelay() );
			sprite.attack( enemy.pos );
			
			return false;
			
		} else {
			
			if (Level.fieldOfView[enemy.pos] && getCloser( enemy.pos )) {
				
				return true;
				
			} else {
				ready();
				return false;
			}
			
		}
	}
	
	public void rest( boolean tillHealthy ) {
		spendAndNext( TIME_TO_REST );
		if (!tillHealthy) {
			sprite.showStatus( CharSprite.DEFAULT, TXT_WAIT );
		}
		restoreHealth = tillHealthy;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		if (wep != null) {
			
			wep.proc( this, enemy, damage );
			
			switch (subClass) {
			case GLADIATOR:
				if (wep instanceof MeleeWeapon) {
					damage += Buff.affect( this, Combo.class ).hit( enemy, damage );
				}
				break;
			case BATTLEMAGE:
				if (wep instanceof Wand) {
					Wand wand = (Wand)wep;
					if (wand.curCharges >= wand.maxCharges) {
						
						wand.use();
						
					} else if (damage > 0) {
						
						wand.curCharges++;
						wand.updateQuickslot();
						
						ScrollOfRecharging.charge( this );
					}
					damage += wand.curCharges;
				}
			case SNIPER:
				if (rangedWeapon != null) {
					Buff.prolong( this, SnipersMark.class, attackDelay() * 1.1f ).object = enemy.id();
				}
				break;
			default:
			}
		}
		
		return damage;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		
		RingOfThorns.Thorns thorns = buff( RingOfThorns.Thorns.class ); 
		if (thorns != null) {
			int dmg = Random.IntRange( 0, damage );
			if (dmg > 0) {
				enemy.damage( dmg, thorns );
			}
		}
		
		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}
		
		if (belongings.armor != null) {
			damage = belongings.armor.proc( enemy, this, damage );
		}
		
		return damage;
	}
	
	@Override
	public void damage( int dmg, Object src ) {		
		restoreHealth = false;
		super.damage( dmg, src );
		
		if (subClass == HeroSubClass.BERSERKER && 0 < HP && HP <= HT * Fury.LEVEL) {
			Buff.affect( this, Fury.class );
		}
	}
	
	private void checkVisibleMobs() {
		ArrayList<Mob> visible = new ArrayList<Mob>();
		
		boolean newMob = false;
		
		for (Mob m : Dungeon.level.mobs) {
			if (Level.fieldOfView[ m.pos ] && m.hostile) {
				visible.add( m );
				if (!visibleEnemies.contains( m )) {
					newMob = true;
				}
			}
		}
		
		if (newMob) {
			interrupt();
			restoreHealth = false;
		}
		
		visibleEnemies = visible;
	}
	
	public int visibleEnemies() {
		return visibleEnemies.size();
	}
	
	public Mob visibleEnemy( int index ) {
		return visibleEnemies.get( index % visibleEnemies.size() );
	}
	
	private boolean getCloser( final int target ) {
		
		if (rooted) {
			Camera.main.shake( 1, 1f );
			return false;
		}
		
		int step = -1;
		
		if (Level.adjacent( pos, target )) {
			
			if (Actor.findChar( target ) == null) {
				if (Level.pit[target] && !flying && !Chasm.jumpConfirmed) {
					Chasm.heroJump( this );
					interrupt();
					return false;
				}
				if (Level.passable[target] || Level.avoid[target]) {
					step = target;
				}
			}
			
		} else {
		
			int len = Level.LENGTH;
			boolean[] p = Level.passable;
			boolean[] v = Dungeon.level.visited;
			boolean[] m = Dungeon.level.mapped;
			boolean[] passable = new boolean[len];
			for (int i=0; i < len; i++) {
				passable[i] = p[i] && (v[i] || m[i]);
			}
			
			step = Dungeon.findPath( this, pos, target, passable, Level.fieldOfView );
		}
		
		if (step != -1) {
			
			int oldPos = pos;
			move( step );
			sprite.move( oldPos, pos );
			spend( 1 / speed() );
			
			return true;

		} else {

			return false;
			
		}

	}
	
	public boolean handle( int cell ) {
		
		if (cell == -1) {
			return false;
		}
		
		Char ch;
		Heap heap;
		
		if (Dungeon.level.map[cell] == Terrain.ALCHEMY && cell != pos) {
			
			curAction = new HeroAction.Cook( cell );
			
		} else if (Level.fieldOfView[cell] && (ch = Actor.findChar( cell )) instanceof Mob) {
			
			if (ch instanceof NPC) {
				curAction = new HeroAction.Interact( (NPC)ch );
			} else {
				curAction = new HeroAction.Attack( ch );
			}
			
		} else if (Level.fieldOfView[cell] && (heap = Dungeon.level.heaps.get( cell )) != null && heap.type != Heap.Type.HIDDEN) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().price() > 0 ? 
					new HeroAction.Buy( cell ) : 
					new HeroAction.PickUp( cell );
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}
			
		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR || Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {
			
			curAction = new HeroAction.Unlock( cell );
			
		} else if (cell == Dungeon.level.exit) {
			
			curAction = new HeroAction.Descend( cell );
			
		} else if (cell == Dungeon.level.entrance) {
			
			curAction = new HeroAction.Ascend( cell );
			
		} else  {
			
			curAction = new HeroAction.Move( cell );
			lastAction = null;
			
		}

		return act();
	}
	
	public void earnExp( int exp ) {
		
		this.exp += exp;
		
		boolean levelUp = false;
		while (this.exp >= maxExp()) {
			this.exp -= maxExp();
			lvl++;
			
			HT += 5;
			HP += 5;			
			attackSkill++;
			defenseSkill++;
			
			if (lvl < 10) {
				updateAwareness();
			}
			
			levelUp = true;
		}
		
		if (levelUp) {
			
			GLog.p( Babylon.get().getFromResources("hero_lvl_up_desc"), lvl );
			sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("hero_lvl_up"));
			Sample.INSTANCE.play( Assets.SND_LEVELUP );
			
			Badges.validateLevelReached();
		}
		
		if (subClass == HeroSubClass.WARLOCK) {
			
			int value = Math.min( HT - HP, 1 + (Dungeon.depth - 1) / 5 );
			if (value > 0) {
				HP += value;
				sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			}
			
			((Hunger)buff( Hunger.class )).satisfy( 10 );
		}
	}
	
	public int maxExp() {
		return 5 + lvl * 5;
	}
	
	void updateAwareness() {
		awareness = (float)(1 - Math.pow( 
			(heroClass == HeroClass.ROGUE ? 0.85 : 0.90), 
			(1 + Math.min( lvl,  9 )) * 0.5 
		));
	}
	
	public boolean isStarving() {
		return ((Hunger)buff( Hunger.class )).isStarving();
	}
	
	@Override
	public void add( Buff buff ) {
		super.add( buff );
		
		if (sprite != null) {
			if (buff instanceof Burning) {
				GLog.w(Babylon.get().getFromResources("hero_fire"));
				interrupt();
			} else if (buff instanceof Paralysis) {
				GLog.w(Babylon.get().getFromResources("hero_paralysed"));
				interrupt();
			} else if (buff instanceof Poison) {
				GLog.w(Babylon.get().getFromResources("hero_poisoned"));
				interrupt();
			} else if (buff instanceof Ooze) {
				GLog.w(Babylon.get().getFromResources("hero_ooze"));
			} else if (buff instanceof Roots) {
				GLog.w(Babylon.get().getFromResources("hero_roots"));
			} else if (buff instanceof Weakness) {
				GLog.w(Babylon.get().getFromResources("hero_weak"));
			} else if (buff instanceof Blindness) {
				GLog.w(Babylon.get().getFromResources("hero_blind"));
			} else if (buff instanceof Fury) {
				GLog.w(Babylon.get().getFromResources("hero_furious"));
				sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("hero_furious_title"));
			} else if (buff instanceof Charm) {
				GLog.w(Babylon.get().getFromResources("hero_charmed"));
			}  else if (buff instanceof Cripple) {
				GLog.w(Babylon.get().getFromResources("hero_crip"));
			} else if (buff instanceof Bleeding) {
				GLog.w(Babylon.get().getFromResources("hero_bleed"));
			} else if (buff instanceof Vertigo) {
				GLog.w(Babylon.get().getFromResources("hero_vertigo"));
				interrupt();
			}
			
			else if (buff instanceof Light) {
				sprite.add( CharSprite.State.ILLUMINATED );
			}
		}
		
		BuffIndicator.refreshHero();
	}
	
	@Override
	public void remove( Buff buff ) {
		super.remove( buff );
		
		if (buff instanceof Light) {
			sprite.remove( CharSprite.State.ILLUMINATED );
		}
		
		BuffIndicator.refreshHero();
	}
	
	@Override
	public int stealth() {
		int stealth = super.stealth();
		for (Buff buff : buffs( RingOfShadows.Shadows.class )) {
			stealth += ((RingOfShadows.Shadows)buff).level;
		}
		return stealth;
	}
	
	@Override
	public void die( Object cause  ) {
		
		curAction = null;
		
		DewVial.autoDrink( this );
		if (isAlive()) {
			new Flare( 8, 32 ).color( 0xFFFF66, true ).show( sprite, 2f ) ;
			return;
		}
		
		Actor.fixTime();
		super.die( cause );
		
		Ankh ankh = (Ankh)belongings.getItem( Ankh.class );
		if (ankh == null) {
			
			reallyDie( cause );
			
		} else {
			
			Dungeon.deleteGame( Dungeon.hero.heroClass, false );
			GameScene.show( new WndResurrect( ankh, cause ) );
			
		}
	}
	
	public static void reallyDie( Object cause ) {
		
		int length = Level.LENGTH;
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Level.discoverable;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					Level.set( i, Terrain.discover( terr ) );						
					GameScene.updateMap( i );
				}
			}
		}
		
		Bones.leave();
		
		Dungeon.observe();
				
		Dungeon.hero.belongings.identify();
		
		int pos = Dungeon.hero.pos;
		
		ArrayList<Integer> passable = new ArrayList<Integer>();
		for (Integer ofs : Level.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Level.passable[cell] || Level.avoid[cell]) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );
		
		ArrayList<Item> items = new ArrayList<Item>( Dungeon.hero.belongings.backpack.items );
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}
			
			Item item = Random.element( items );
			Dungeon.level.drop( item, cell ).sprite.drop( pos );
			items.remove( item );
		}
		
		GameScene.gameOver();
		
		if (cause instanceof Hero.Doom) {
			((Hero.Doom)cause).onDeath();
		}
		
		Dungeon.deleteGame( Dungeon.hero.heroClass, true );
	}
	
	@Override
	public void move( int step ) {
		super.move( step );
		
		if (!flying) {
			
			if (Level.water[pos]) {
				Sample.INSTANCE.play( Assets.SND_WATER, 1, 1, Random.Float( 0.8f, 1.25f ) );
			} else {
				Sample.INSTANCE.play( Assets.SND_STEP );
			}
			Dungeon.level.press( pos, this );
		}
	}
	
	@Override
	public void onMotionComplete() {
		Dungeon.observe();
		search( false );
			
		super.onMotionComplete();
	}
	
	@Override
	public void onAttackComplete() {
		
		AttackIndicator.target( enemy );
		
		attack( enemy );
		curAction = null;
		
		Invisibility.dispel();

		super.onAttackComplete();
	}
	
	@Override
	public void onOperateComplete() {
		
		if (curAction instanceof HeroAction.Unlock) {
			
			if (theKey != null) {
				theKey.detach( belongings.backpack );
				theKey = null;
			}
			
			int doorCell = ((HeroAction.Unlock)curAction).dst;
			int door = Dungeon.level.map[doorCell];
			
			Level.set( doorCell, door == Terrain.LOCKED_DOOR ? Terrain.DOOR : Terrain.UNLOCKED_EXIT );
			GameScene.updateMap( doorCell );
			
		} else if (curAction instanceof HeroAction.OpenChest) {
			
			if (theKey != null) {
				theKey.detach( belongings.backpack );
				theKey = null;
			}
			
			Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst ); 
			if (heap.type == Type.SKELETON) {
				Sample.INSTANCE.play( Assets.SND_BONES );
			}
			heap.open( this );
		}
		curAction = null;

		super.onOperateComplete();
	}
	
	public boolean search( boolean intentional ) {
		
		boolean smthFound = false;
		
		int positive = 0;
		int negative = 0;
		for (Buff buff : buffs( RingOfDetection.Detection.class )) {
			int bonus = ((RingOfDetection.Detection)buff).level;
			if (bonus > positive) {
				positive = bonus;
			} else if (bonus < 0) {
				negative += bonus;
			}
		}
		int distance = 1 + positive + negative;

		float level = intentional ? (2 * awareness - awareness * awareness) : awareness;
		if (distance <= 0) {
			level /= 2 - distance;
			distance = 1;
		}
		
		int cx = pos % Level.WIDTH;
		int cy = pos / Level.WIDTH;
		int ax = cx - distance;
		if (ax < 0) {
			ax = 0;
		}
		int bx = cx + distance;
		if (bx >= Level.WIDTH) {
			bx = Level.WIDTH - 1;
		}
		int ay = cy - distance;
		if (ay < 0) {
			ay = 0;
		}
		int by = cy + distance;
		if (by >= Level.HEIGHT) {
			by = Level.HEIGHT - 1;
		}
		
		for (int y = ay; y <= by; y++) {
			for (int x = ax, p = ax + y * Level.WIDTH; x <= bx; x++, p++) {
				
				if (Dungeon.visible[p]) {
					
					if (intentional) {
						sprite.parent.addToBack( new CheckedCell( p ) );
					}
					
					if (Level.secret[p] && (intentional || Random.Float() < level)) {
						
						int oldValue = Dungeon.level.map[p];
						
						GameScene.discoverTile( p, oldValue );
						
						Level.set( p, Terrain.discover( oldValue ) );	
						
						GameScene.updateMap( p );
						
						ScrollOfMagicMapping.discover( p );
						
						smthFound = true;
					}
					
					if (intentional) {
						Heap heap = Dungeon.level.heaps.get( p );
						if (heap != null && heap.type == Type.HIDDEN) {
							heap.open( this );
							smthFound = true;
						}
					}
				}
			}
		}

		
		if (intentional) {
			sprite.showStatus( CharSprite.DEFAULT, Babylon.get().getFromResources("hero_search"));
			sprite.operate( pos );
			if (smthFound) {
				spendAndNext( Random.Float() < level ? TIME_TO_SEARCH : TIME_TO_SEARCH * 2 );
			} else {
				spendAndNext( TIME_TO_SEARCH );
			}
			
		}
		
		if (smthFound) {
			GLog.w(Babylon.get().getFromResources("hero_noticed"));
			Sample.INSTANCE.play( Assets.SND_SECRET );
			interrupt();
		}
		
		return smthFound;
	}
	
	public void resurrect( int resetLevel ) {
		
		HP = HT;
		Dungeon.gold = 0;
		exp = 0;
		
		belongings.resurrect( resetLevel );
		
		live();
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		RingOfElements.Resistance r = buff( RingOfElements.Resistance.class );
		return r == null ? super.resistances() : r.resistances();
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		GasesImmunity buff = buff( GasesImmunity.class );
		return buff == null ? super.immunities() : GasesImmunity.IMMUNITIES;
	}
	
	@Override
	public void next() {
		super.next();
	}
	
	public static interface Doom {
		public void onDeath();
	}
}

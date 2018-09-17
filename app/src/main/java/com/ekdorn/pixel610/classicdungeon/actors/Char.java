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
package com.ekdorn.pixel610.classicdungeon.actors;

import java.util.HashSet;

import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.ResultDescriptions;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Amok;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Bleeding;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Charm;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Vertigo;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Cripple;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Frost;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Light;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Shadows;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Sleep;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Speed;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Levitation;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.MindVision;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Paralysis;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Poison;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Slow;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Terror;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroSubClass;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Bestiary;
import com.ekdorn.pixel610.classicdungeon.effects.CellEmitter;
import com.ekdorn.pixel610.classicdungeon.effects.particles.PoisonParticle;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.levels.Terrain;
import com.ekdorn.pixel610.classicdungeon.levels.features.Door;
import com.ekdorn.pixel610.classicdungeon.sprites.CharSprite;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundlable;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.GameMath;
import com.ekdorn.pixel610.utils.Random;

public abstract class Char extends Actor {

	public int pos = 0;
	
	public CharSprite sprite;
	
	public String name = "mob";
	
	public int HT;
	public int HP;
	
	protected float baseSpeed	= 1;
	
	public boolean paralysed	= false;
	public boolean rooted		= false;
	public boolean flying		= false;
	public int invisible		= 0;
	
	public int viewDistance	= 8;
	
	private HashSet<Buff> buffs = new HashSet<Buff>();
	
	@Override
	protected boolean act() {
		Dungeon.level.updateFieldOfView( this );
		return false;
	}
	
	private static final String POS			= "pos";
	private static final String TAG_HP		= "HP";
	private static final String TAG_HT		= "HT";
	private static final String BUFFS		= "buffs";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );
		
		bundle.put( POS, pos );
		bundle.put( TAG_HP, HP );
		bundle.put( TAG_HT, HT );
		bundle.put( BUFFS, buffs );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );
		
		pos = bundle.getInt( POS );
		HP = bundle.getInt( TAG_HP );
		HT = bundle.getInt( TAG_HT );
		
		for (Bundlable b : bundle.getCollection( BUFFS )) {
			if (b != null) {
				((Buff)b).attachTo( this );
			}
		}
	}
	
	public boolean attack( Char enemy ) {
		
		boolean visibleFight = Dungeon.visible[pos] || Dungeon.visible[enemy.pos];
		
		if (hit( this, enemy, false )) {
			
			if (visibleFight) {
				GLog.i( Babylon.get().getFromResources("char_hit"), name, enemy.name );
			}
			
			// FIXME
			int dr = this instanceof Hero && ((Hero)this).rangedWeapon != null && ((Hero)this).subClass == HeroSubClass.SNIPER ? 0 :
				Random.IntRange( 0, enemy.dr() );
			
			int dmg = damageRoll();
			int effectiveDamage = Math.max( dmg - dr, 0 );
			
			effectiveDamage = attackProc( enemy, effectiveDamage );
			effectiveDamage = enemy.defenseProc( this, effectiveDamage );
			enemy.damage( effectiveDamage, this );
			
			if (visibleFight) {
				Sample.INSTANCE.play( Assets.SND_HIT, 1, 1, Random.Float( 0.8f, 1.25f ) );
			}

			if (enemy == Dungeon.hero) {
				Dungeon.hero.interrupt();
				if (effectiveDamage > enemy.HT / 4) {
					Camera.main.shake( GameMath.gate( 1, effectiveDamage / (enemy.HT / 4), 5), 0.3f );
				}
			}
			
			//enemy.sprite.bloodBurstA( sprite.center(), effectiveDamage );
			enemy.sprite.flash();
			
			if (!enemy.isAlive() && visibleFight) {
				if (enemy == Dungeon.hero) {
					
					if (Dungeon.hero.killerGlyph != null) {
						
					// FIXME
					//	Dungeon.fail( Utils.format( ResultDescriptions.GLYPH, Dungeon.hero.killerGlyph.name(), Dungeon.depth ) );
					//	GLog.n( TXT_KILL, Dungeon.hero.killerGlyph.name() );
						
					} else {
						if (Bestiary.isBoss( this )) {
							Dungeon.fail( Utils.format( ResultDescriptions.BOSS, name, Dungeon.depth ) );
						} else {
							Dungeon.fail( Utils.format( ResultDescriptions.MOB, 
								Utils.indefinite( name ), Dungeon.depth ) );
						}
						
						GLog.n( Babylon.get().getFromResources("char_kill"), name );
					}
					
				} else {
					GLog.i( Babylon.get().getFromResources("char_defeat"), name, enemy.name );
				}
			}
			
			return true;
			
		} else {
			
			if (visibleFight) {
				String defense = enemy.defenseVerb();
				enemy.sprite.showStatus( CharSprite.NEUTRAL, defense );
				if (this == Dungeon.hero) {
					GLog.i( Babylon.get().getFromResources("char_you_miss"), enemy.name, defense );
				} else {
					GLog.i( Babylon.get().getFromResources("char_enemy_miss"), enemy.name, defense, name );
				}
				
				Sample.INSTANCE.play( Assets.SND_MISS );
			}
			
			return false;
			
		}
	}
	
	public static boolean hit( Char attacker, Char defender, boolean magic ) {
		float acuRoll = Random.Float( attacker.attackSkill( defender ) );
		float defRoll = Random.Float( defender.defenseSkill( attacker ) );
		return (magic ? acuRoll * 2 : acuRoll) >= defRoll;
	}
	
	public int attackSkill( Char target ) {
		return 0;
	}
	
	public int defenseSkill( Char enemy ) {
		return 0;
	}
	
	public String defenseVerb() {
		return Babylon.get().getFromResources("defmod_dodged");
	}
	
	public int dr() {
		return 0;
	}
	
	public int damageRoll() {
		return 1;
	}
	
	public int attackProc( Char enemy, int damage ) {
		return damage;
	}
	
	public int defenseProc( Char enemy, int damage ) {
		return damage;
	}
	
	public float speed() {
		return buff( Cripple.class ) == null ? baseSpeed : baseSpeed * 0.5f;
	}
	
	public void damage( int dmg, Object src ) {
		
		if (HP <= 0) {
			return;
		}
		
		Buff.detach( this, Frost.class );
		
		Class<?> srcClass = src.getClass();
		if (immunities().contains( srcClass )) {
			dmg = 0;
		} else if (resistances().contains( srcClass )) {
			dmg = Random.IntRange( 0, dmg );
		}
		
		if (buff( Paralysis.class ) != null) {
			if (Random.Int( dmg ) >= Random.Int( HP )) {
				Buff.detach( this, Paralysis.class );
				if (Dungeon.visible[pos]) {
					GLog.i( Babylon.get().getFromResources("char_paralyse"), name );
				}
			}
		}
		
		HP -= dmg;
		if (dmg > 0 || src instanceof Char) {
			sprite.showStatus( HP > HT / 2 ? 
				CharSprite.WARNING : 
				CharSprite.NEGATIVE,
				Integer.toString( dmg ) );
		}
		if (HP <= 0) {
			die( src );
		}
	}
	
	public void destroy() {
		HP = 0;
		Actor.remove( this );
		Actor.freeCell( pos );
	}
	
	public void die( Object src ) {
		destroy();
		sprite.die();
	}
	
	public boolean isAlive() {
		return HP > 0;
	}
	
	@Override
	protected void spend( float time ) {
		
		float timeScale = 1f;
		if (buff( Slow.class ) != null) {
			timeScale *= 0.5f;
		}
		if (buff( Speed.class ) != null) {
			timeScale *= 2.0f;
		}
		
		super.spend( time / timeScale );
	}
	
	public HashSet<Buff> buffs() {
		return buffs;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Buff> HashSet<T> buffs( Class<T> c ) {
		HashSet<T> filtered = new HashSet<T>();
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				filtered.add( (T)b );
			}
		}
		return filtered;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Buff> T buff( Class<T> c ) {
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				return (T)b;
			}
		}
		return null;
	}
	
	public boolean isCharmedBy( Char ch ) {
		int chID = ch.id();
		for (Buff b : buffs) {
			if (b instanceof Charm && ((Charm)b).object == chID) {
				return true;
			}
		}
		return false;
	}
	
	public void add( Buff buff ) {
		
		buffs.add( buff );
		Actor.add( buff );
		
		if (sprite != null) {
			if (buff instanceof Poison) {
				
				CellEmitter.center( pos ).burst( PoisonParticle.SPLASH, 5 );
				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_poisoned"));
				
			} else if (buff instanceof Amok) {
				
				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_akok"));

			} else if (buff instanceof Slow) {

				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_slowed"));
				
			} else if (buff instanceof MindVision) {
				
				sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("char_mindvision0"));
				sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("char_mindvision1"));
				
			} else if (buff instanceof Paralysis) {

				sprite.add( CharSprite.State.PARALYSED );
				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_paralysed"));
				
			} else if (buff instanceof Terror) {
				
				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_frightened"));
				
			} else if (buff instanceof Roots) {
				
				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_rooted"));
				
			} else if (buff instanceof Cripple) {

				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_crippled"));
				
			} else if (buff instanceof Bleeding) {

				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_bleeding"));
				
			} else if (buff instanceof Vertigo) {

				sprite.showStatus( CharSprite.NEGATIVE, Babylon.get().getFromResources("char_dizzy"));
				
			} else if (buff instanceof Sleep) {
				sprite.idle();
			}
			
			  else if (buff instanceof Burning) {
				sprite.add( CharSprite.State.BURNING );
			} else if (buff instanceof Levitation) {
				sprite.add( CharSprite.State.LEVITATING );
			} else if (buff instanceof Frost) {
				sprite.add( CharSprite.State.FROZEN );
			} else if (buff instanceof Invisibility) {
				if (!(buff instanceof Shadows)) {
					sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("char_invisible"));
				}
				sprite.add( CharSprite.State.INVISIBLE );
			}
		}
	}
	
	public void remove( Buff buff ) {
		
		buffs.remove( buff );
		Actor.remove( buff );
		
		if (buff instanceof Burning) {
			sprite.remove( CharSprite.State.BURNING );
		} else if (buff instanceof Levitation) {
			sprite.remove( CharSprite.State.LEVITATING );
		} else if (buff instanceof Invisibility && invisible <= 0) {
			sprite.remove( CharSprite.State.INVISIBLE );
		} else if (buff instanceof Paralysis) {
			sprite.remove( CharSprite.State.PARALYSED );
		} else if (buff instanceof Frost) {
			sprite.remove( CharSprite.State.FROZEN );
		} 
	}
	
	public void remove( Class<? extends Buff> buffClass ) {
		for (Buff buff : buffs( buffClass )) {
			remove( buff );
		}
	}
	
	
	
	@Override
	protected void onRemove() {
		for (Buff buff : buffs.toArray( new Buff[0] )) {
			buff.detach();
		}
	}
	
	public void updateSpriteState() {
		for (Buff buff:buffs) {
			if (buff instanceof Burning) {
				sprite.add( CharSprite.State.BURNING );
			} else if (buff instanceof Levitation) {
				sprite.add( CharSprite.State.LEVITATING );
			} else if (buff instanceof Invisibility) {
				sprite.add( CharSprite.State.INVISIBLE );
			} else if (buff instanceof Paralysis) {
				sprite.add( CharSprite.State.PARALYSED );
			} else if (buff instanceof Frost) {
				sprite.add( CharSprite.State.FROZEN );
			} else if (buff instanceof Light) {
				sprite.add( CharSprite.State.ILLUMINATED );
			}
		}
	}
	
	public int stealth() {
		return 0;
	}
	
	public void move( int step ) {
		
		if (Level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
			step = pos + Level.NEIGHBOURS8[Random.Int( 8 )];
			if (!(Level.passable[step] || Level.avoid[step]) || Actor.findChar( step ) != null) {
				return;
			}
		}
		
		if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
			Door.leave( pos );
		}
		
		pos = step;
		
		if (flying && Dungeon.level.map[pos] == Terrain.DOOR) {
			Door.enter( pos );
		}
		
		if (this != Dungeon.hero) {
			sprite.visible = Dungeon.visible[pos];
		}
	}
	
	public int distance( Char other ) {
		return Level.distance( pos, other.pos );
	}
	
	public void onMotionComplete() {
		next();
	}
	
	public void onAttackComplete() {
		next();
	}
	
	public void onOperateComplete() {
		next();
	}
	
	private static final HashSet<Class<?>> EMPTY = new HashSet<Class<?>>();
	
	public HashSet<Class<?>> resistances() {
		return EMPTY;
	}
	
	public HashSet<Class<?>> immunities() {
		return EMPTY;
	}
}

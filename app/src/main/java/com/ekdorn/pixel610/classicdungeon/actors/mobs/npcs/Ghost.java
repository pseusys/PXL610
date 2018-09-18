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
package com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs;

import java.util.HashSet;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.tweeners.AlphaTweener;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Challenges;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.Journal;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Paralysis;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.CursePersonification;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.FetidRat;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.classicdungeon.effects.CellEmitter;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.effects.particles.ShadowParticle;
import com.ekdorn.pixel610.classicdungeon.items.Generator;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor;
import com.ekdorn.pixel610.classicdungeon.items.armor.ClothArmor;
import com.ekdorn.pixel610.classicdungeon.items.quest.DriedRose;
import com.ekdorn.pixel610.classicdungeon.items.quest.RatSkull;
import com.ekdorn.pixel610.classicdungeon.items.weapon.Weapon;
import com.ekdorn.pixel610.classicdungeon.items.weapon.missiles.MissileWeapon;
import com.ekdorn.pixel610.classicdungeon.levels.SewerLevel;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.sprites.GhostSprite;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.classicdungeon.windows.WndGhost;
import com.ekdorn.pixel610.classicdungeon.windows.WndQuest;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Ghost extends NPC {

	{
		name = Babylon.get().getFromResources("mob_ghost");
		spriteClass = GhostSprite.class;
		
		flying = true;
		
		state = WANDERING;
	}
	
	public Ghost() {
		super();

		Sample.INSTANCE.load( Assets.SND_GHOST );
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return 1000;
	}
	
	@Override
	public String defenseVerb() {
		return Babylon.get().getFromResources("defmod_evade");
	}
	
	@Override
	public float speed() {
		return 0.5f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public void interact() {
		sprite.turnTo( pos, Dungeon.hero.pos );
		Sample.INSTANCE.play( Assets.SND_GHOST );
		
		Quest.type.handler.interact( this );
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_ghost_desc");
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( Paralysis.class );
		IMMUNITIES.add( Roots.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
	
	public static void replace( final Mob a, final Mob b ) {
		final float FADE_TIME = 0.5f;
		
		a.destroy();
		a.sprite.parent.add( new AlphaTweener( a.sprite, 0, FADE_TIME ) {
			protected void onComplete() {
				a.sprite.killAndErase();
				parent.erase( this );
			};
		} );
		
		b.pos = a.pos;
		GameScene.add( b );
		
		b.sprite.flipHorizontal = a.sprite.flipHorizontal;
		b.sprite.alpha( 0 );
		b.sprite.parent.add( new AlphaTweener( b.sprite, 1, FADE_TIME ) );
	}

	public static class Quest {

		enum Type {
			ILLEGAL( null ), ROSE( roseQuest ), RAT( ratQuest ), CURSE( curseQuest );
			
			public QuestHandler handler;
			private Type( QuestHandler handler ) {
				this.handler = handler;
			}
		}
		
		private static Type type;
		
		private static boolean spawned;
		private static boolean given;
		private static boolean processed;
		
		private static int depth;
		
		private static int left2kill;
		
		public static Weapon weapon;
		public static Armor armor;
		
		public static void reset() {
			spawned = false; 
			
			weapon = null;
			armor = null;
		}
		
		private static final String NODE		= "sadGhost";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE		= "type";
		private static final String ALTERNATIVE	= "alternative";
		private static final String LEFT2KILL	= "left2kill";
		private static final String GIVEN		= "given";
		private static final String PROCESSED	= "processed";
		private static final String DEPTH		= "depth";
		private static final String WEAPON		= "weapon";
		private static final String ARMOR		= "armor";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( TYPE, type.toString() );
				if (type == Type.ROSE) {
					node.put( LEFT2KILL, left2kill );
				}
				
				node.put( GIVEN, given );
				node.put( DEPTH, depth );
				node.put( PROCESSED, processed );
				
				node.put( WEAPON, weapon );
				node.put( ARMOR, armor );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {
			
			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				
				type = node.getEnum( TYPE, Type.class );
				if (type == Type.ILLEGAL) {
					type = node.getBoolean( ALTERNATIVE ) ? Type.RAT : Type.ROSE;
				}
				if (type == Type.ROSE) {
					left2kill = node.getInt( LEFT2KILL );
				}
				
				given	= node.getBoolean( GIVEN );
				depth	= node.getInt( DEPTH );
				processed	= node.getBoolean( PROCESSED );
				
				weapon	= (Weapon)node.get( WEAPON );
				armor	= (Armor)node.get( ARMOR );
			} else {
				reset();
			}
		}
		
		public static void spawn( SewerLevel level ) {
			if (!spawned && Dungeon.depth > 1 && Random.Int( 5 - Dungeon.depth ) == 0) {
				
				Ghost ghost = new Ghost();
				do {
					ghost.pos = level.randomRespawnCell();
				} while (ghost.pos == -1);
				level.mobs.add( ghost );
				Actor.occupyCell( ghost );
				
				spawned = true;
				switch (Random.Int( 3 )) {
				case 0:
					type = Type.ROSE;
					left2kill = 8;
					break;
				case 1:
					type = Type.RAT;
					break;
				case 2:
					type = Type.CURSE;
					break;
				}
				
				given = false;
				processed = false;
				depth = Dungeon.depth;
				
				for (int i=0; i < 4; i++) {
					Item another;
					do {
						another = (Weapon)Generator.random( Generator.Category.WEAPON );
					} while (another instanceof MissileWeapon);
					
					if (weapon == null || another.level() > weapon.level()) {
						weapon = (Weapon)another;
					}
				}
				
				if (Dungeon.isChallenged( Challenges.NO_ARMOR )) {
					armor = (Armor)new ClothArmor().degrade();
				} else {
					armor = (Armor)Generator.random( Generator.Category.ARMOR );
					for (int i=0; i < 3; i++) {
						Item another = Generator.random( Generator.Category.ARMOR );
						if (another.level() > armor.level()) {
							armor = (Armor)another;
						}
					}
				}
				
				weapon.identify();
				armor.identify();
			}
		}

		public static void processSewersKill( int pos ) {
			if (spawned && given && !processed && (depth == Dungeon.depth)) {
				switch (type) {
				case ROSE:
					if (Random.Int( left2kill ) == 0) {
						Dungeon.level.drop( new DriedRose(), pos ).sprite.drop();
						processed = true;
					} else {
						left2kill--;
					}
					break;
				case RAT:
					FetidRat rat = new FetidRat();
					rat.pos = Dungeon.level.randomRespawnCell();
					if (rat.pos != -1) {
						GameScene.add( rat );
						processed = true;
					}
					break;
				default:
				}
			}
		}
		
		public static void complete() {
			weapon = null;
			armor = null;
			
			Journal.remove( Journal.Feature.GHOST );
		}
	}

	abstract public static class QuestHandler {
		
		abstract public void interact(  Ghost ghost ); 
		
		protected void relocate( Ghost ghost ) {
			int newPos = -1;
			for (int i=0; i < 10; i++) {
				newPos = Dungeon.level.randomRespawnCell();
				if (newPos != -1) {
					break;
				}
			}
			if (newPos != -1) {
				
				Actor.freeCell( ghost.pos );
				
				CellEmitter.get( ghost.pos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
				ghost.pos = newPos;
				ghost.sprite.place( ghost.pos );
				ghost.sprite.visible = Dungeon.visible[ghost.pos];
			}
		}
	}
	
	private static final QuestHandler roseQuest = new QuestHandler() {

		public void interact( Ghost ghost ) {
			if (Quest.given) {

				Item item = Dungeon.hero.belongings.getItem( DriedRose.class );	
				if (item != null) {
					GameScene.show( new WndGhost( ghost, Babylon.get().getFromResources("mob_ghost_completed0"), Quest.armor, Quest.weapon, item ) );
				} else {
					GameScene.show( new WndQuest( ghost, Babylon.get().getFromResources("mob_ghost_quest1") ) );
					relocate( ghost );
				}
				
			} else {
				GameScene.show( new WndQuest( ghost, Babylon.get().getFromResources("mob_ghost_quest0") ) );
				Quest.given = true;
				
				Journal.add( Journal.Feature.GHOST );
			}
		}
	};
	
	private static final QuestHandler ratQuest = new QuestHandler() {
		
		public void interact( Ghost ghost ) {
			if (Quest.given) {

				Item item = Dungeon.hero.belongings.getItem( RatSkull.class );	
				if (item != null) {
					GameScene.show( new WndGhost( ghost, Babylon.get().getFromResources("mob_ghost_completed1"), Quest.armor, Quest.weapon, item ) );
				} else {
					GameScene.show( new WndQuest( ghost, Babylon.get().getFromResources("mob_ghost_quest3") ) );
					relocate( ghost );
				}
				
			} else {
				GameScene.show( new WndQuest( ghost, Babylon.get().getFromResources("mob_ghost_quest2") ) );
				Quest.given = true;
				
				Journal.add( Journal.Feature.GHOST );
			}
		}
	};
	
	private static final QuestHandler curseQuest = new QuestHandler() {
		
		public void interact( final Ghost ghost ) {
			if (Quest.given) {

				GameScene.show( new WndGhost( ghost, Utils.format( Babylon.get().getFromResources("mob_ghost_completed3"), Dungeon.hero.className() ), Quest.armor, Quest.weapon, null ) );
				
			} else {
				GameScene.show( new WndQuest( ghost, Babylon.get().getFromResources("mob_ghost_quest4"),
						Babylon.get().getFromResources("mob_ghost_quest_yes"), Babylon.get().getFromResources("mob_ghost_quest_no") ) {
					protected void onSelect( int index ) {
						if (index == 0) {
							Quest.given = true;
							
							CursePersonification d = new CursePersonification();
							Ghost.replace( ghost, d );
							
							d.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
							Sample.INSTANCE.play( Assets.SND_GHOST );
							
							Dungeon.hero.next();
						} else {
							relocate( ghost );
						}
					};
				} );
				
				Journal.add( Journal.Feature.GHOST );
			}
		}
	};
}
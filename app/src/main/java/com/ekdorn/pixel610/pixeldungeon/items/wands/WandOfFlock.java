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
package com.ekdorn.pixel610.pixeldungeon.items.wands;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.NPC;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.MagicMissile;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.SheepSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.BArray;
import com.ekdorn.pixel610.utils.Callback;
import com.ekdorn.pixel610.utils.PathFinder;
import com.ekdorn.pixel610.utils.Random;

public class WandOfFlock extends Wand {

	{
		name = Babylon.get().getFromResources("wand_flock");
	}
	
	@Override
	protected void onZap( int cell ) {
		
		int level = power();
		
		int n = level + 2;
		
		if (Actor.findChar( cell ) != null && Ballistica.distance > 2) {
			cell = Ballistica.trace[Ballistica.distance - 2];
		}
		
		boolean[] passable = BArray.or( Level.passable, Level.avoid, null );
		for (Actor actor : Actor.all()) {
			if (actor instanceof Char) {
				passable[((Char)actor).pos] = false;
			}
		}
		
		PathFinder.buildDistanceMap( cell, passable, n );
		int dist = 0;
		
		if (Actor.findChar( cell ) != null) {
			PathFinder.distance[cell] = Integer.MAX_VALUE;
			dist = 1;
		}
		
		float lifespan = level + 3;
		
	sheepLabel:
		for (int i=0; i < n; i++) {
			do {
				for (int j=0; j < Level.LENGTH; j++) {
					if (PathFinder.distance[j] == dist) {
						
						Sheep sheep = new Sheep();
						sheep.lifespan = lifespan;
						sheep.pos = j;
						GameScene.add( sheep );
						Dungeon.level.mobPress( sheep );
						
						CellEmitter.get( j ).burst( Speck.factory( Speck.WOOL ), 4 );
						
						PathFinder.distance[j] = Integer.MAX_VALUE;
						
						continue sheepLabel;
					}
				}
				dist++;
			} while (dist < n);
		}
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.wool( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("wand_flock_desc");
	}
	
	public static class Sheep extends NPC {
		
		private static final String[] QUOTES = {Babylon.get().getFromResources("mob_sheep_quote0"),
				Babylon.get().getFromResources("mob_sheep_quote1"),
				Babylon.get().getFromResources("mob_sheep_quote2"),
				Babylon.get().getFromResources("mob_sheep_quote3")};
		
		{
			name = Babylon.get().getFromResources("mob_sheep");
			spriteClass = SheepSprite.class;
		}
		
		public float lifespan;
		
		private boolean initialized = false;
		
		@Override
		protected boolean act() {
			if (initialized) {
				HP = 0;

				destroy();
				sprite.die();
				
			} else {
				initialized = true;
				spend( lifespan + Random.Float( 2 ) );
			}
			return true;
		}
		
		@Override
		public void damage( int dmg, Object src ) {
		}
		
		@Override
		public String description() {
			return Babylon.get().getFromResources("mob_sheep_desc");
		}

		@Override
		public void interact() {
			yell( Random.element( QUOTES ) );
		}
	}
}

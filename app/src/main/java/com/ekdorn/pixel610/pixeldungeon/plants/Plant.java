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
package com.ekdorn.pixel610.pixeldungeon.plants;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Barkskin;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroSubClass;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.LeafParticle;
import com.ekdorn.pixel610.pixeldungeon.items.Dewdrop;
import com.ekdorn.pixel610.pixeldungeon.items.Generator;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.levels.Terrain;
import com.ekdorn.pixel610.pixeldungeon.sprites.PlantSprite;
import com.ekdorn.pixel610.utils.Bundlable;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Plant implements Bundlable {

	public String plantName;
	
	public int image;
	public int pos;
	
	public PlantSprite sprite;
	
	public void activate( Char ch ) {
		
		if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
			Buff.affect( ch, Barkskin.class ).level( ch.HT / 3 );
		}
		
		wither();
	}
	
	public void wither() {
		Dungeon.level.uproot( pos );
		
		sprite.kill();
		if (Dungeon.visible[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}
		
		if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
			if (Random.Int( 5 ) == 0) {
				Dungeon.level.drop( Generator.random( Generator.Category.SEED ), pos ).sprite.drop();
			}
			if (Random.Int( 5 ) == 0) {
				Dungeon.level.drop( new Dewdrop(), pos ).sprite.drop();
			}
		}
	}
	
	private static final String POS	= "pos";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
	}
	
	public String desc() {
		return null;
	}
	
	public static class Seed extends Item {
		
		private static final float TIME_TO_PLANT = 1f;
		
		{
			stackable = true;	
			defaultAction = Babylon.get().getFromResources("item_acthrow");
		}
		
		protected Class<? extends Plant> plantClass;
		protected String plantName;
		
		public Class<? extends Item> alchemyClass;
		
		@Override
		public ArrayList<String> actions( Hero hero ) {
			ArrayList<String> actions = super.actions( hero );
			actions.add( Babylon.get().getFromResources("plants_acplant") );
			return actions;
		}
		
		@Override
		protected void onThrow( int cell ) {
			if (Dungeon.level.map[cell] == Terrain.ALCHEMY || Level.pit[cell]) {
				super.onThrow( cell );
			} else {
				Dungeon.level.plant( this, cell );
			}
		}
		
		@Override
		public void execute( Hero hero, String action ) {
			if (action.equals( Babylon.get().getFromResources("plants_acplant") )) {
							
				hero.spend( TIME_TO_PLANT );
				hero.busy();
				((Seed)detach( hero.belongings.backpack )).onThrow( hero.pos );
				
				hero.sprite.operate( hero.pos );
				
			} else {
				
				super.execute (hero, action );
				
			}
		}
		
		public Plant couch( int pos ) {
			try {
				if (Dungeon.visible[pos]) {
					Sample.INSTANCE.play( Assets.SND_PLANT );
				}
				Plant plant = plantClass.newInstance();
				plant.pos = pos;
				return plant;
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		public boolean isUpgradable() {
			return false;
		}
		
		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public int price() {
			return 10 * quantity;
		}
		
		@Override
		public String info() { 
			return String.format( Babylon.get().getFromResources("plants_seed"), desc() );
		}
	}
}

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
package com.ekdorn.pixel610.pixeldungeon.items;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.particles.Emitter;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Light;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.FlameParticle;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;

public class Torch extends Item {
	
	public static final float TIME_TO_LIGHT = 1;
	
	{
		name = Babylon.get().getFromResources("torch_name");
		image = ItemSpriteSheet.TORCH;
		
		stackable = true;
		
		defaultAction = Babylon.get().getFromResources("torch_aclight");
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( Babylon.get().getFromResources("torch_aclight") );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		
		if (action == Babylon.get().getFromResources("torch_aclight")) {
			
			hero.spend( TIME_TO_LIGHT );
			hero.busy();
			
			hero.sprite.operate( hero.pos );
			
			detach( hero.belongings.backpack );
			Buff.affect( hero, Light.class, Light.DURATION );
			
			Emitter emitter = hero.sprite.centerEmitter();
			emitter.start( FlameParticle.FACTORY, 0.2f, 3 );
			
		} else {
			
			super.execute( hero, action );
			
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
		return Babylon.get().getFromResources("torch_info");
	}
}

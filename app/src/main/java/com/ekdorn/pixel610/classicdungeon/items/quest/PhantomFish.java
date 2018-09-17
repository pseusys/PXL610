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
package com.ekdorn.pixel610.classicdungeon.items.quest;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;

public class PhantomFish extends Item {
	
	private static final float TIME_TO_EAT	= 2f;

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("quest_phantomfish");
		image = ItemSpriteSheet.PHANTOM;

		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( Babylon.get().getFromResources("food_action") );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		if (action.equals( Babylon.get().getFromResources("food_action") )) {
			
			detach( hero.belongings.backpack );
			
			hero.sprite.operate( hero.pos );
			hero.busy();
			Sample.INSTANCE.play( Assets.SND_EAT );
			Sample.INSTANCE.play( Assets.SND_MELD );
			
			GLog.i(Babylon.get().getFromResources("quest_phantomfish_exec"));
			Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
			
			hero.spend( TIME_TO_EAT );
			
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
	public String info() {
		return Babylon.get().getFromResources("quest_phantomfish_desc");
	}
}

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
package com.ekdorn.pixel610.pixeldungeon.windows;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.Wandmaker;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class WndWandmaker extends WndQuest {
	
	private Wandmaker wandmaker;
	private Item questItem;
	
	
	public WndWandmaker( final Wandmaker wandmaker, final Item item ) {
		
		super( wandmaker, Babylon.get().getFromResources("wnd_wandmaker_message"),
				Babylon.get().getFromResources("wnd_wandmaker_battle"), Babylon.get().getFromResources("wnd_wandmaker_nonbattle") );
		
		this.wandmaker = wandmaker;
		questItem = item;
	}
	
	@Override
	protected void onSelect( int index ) {

		questItem.detach( Dungeon.hero.belongings.backpack );
		
		Item reward = index == 0 ? Wandmaker.Quest.wand1 : Wandmaker.Quest.wand2;
		reward.identify();
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Babylon.get().getFromResources("hero_you_have"), reward.name() );
		} else {
			Dungeon.level.drop( reward, wandmaker.pos ).sprite.drop();
		}
		
		wandmaker.yell( Utils.format( Babylon.get().getFromResources("wnd_wandmaker_farewell"), Dungeon.hero.className() ) );
		wandmaker.destroy();
		
		wandmaker.sprite.die();
		
		Wandmaker.Quest.complete();
	}
}

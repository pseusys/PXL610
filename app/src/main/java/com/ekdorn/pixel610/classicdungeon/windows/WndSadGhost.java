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
package com.ekdorn.pixel610.classicdungeon.windows;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.npcs.Ghost;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;

public class WndSadGhost extends WndQuest {
	
	private Ghost ghost;
	private Item questItem;
	
	public WndSadGhost( final Ghost ghost, final Item item, String text ) {
		
		super( ghost, text, Babylon.get().getFromResources("wnd_ghost_weapon"), Babylon.get().getFromResources("wnd_ghost_armor") );
		
		this.ghost = ghost;
		questItem = item;
	}
	
	@Override
	protected void onSelect( int index ) {

		if (questItem != null) {
			questItem.detach( Dungeon.hero.belongings.backpack );
		}
		
		Item reward = index == 0 ? Ghost.Quest.weapon : Ghost.Quest.armor;
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Babylon.get().getFromResources("hero_you_have"), reward.name() );
		} else {
			Dungeon.level.drop( reward, ghost.pos ).sprite.drop();
		}
		
		ghost.yell(Babylon.get().getFromResources("wnd_ghost_farewell"));
		ghost.die( null );
		
		Ghost.Quest.complete();
	}
}

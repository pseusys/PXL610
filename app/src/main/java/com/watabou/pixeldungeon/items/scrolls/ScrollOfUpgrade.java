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
package com.watabou.pixeldungeon.items.scrolls;

import com.watabou.pixeldungeon.Babylon;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.windows.WndBag;

public class ScrollOfUpgrade extends InventoryScroll {
	
	{
		name = Babylon.get().getFromResources("scroll_upgrade");
		inventoryTitle = Babylon.get().getFromResources("scroll_upgrade_title");
		mode = WndBag.Mode.UPGRADEABLE;
	}
	
	@Override
	protected void onItemSelected( Item item ) {

		ScrollOfRemoveCurse.uncurse( Dungeon.hero, item );
		if (item.isBroken()) {
			item.fix();
		} else {
			item.upgrade();
		}
		
		upgrade( curUser );
		GLog.p( Babylon.get().getFromResources("scroll_upgrade_looksbetter"), item.name() );
		
		Badges.validateItemLevelAquired( item );
	}
	
	public static void upgrade( Hero hero ) {
		hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_upgrade_desc");
	}
}

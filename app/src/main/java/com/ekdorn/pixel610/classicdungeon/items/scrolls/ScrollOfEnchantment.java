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
package com.ekdorn.pixel610.classicdungeon.items.scrolls;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.effects.Enchanting;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.items.armor.Armor;
import com.ekdorn.pixel610.classicdungeon.items.weapon.Weapon;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.windows.WndBag;

public class ScrollOfEnchantment extends InventoryScroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_enchantment");
		inventoryTitle = Babylon.get().getFromResources("scroll_enchantment_title");
		mode = WndBag.Mode.ENCHANTABLE;
	}
	
	@Override
	protected void onItemSelected( Item item ) {

		ScrollOfRemoveCurse.uncurse( Dungeon.hero, item );
		
		if (item instanceof Weapon) {
			
			((Weapon)item).enchant();
			
		} else {

			((Armor)item).inscribe();
		
		}
		
		item.fix();
		
		curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
		Enchanting.show( curUser, item );
		GLog.w( Babylon.get().getFromResources("scroll_enchantment_glows"), item.name() );
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_enchantment_desc");
	}
}

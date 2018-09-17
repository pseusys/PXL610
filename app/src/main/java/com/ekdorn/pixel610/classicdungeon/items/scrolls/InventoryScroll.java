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

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.windows.WndBag;
import com.ekdorn.pixel610.classicdungeon.windows.WndOptions;

public abstract class InventoryScroll extends Scroll {

	protected WndBag.Mode mode = WndBag.Mode.ALL;

	protected static String inventoryTitle = Babylon.get().getFromResources("scroll_itemselect");
	
	@Override
	protected void doRead() {
		
		if (!isKnown()) {
			setKnown();
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}
		
		GameScene.selectItem( itemSelector, mode, inventoryTitle );
	}
	
	private void confirmCancelation() {
		GameScene.show( new WndOptions( name(), Babylon.get().getFromResources("scroll_warning"),
				Babylon.get().getFromResources("scroll_yes"), Babylon.get().getFromResources("scroll_no") ) {
			@Override
			protected void onSelect( int index ) {
				switch (index) {
				case 0:
					curUser.spendAndNext( TIME_TO_READ );
					identifiedByUse = false;
					break;
				case 1:
					GameScene.selectItem( itemSelector, mode, inventoryTitle );
					break;
				}
			}
			public void onBackPressed() {};
		} );
	}
	
	protected abstract void onItemSelected( Item item );
	
	protected static boolean identifiedByUse = false;
	protected static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {

				((InventoryScroll)curItem).onItemSelected( item );
				((InventoryScroll)curItem).readAnimation();
				
				Sample.INSTANCE.play( Assets.SND_READ );
				Invisibility.dispel();
				
			} else if (identifiedByUse) {
				
				((InventoryScroll)curItem).confirmCancelation();
				
			} else {

				curItem.collect( curUser.belongings.backpack );
				
			}
		}
	};
}

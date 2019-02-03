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
package com.ekdorn.pixel610.pixeldungeon.items.armor;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;


public class PlateArmor extends Armor {

	@Override
	public void finish() {
		switch (depth) {
			case 0:
				name = Babylon.get().getFromResources("armor_plate_0");
				break;
			case 1:
				name = Babylon.get().getFromResources("armor_plate_1");
				break;
			case 2:
				name = Babylon.get().getFromResources("armor_plate_2");
				break;
			case 3:
				name = Babylon.get().getFromResources("armor_plate_3");
				break;
			case 4:
				name = Babylon.get().getFromResources("armor_plate_4");
				break;
			default:
				name = Babylon.get().getFromResources("armor_plate_0");
				break;
		}
		image = ItemSpriteSheet.ARMOR_PLATE;
	}
	
	public PlateArmor() {
		super( 5 );
	}
	
	@Override
	public String desc() {
		switch (depth) {
			case 0:
				return Babylon.get().getFromResources("armor_plate_desc_0");
			case 1:
				return Babylon.get().getFromResources("armor_plate_desc_1");
			case 2:
				return Babylon.get().getFromResources("armor_plate_desc_2");
			case 3:
				return Babylon.get().getFromResources("armor_plate_desc_3");
			case 4:
				return Babylon.get().getFromResources("armor_plate_desc_4");
			default:
				return Babylon.get().getFromResources("armor_plate_desc_0");
		}
	}
}

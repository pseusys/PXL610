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
package com.ekdorn.pixel610.pixeldungeon.items.weapon.melee;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;

public class WarHammer extends MeleeWeapon {

	@Override
	public void finish() {
		switch (depth) {
			case 0:
				name = Babylon.get().getFromResources("weapon_hammer_0");
				break;
			case 1:
				name = Babylon.get().getFromResources("weapon_hammer_1");
				break;
			case 2:
				name = Babylon.get().getFromResources("weapon_hammer_2");
				break;
			case 3:
				name = Babylon.get().getFromResources("weapon_hammer_3");
				break;
			case 4:
				name = Babylon.get().getFromResources("weapon_hammer_4");
				break;
			default:
				name = Babylon.get().getFromResources("weapon_hammer_0");
				break;
		}
		image = ItemSpriteSheet.WAR_HAMMER;
	}
	
	public WarHammer() {
		super( 5, 1.2f, 1f );
	}
	
	@Override
	public String desc() {
		switch (depth) {
			case 0:
				return Babylon.get().getFromResources("weapon_hammer_desc_0");
			case 1:
				return Babylon.get().getFromResources("weapon_hammer_desc_1");
			case 2:
				return Babylon.get().getFromResources("weapon_hammer_desc_2");
			case 3:
				return Babylon.get().getFromResources("weapon_hammer_desc_3");
			case 4:
				return Babylon.get().getFromResources("weapon_hammer_desc_4");
			default:
				return Babylon.get().getFromResources("weapon_hammer_desc_0");
		}
	}
}

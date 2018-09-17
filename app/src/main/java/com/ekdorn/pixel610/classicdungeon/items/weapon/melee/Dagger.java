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
package com.ekdorn.pixel610.classicdungeon.items.weapon.melee;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;

public class Dagger extends MeleeWeapon {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("weapon_dagger", depth);
		image = ItemSpriteSheet.DAGGER;
	}
	
	public Dagger() {
		super( 1, 1.2f, 1f );
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("weapon_dagger_desc", depth);
	}
}

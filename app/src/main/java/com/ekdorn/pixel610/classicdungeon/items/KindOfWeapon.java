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
package com.ekdorn.pixel610.classicdungeon.items;

import java.util.ArrayList;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.ui.QuickSlot;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Random;

abstract public class KindOfWeapon extends EquipableItem {

	protected static final float TIME_TO_EQUIP = 1f;
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? Babylon.get().getFromResources("item_acunequip") : Babylon.get().getFromResources("item_acequip") );
		return actions;
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.weapon == this;
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		
		detachAll( hero.belongings.backpack );
		
		if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip( hero, true )) {
			
			hero.belongings.weapon = this;
			activate( hero );
			
			QuickSlot.refresh();
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Babylon.get().getFromResources("weapon_cursed"), name() );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
		}
	}
	
	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			
			hero.belongings.weapon = null;
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	public void activate( Hero hero ) {
	}
	
	abstract public int min();
	abstract public int max();
	
	public int damageRoll( Hero owner ) {
		return Random.NormalIntRange( min(), max() );
	}
	
	public float acuracyFactor( Hero hero ) {
		return 1f;
	}
	
	public float speedFactor( Hero hero ) {
		return 1f;
	}
	
	public void proc( Char attacker, Char defender, int damage ) {
	}
	
}
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
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.Weapon;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Random;

public class MeleeWeapon extends Weapon {
	
	private int tier;
	
	public MeleeWeapon( int tier, float acu, float dly ) {
		super();
		
		this.tier = tier;
		
		ACU = acu;
		DLY = dly;
		
		STR = typicalSTR();
	}
	
	protected int min0() {
		return tier;
	}
	
	protected int max0() {
		return (int)((tier * tier - tier + 10) / ACU * DLY);
	}
	
	@Override
	public int min() {
		return isBroken() ? min0() : min0() + level(); 
	}
	
	@Override
	public int max() {
		return isBroken() ? max0() : max0() + level() * tier;
	}
	
	@Override
	final public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean enchant ) {
		STR--;		
		return super.upgrade( enchant );
	}
	
	public Item safeUpgrade() {
		return upgrade( enchantment != null );
	}
	
	@Override
	public Item degrade() {		
		STR++;
		return super.degrade();
	}
	
	public int typicalSTR() {
		return 8 + tier * 2;
	}
	
	@Override
	public String info() {
		
		final String p = "\n\n";
		
		StringBuilder info = new StringBuilder( desc() );
		
		int lvl = visiblyUpgraded();
		String quality = lvl != 0 ? 
			(lvl > 0 ? 
				(isBroken() ? Babylon.get().getFromResources("weapon_melee_level0") : Babylon.get().getFromResources("weapon_melee_level1")) :
					Babylon.get().getFromResources("weapon_melee_level2")) :
			"";
		info.append( p );
		info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_desc0") + " " + Utils.indefinite( quality ), name) );
		info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_desc1"), tier) + " " );
		
		if (levelKnown) {
			int min = min();
			int max = max();
			info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_known0"), (min + (max - min) / 2)) + " " );
		} else {
			int min = min0();
			int max = max0();
			info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_known1"), (min + (max - min) / 2), typicalSTR()) + " " );
			if (typicalSTR() > Dungeon.hero.STR()) {
				info.append(Babylon.get().getFromResources("weapon_melee_known2") + " ");
			}
		}
		
		if (DLY != 1f) {
			info.append(Babylon.get().getFromResources("weapon_melee_add0") + " " + (DLY < 1f ? Babylon.get().getFromResources("weapon_melee_add1") :
					Babylon.get().getFromResources("weapon_melee_add2")) );
			if (ACU != 1f) {
				if ((ACU > 1f) == (DLY < 1f)) {
					info.append(Babylon.get().getFromResources("weapon_melee_add3") + " ");
				} else {
					info.append(Babylon.get().getFromResources("weapon_melee_add4") + " ");
				}
				info.append( ACU > 1f ? Babylon.get().getFromResources("weapon_melee_add5") : Babylon.get().getFromResources("weapon_melee_add6"));
			}
			info.append(Babylon.get().getFromResources("weapon_melee_add7") + " ");
		} else if (ACU != 1f) {
			info.append(Babylon.get().getFromResources("weapon_melee_add8") + " " + (ACU > 1f ? Babylon.get().getFromResources("weapon_melee_add9") :
					Babylon.get().getFromResources("weapon_melee_add10")) + Babylon.get().getFromResources("weapon_melee_add11") + " ");
		}
		switch (imbue) {
		case SPEED:
			info.append(Babylon.get().getFromResources("weapon_melee_add12"));
			break;
		case ACCURACY:
			info.append(Babylon.get().getFromResources("weapon_melee_add13"));
			break;
		case NONE:
		}
		
		if (enchantment != null) {
			info.append(Babylon.get().getFromResources("weapon_melee_enchanted"));
		}
		
		if (levelKnown && Dungeon.hero.belongings.backpack.items.contains( this )) {
			if (STR > Dungeon.hero.STR()) {
				info.append( p );
				info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_fin0"), name) );
			}
			if (STR < Dungeon.hero.STR()) {
				info.append( p );
				info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_fin1"), name) );
			}
		}
		
		if (isEquipped( Dungeon.hero )) {
			info.append( p );
			info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_fin2") + (cursed ? Babylon.get().getFromResources("weapon_melee_fin3") : "."), name) );
		} else {
			if (cursedKnown && cursed) {
				info.append( p );
				info.append(Babylon.get().getFromResources("weapon_melee_fin4") + name + "." );
			}
		}
		
		return info.toString();
	}
	
	@Override
	public int price() {
		int price = 20 * (1 << (tier - 1));
		if (enchantment != null) {
			price *= 1.5;
		}
		return considerState( price );
	}
	
	@Override
	public Item random() {
		super.random();
		
		if (Random.Int( 10 + level() ) == 0) {
			enchant();
		}
		
		return this;
	}
}

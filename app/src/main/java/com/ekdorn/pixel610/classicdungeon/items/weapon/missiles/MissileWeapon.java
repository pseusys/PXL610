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
package com.ekdorn.pixel610.classicdungeon.items.weapon.missiles;

import java.util.ArrayList;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.items.weapon.Weapon;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.classicdungeon.windows.WndOptions;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;
		defaultAction = Babylon.get().getFromResources("item_acthrow");
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (hero.heroClass != HeroClass.HUNTRESS && hero.heroClass != HeroClass.ROGUE) {
			actions.remove( Babylon.get().getFromResources("item_acequip") );
			actions.remove( Babylon.get().getFromResources("item_acunequip") );
		}
		return actions;
	}

	@Override
	protected void onThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		if (enemy == null || enemy == curUser) {
			super.onThrow( cell );
		} else {
			if (!curUser.shoot( enemy, this )) {
				miss( cell );
			}
		}
	}
	
	protected void miss( int cell ) {
		super.onThrow( cell );
	}
	
	@Override
	public void proc( Char attacker, Char defender, int damage ) {
		
		super.proc( attacker, defender, damage );
		
		Hero hero = (Hero)attacker;
		if (hero.rangedWeapon == null && stackable) {
			if (quantity == 1) {
				doUnequip( hero, false, false );
			} else {
				detach( null );
			}
		}
	}
	
	@Override
	public boolean doEquip( final Hero hero ) {
		GameScene.show( 
			new WndOptions( Babylon.get().getFromResources("weapon_missle"), Babylon.get().getFromResources("weapon_missle_sure"),
					Babylon.get().getFromResources("weapon_missle_yes"), Babylon.get().getFromResources("weapon_missle_no") ) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						MissileWeapon.super.doEquip( hero );
					}
				};
			}
		);
		
		return false;
	}
	
	@Override
	public Item random() {
		return this;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {
		
		StringBuilder info = new StringBuilder( desc() );
		
		int min = min();
		int max = max();
		info.append( "\n\n" + Utils.format(Babylon.get().getFromResources("weapon_missle_info0"), (min + (max - min) / 2)) + " " );
		
		if (Dungeon.hero.belongings.backpack.items.contains( this )) {
			if (STR > Dungeon.hero.STR()) {
				info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_fin0"), name) );
			}
			if (STR < Dungeon.hero.STR()) {
				info.append( Utils.format(Babylon.get().getFromResources("weapon_melee_fin1"), name) );
			}
		}
		
		if (isEquipped( Dungeon.hero )) {
			info.append( "\n\n" + Utils.format(Babylon.get().getFromResources("weapon_melee_fin2"), name) );
		}
		
		return info.toString();
	}
}

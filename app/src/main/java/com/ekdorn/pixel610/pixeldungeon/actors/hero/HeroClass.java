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
package com.ekdorn.pixel610.pixeldungeon.actors.hero;

import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.additional.GameMode;
import com.ekdorn.pixel610.pixeldungeon.items.Pen3D;
import com.ekdorn.pixel610.pixeldungeon.items.TomeOfMastery;
import com.ekdorn.pixel610.pixeldungeon.items.armor.ClothArmor;
import com.ekdorn.pixel610.pixeldungeon.items.bags.Keyring;
import com.ekdorn.pixel610.pixeldungeon.items.food.Food;
import com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfShadows;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.Scroll;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfMagicMissile;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.melee.Dagger;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.melee.Knuckles;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.melee.ShortSword;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.missiles.Dart;
import com.ekdorn.pixel610.pixeldungeon.items.weapon.missiles.Boomerang;
import com.ekdorn.pixel610.pixeldungeon.ui.QuickSlot;
import com.ekdorn.pixel610.utils.Bundle;

public enum HeroClass {

	WARRIOR, MAGE, ROGUE, HUNTRESS,

	MALE, FEMALE;

	public static final String[] WAR_PERKS = {
			Babylon.get().getFromResources("hero_cl_war_0"),
			Babylon.get().getFromResources("hero_cl_war_1"),
			Babylon.get().getFromResources("hero_cl_war_2"),
			Babylon.get().getFromResources("hero_cl_war_3"),
			Babylon.get().getFromResources("hero_cl_war_4"),
	};
	
	public static final String[] MAG_PERKS = {
			Babylon.get().getFromResources("hero_cl_mag_0"),
			Babylon.get().getFromResources("hero_cl_mag_1"),
			Babylon.get().getFromResources("hero_cl_mag_2"),
			Babylon.get().getFromResources("hero_cl_mag_3"),
			Babylon.get().getFromResources("hero_cl_mag_4")
	};
	
	public static final String[] ROG_PERKS = {
			Babylon.get().getFromResources("hero_cl_rog_0"),
			Babylon.get().getFromResources("hero_cl_rog_1"),
			Babylon.get().getFromResources("hero_cl_rog_2"),
			Babylon.get().getFromResources("hero_cl_rog_3"),
			Babylon.get().getFromResources("hero_cl_rog_4"),
			Babylon.get().getFromResources("hero_cl_rog_5")
	};
	
	public static final String[] HUN_PERKS = {
			Babylon.get().getFromResources("hero_cl_hun_0"),
			Babylon.get().getFromResources("hero_cl_hun_1"),
			Babylon.get().getFromResources("hero_cl_hun_2"),
			Babylon.get().getFromResources("hero_cl_hun_3"),
			Babylon.get().getFromResources("hero_cl_hun_4")
	};
	
	public void initHero( Hero hero ) {
		
		hero.heroClass = this;

		switch (PXL610.gamemode()) {
			case GameMode.original:
				initOriginal( hero );

				if (Badges.isUnlocked( masteryBadge() )) {
					new TomeOfMastery().collect();
				}
				break;

			case GameMode.dlc1:
				initDLC1( hero );
				break;
		}
		
		switch (this) {
		case WARRIOR:
			initWarrior( hero );
			break;
			
		case MAGE:
			initMage( hero );
			break;
			
		case ROGUE:
			initRogue( hero );
			break;
			
		case HUNTRESS:
			initHuntress( hero );
			break;
		}

		if (PXL610.invited()) new Pen3D().identify().collect();
		
		hero.updateAwareness();
	}
	
	private static void initOriginal( Hero hero ) {
		(hero.belongings.armor = new ClothArmor()).identify();
		new Food().identify().collect();
		new Keyring().collect();
	}

	private static void initDLC1( Hero hero ) {
		(hero.belongings.armor = new ClothArmor()).identify();
		new Food().identify().collect();
		new Keyring().collect();
	}
	
	public Badges.Badge masteryBadge() {
		switch (this) {
		case WARRIOR:
			return Badges.Badge.MASTERY_WARRIOR;
		case MAGE:
			return Badges.Badge.MASTERY_MAGE;
		case ROGUE:
			return Badges.Badge.MASTERY_ROGUE;
		case HUNTRESS:
			return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}
	
	private static void initWarrior( Hero hero ) {
		hero.STR = hero.STR + 1;
		
		(hero.belongings.weapon = new ShortSword()).identify();
		new Dart( 8 ).identify().collect();
		
		QuickSlot.primaryValue = Dart.class;

		for (Class<? extends Scroll> scroll: Scroll.getUnknown()) {
            try {
                scroll.newInstance().setKnown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	private static void initMage( Hero hero ) {	
		(hero.belongings.weapon = new Knuckles()).identify();
		
		WandOfMagicMissile wand = new WandOfMagicMissile();
		wand.identify().collect();
		
		QuickSlot.primaryValue = wand;
	}
	
	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();
		(hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();
		new Dart( 8 ).identify().collect();
		
		hero.belongings.ring1.activate( hero );
		
		QuickSlot.primaryValue = Dart.class;
		
		new ScrollOfMagicMapping().setKnown();
	}
	
	private static void initHuntress( Hero hero ) {
		
		hero.HP = (hero.HT -= 5);
		
		(hero.belongings.weapon = new Dagger()).identify();
		Boomerang boomerang = new Boomerang();
		boomerang.identify().collect();
		
		QuickSlot.primaryValue = boomerang;
	}
	
	public String title() {
		switch (this) {
			case WARRIOR:
				return Babylon.get().getFromResources("hero_cl_warrior");
			case MAGE:
				return Babylon.get().getFromResources("hero_cl_mage");
			case ROGUE:
				return Babylon.get().getFromResources("hero_cl_rogue");
			case HUNTRESS:
				return Babylon.get().getFromResources("hero_cl_huntress");

			/*case MALE:
                return;
            case FEMALE:
                return;*/

			default:
				return Babylon.get().getFromResources("hero_cl_rogue");
		}
	}
	
	public String spritesheet() {
		
		switch (this) {
		case WARRIOR:
			return Assets.WARRIOR;
		case MAGE:
			return Assets.MAGE;
		case ROGUE:
			return Assets.ROGUE;
		case HUNTRESS:
			return Assets.HUNTRESS;

        /*case MALE:
            return;
        case FEMALE:
            return;*/
		}
		
		return null;
	}
	
	public String[] perks() {
		
		switch (this) {
		case WARRIOR:
			return WAR_PERKS;
		case MAGE:
			return MAG_PERKS;
		case ROGUE:
			return ROG_PERKS;
		case HUNTRESS:
			return HUN_PERKS;

		/*case MALE:
            return;
        case FEMALE:
            return;*/
		}
		
		return null;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}

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
package com.ekdorn.pixel610.classicdungeon.additional.DLC1.actors;

import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.classicdungeon.items.armor.ClothArmor;
import com.ekdorn.pixel610.classicdungeon.items.bags.Keyring;
import com.ekdorn.pixel610.classicdungeon.items.food.Food;
import com.ekdorn.pixel610.classicdungeon.items.rings.RingOfShadows;
import com.ekdorn.pixel610.classicdungeon.items.scrolls.ScrollOfMagicMapping;
import com.ekdorn.pixel610.classicdungeon.items.weapon.melee.Dagger;
import com.ekdorn.pixel610.classicdungeon.items.weapon.missiles.Boomerang;
import com.ekdorn.pixel610.classicdungeon.items.weapon.missiles.Dart;
import com.ekdorn.pixel610.classicdungeon.ui.QuickSlot;
import com.ekdorn.pixel610.utils.Bundle;

public enum DLC1HeroClass {

	MALE("rogue"),
	FEMALE("huntress");

	private String tag;

	private DLC1HeroClass( String tag ) {
		this.tag = tag;
	}

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

	public void initHero( DLC1Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {

			case MALE:
				initRogue( hero );
				break;

			case FEMALE:
				initHuntress( hero );
				break;
		}

		hero.updateAwareness();
	}

	private static void initCommon( Hero hero ) {
		(hero.belongings.armor = new ClothArmor()).identify();
		new Food().identify().collect();
		new Keyring().collect();
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

	public String tag() {
		return tag;
	}

	public String title() {
		switch (this) {
			case MALE:
				return Babylon.get().getFromResources("hero_cl_rogue");
			case FEMALE:
				return Babylon.get().getFromResources("hero_cl_huntress");
			default:
				return Babylon.get().getFromResources("hero_cl_rogue");
		}
	}

	public String spritesheet() {

		switch (this) {
			case MALE:
				return Assets.ROGUE;
			case FEMALE:
				return Assets.HUNTRESS;
		}

		return null;
	}

	public String[] perks() {

		switch (this) {
			case MALE:
				return ROG_PERKS;
			case FEMALE:
				return HUN_PERKS;
		}

		return null;
	}

	private static final String CLASS	= "class";

	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}

	public static DLC1HeroClass restoreInBundle(Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : MALE;
	}

	public static HeroClass getClassById(String id) {
		if (id.equals(MALE.tag)) {
			return HeroClass. ROGUE;
		} else if (id.equals(FEMALE.tag)) {
			return HeroClass.HUNTRESS;
		} else {
			return HeroClass. ROGUE;
		}
	}
}

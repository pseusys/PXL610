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
package com.ekdorn.pixel610.classicdungeon.actors.hero;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.utils.Bundle;

public enum HeroSubClass {

	NONE( null, null ),
	
	GLADIATOR(Babylon.get().getFromResources("hero_scl_glad"),
			Babylon.get().getFromResources("hero_scl_glad_desc")),
	BERSERKER(Babylon.get().getFromResources("hero_scl_bers"),
			Babylon.get().getFromResources("hero_scl_bers_desc")),
	
	WARLOCK(Babylon.get().getFromResources("hero_scl_warl"),
			Babylon.get().getFromResources("hero_scl_warl_desc")),
	BATTLEMAGE(Babylon.get().getFromResources("hero_scl_batt"),
			Babylon.get().getFromResources("hero_scl_batt_desc")),
	
	ASSASSIN(Babylon.get().getFromResources("hero_scl_ass"),
			Babylon.get().getFromResources("hero_scl_ass_desc")),
	FREERUNNER(Babylon.get().getFromResources("hero_scl_free"),
			Babylon.get().getFromResources("hero_scl_free_desc")),
		
	SNIPER(Babylon.get().getFromResources("hero_scl_snip"),
			Babylon.get().getFromResources("hero_scl_snip_desc")),
	WARDEN(Babylon.get().getFromResources("hero_scl_ward"),
			Babylon.get().getFromResources("hero_scl_ward_desc"));
	
	private String title;
	private String desc;
	
	private HeroSubClass( String title, String desc ) {
		this.title = title;
		this.desc = desc;
	}
	
	public String title() {
		return title;
	}
	
	public String desc() {
		return desc;
	}
	
	private static final String SUBCLASS	= "subClass";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( SUBCLASS, toString() );
	}
	
	public static HeroSubClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( SUBCLASS );
		try {
			return valueOf( value );
		} catch (Exception e) {
			return NONE;
		}
	}
	
}

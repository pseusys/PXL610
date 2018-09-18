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
package com.ekdorn.pixel610.pixeldungeon.items.scrolls;

import java.util.ArrayList;
import java.util.HashSet;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Blindness;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.ItemStatusHandler;
import com.ekdorn.pixel610.pixeldungeon.sprites.HeroSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundle;

public abstract class Scroll extends Item {
	
	protected static final float TIME_TO_READ	= 1f;
	
	private static final Class<?>[] scrolls = {
		ScrollOfIdentify.class, 
		ScrollOfMagicMapping.class, 
		ScrollOfRecharging.class, 
		ScrollOfRemoveCurse.class, 
		ScrollOfTeleportation.class, 
		ScrollOfChallenge.class,
		ScrollOfTerror.class,
		ScrollOfLullaby.class,
		ScrollOfPsionicBlast.class,
		ScrollOfMirrorImage.class,
		ScrollOfUpgrade.class,
		ScrollOfEnchantment.class
	};
	private static final String[] runes = 
		{Babylon.get().getFromResources("scroll_kaunan"),
				Babylon.get().getFromResources("scroll_sowilo"),
				Babylon.get().getFromResources("scroll_laguz"),
				Babylon.get().getFromResources("scroll_yngvi"),
				Babylon.get().getFromResources("scroll_gyfu"),
				Babylon.get().getFromResources("scroll_raido"),
				Babylon.get().getFromResources("scroll_isaz"),
				Babylon.get().getFromResources("scroll_mannaz"),
				Babylon.get().getFromResources("scroll_naudiz"),
				Babylon.get().getFromResources("scroll_berkanan"),
				Babylon.get().getFromResources("scroll_odal"),
				Babylon.get().getFromResources("scroll_tiwaz")};
	private static final Integer[] images = {
		ItemSpriteSheet.SCROLL_KAUNAN, 
		ItemSpriteSheet.SCROLL_SOWILO, 
		ItemSpriteSheet.SCROLL_LAGUZ, 
		ItemSpriteSheet.SCROLL_YNGVI, 
		ItemSpriteSheet.SCROLL_GYFU, 
		ItemSpriteSheet.SCROLL_RAIDO, 
		ItemSpriteSheet.SCROLL_ISAZ, 
		ItemSpriteSheet.SCROLL_MANNAZ, 
		ItemSpriteSheet.SCROLL_NAUDIZ, 
		ItemSpriteSheet.SCROLL_BERKANAN, 
		ItemSpriteSheet.SCROLL_ODAL, 
		ItemSpriteSheet.SCROLL_TIWAZ};
	
	private static ItemStatusHandler<Scroll> handler;
	
	private String rune;
	
	{
		stackable = true;		
		defaultAction = Babylon.get().getFromResources("scroll_read");
	}
	
	@SuppressWarnings("unchecked")
	public static void initLabels() {
		handler = new ItemStatusHandler<Scroll>( (Class<? extends Scroll>[])scrolls, runes, images );
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Scroll>( (Class<? extends Scroll>[])scrolls, runes, images, bundle );
	}
	
	public Scroll() {
		super();
		image = handler.image( this );
		rune = handler.label( this );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( Babylon.get().getFromResources("scroll_read") );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( Babylon.get().getFromResources("scroll_read") )) {
			
			if (hero.buff( Blindness.class ) != null) {
				GLog.w( Babylon.get().getFromResources("scroll_blinded") );
			} else {
				curUser = hero;
				curItem = detach( hero.belongings.backpack );
				doRead();
			}
			
		} else {
		
			super.execute( hero, action );
			
		}
	}
	
	abstract protected void doRead();
	
	protected void readAnimation() {
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();
	}
	
	public boolean isKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		//Badges.validateAllScrollsIdentified();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}
	
	@Override
	public String name() {
		return isKnown() ? name : Babylon.get().getFromResources("scroll_scroll") + " \"" + rune + "\"";
	}
	
	@Override
	public String info() {
		return isKnown() ?
			desc() : Utils.format(Babylon.get().getFromResources("scroll_desc"), rune);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	public static HashSet<Class<? extends Scroll>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Scroll>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == scrolls.length;
	}
	
	@Override
	public int price() {
		return 15 * quantity;
	}
}

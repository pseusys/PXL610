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
package com.ekdorn.pixel610.classicdungeon.items.rings;

import java.util.ArrayList;

import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Badges;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.PXL610;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.hero.Hero;
import com.ekdorn.pixel610.classicdungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.classicdungeon.items.EquipableItem;
import com.ekdorn.pixel610.classicdungeon.items.Item;
import com.ekdorn.pixel610.classicdungeon.items.ItemStatusHandler;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.classicdungeon.windows.WndOptions;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Ring extends EquipableItem {

	private static final int TICKS_TO_KNOW	= 200;
	
	private static final float TIME_TO_EQUIP = 1f;
	
	protected Buff buff;
	
	private static final Class<?>[] rings = { 
		RingOfMending.class, 
		RingOfDetection.class, 
		RingOfShadows.class,
		RingOfPower.class,
		RingOfHerbalism.class,
		RingOfAccuracy.class,
		RingOfEvasion.class,
		RingOfSatiety.class,
		RingOfHaste.class,
		RingOfHaggler.class,
		RingOfElements.class,
		RingOfThorns.class
	};
	private static final String[] gems = 
		{Babylon.get().getFromResources("ring_diamond"),
				Babylon.get().getFromResources("ring_opal"),
				Babylon.get().getFromResources("ring_garnet"),
				Babylon.get().getFromResources("ring_ruby"),
				Babylon.get().getFromResources("ring_amethyst"),
				Babylon.get().getFromResources("ring_topaz"),
				Babylon.get().getFromResources("ring_onyx"),
				Babylon.get().getFromResources("ring_tourmaline"),
				Babylon.get().getFromResources("ring_emerald"),
				Babylon.get().getFromResources("ring_sapphire"),
				Babylon.get().getFromResources("ring_quartz"),
				Babylon.get().getFromResources("ring_agate")};
	private static final Integer[] images = {
		ItemSpriteSheet.RING_DIAMOND, 
		ItemSpriteSheet.RING_OPAL, 
		ItemSpriteSheet.RING_GARNET, 
		ItemSpriteSheet.RING_RUBY, 
		ItemSpriteSheet.RING_AMETHYST, 
		ItemSpriteSheet.RING_TOPAZ, 
		ItemSpriteSheet.RING_ONYX, 
		ItemSpriteSheet.RING_TOURMALINE, 
		ItemSpriteSheet.RING_EMERALD, 
		ItemSpriteSheet.RING_SAPPHIRE, 
		ItemSpriteSheet.RING_QUARTZ, 
		ItemSpriteSheet.RING_AGATE};
	
	private static ItemStatusHandler<Ring> handler;
	
	private String gem;
	
	private int ticksToKnow = TICKS_TO_KNOW;
	
	@SuppressWarnings("unchecked")
	public static void initGems() {
		handler = new ItemStatusHandler<Ring>( (Class<? extends Ring>[])rings, gems, images );
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Ring>( (Class<? extends Ring>[])rings, gems, images, bundle );
	}
	
	public Ring() {
		super();
		syncGem();
	}
	
	public void syncGem() {
		image	= handler.image( this );
		gem		= handler.label( this );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? Babylon.get().getFromResources("item_acunequip") : Babylon.get().getFromResources("item_acequip") );
		return actions;
	}
	
	@Override
	public boolean doEquip( final Hero hero ) {
		
		if (hero.belongings.ring1 != null && hero.belongings.ring2 != null) {
			
			final Ring r1 = hero.belongings.ring1;
			final Ring r2 = hero.belongings.ring2;
			
			PXL610.scene().add(
				new WndOptions( Babylon.get().getFromResources("ring_unequip"), Babylon.get().getFromResources("ring_unequip_message"),
					Utils.capitalize( r1.toString() ), 
					Utils.capitalize( r2.toString() ) ) {
					
					@Override
					protected void onSelect( int index ) {
						
						detach( hero.belongings.backpack );
						
						Ring equipped = (index == 0 ? r1 : r2);
						if (equipped.doUnequip( hero, true, false )) {
							doEquip( hero );
						} else {
							collect( hero.belongings.backpack );
						}
					}
				} );
			
			return false;
			
		} else {
			
			if (hero.belongings.ring1 == null) {
				hero.belongings.ring1 = this;
			} else {
				hero.belongings.ring2 = this;
			}
			
			detach( hero.belongings.backpack );
			
			activate( hero );
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n(Babylon.get().getFromResources("ring_cursed"), this );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		}

	}
	
	public void activate( Char ch ) {
		buff = buff();
		buff.attachTo( ch );
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			
			if (hero.belongings.ring1 == this) {
				hero.belongings.ring1 = null;
			} else {
				hero.belongings.ring2 = null;
			}
			
			hero.remove( buff );
			buff = null;
			
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.ring1 == this || hero.belongings.ring2 == this;
	}
	
	@Override
	public int effectiveLevel() {
		return isBroken() ? 1 : level();
	}
	
	private void renewBuff() {
		if (buff != null) {
			Char owner = buff.target;
			buff.detach();
			if ((buff = buff()) != null) {
				buff.attachTo( owner );
			}
		}
	}
	
	@Override
	public void getBroken() {
		renewBuff();
		super.getBroken();
	}
	
	@Override
	public void fix() {
		super.fix();
		renewBuff();
	}
	
	@Override
	public int maxDurability( int lvl ) {
		if (lvl <= 1) {
			return Integer.MAX_VALUE;
		} else {
			return 100 * (lvl < 16 ? 16 - lvl : 1);
		}
	}
	
	public boolean isKnown() {
		return handler.isKnown( this );
	}
	
	protected void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllRingsIdentified();
	}
	
	@Override
	public String toString() {
		return 
			levelKnown && isBroken() ? 
				"broken " + super.toString() : 
				super.toString();
	}
	
	@Override
	public String name() {
		return isKnown() ? name :  Babylon.get().getFromResources("ring_ring")+ " " + gem;
	}
	
	@Override
	public String desc() {
		return Utils.format(Babylon.get().getFromResources("ring_desc"), gem);
	}
	
	@Override
	public String info() {
		if (isEquipped( Dungeon.hero )) {

			return desc() + "\n\n" + Utils.format(Babylon.get().getFromResources("ring_onfinger"), name()) + (cursed ? Babylon.get().getFromResources("ring_ifcursed") : "." );
			
		} else if (cursed && cursedKnown) {
			
			return desc() + Babylon.get().getFromResources("ring_cursedknown") + " " + name() + ".";
			
		} else {
			
			return desc();
			
		}
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}
	
	@Override
	public Item random() {
		int lvl = Random.Int( 1, 3 );
		if (Random.Float() < 0.3f) {
			degrade( lvl );
			cursed = true;
		} else {
			upgrade( lvl );
		}
		return this;
	}
	
	public static boolean allKnown() {
		return handler.known().size() == rings.length - 2;
	}
	
	@Override
	public int price() {
		return considerState( 80 );
	}
	
	protected RingBuff buff() {
		return null;
	}
	
	private static final String UNFAMILIRIARITY	= "unfamiliarity";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, ticksToKnow );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((ticksToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			ticksToKnow = TICKS_TO_KNOW;
		}
	}
	
	public class RingBuff extends Buff {
		
		public int level;
		public RingBuff() {
			level = Ring.this.effectiveLevel();
		}
		
		@Override
		public boolean attachTo( Char target ) {

			if (target instanceof Hero && ((Hero)target).heroClass == HeroClass.ROGUE && !isKnown()) {
				setKnown();
				GLog.i( Babylon.get().getFromResources("ring_known"), name() );
				Badges.validateItemLevelAquired( Ring.this );
			}
			
			return super.attachTo(target);
		}
		
		@Override
		public boolean act() {
			
			if (!isIdentified() && --ticksToKnow <= 0) {
				String gemName = name();
				identify();
				GLog.w( Babylon.get().getFromResources("armor_autoid"), gemName, Ring.this.toString() );
				Badges.validateItemLevelAquired( Ring.this );
			}
			
			use();
			
			spend( TICK );
			
			return true;
		}
	}
}

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
package com.ekdorn.pixel610.pixeldungeon.items.wands;

import java.util.ArrayList;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.effects.MagicMissile;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.ItemStatusHandler;
import com.ekdorn.pixel610.pixeldungeon.items.KindOfWeapon;
import com.ekdorn.pixel610.pixeldungeon.items.bags.Bag;
import com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfPower.Power;
import com.ekdorn.pixel610.pixeldungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.pixeldungeon.scenes.CellSelector;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.ui.QuickSlot;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Callback;
import com.ekdorn.pixel610.utils.Random;

public abstract class Wand extends KindOfWeapon {

	private static final int USAGES_TO_KNOW	= 40;

	private static final float TIME_TO_ZAP	= 1f;
	
	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;
	
	protected Charger charger;
	
	private boolean curChargeKnown = false;
	
	private int usagesToKnow = USAGES_TO_KNOW;
	
	protected boolean hitChars = true;
	
	private static final Class<?>[] wands = {
		WandOfTeleportation.class, 
		WandOfSlowness.class, 
		WandOfFirebolt.class, 
		WandOfPoison.class, 
		WandOfRegrowth.class,
		WandOfBlink.class,
		WandOfLightning.class,
		WandOfAmok.class,
		WandOfReach.class,
		WandOfFlock.class,
		WandOfDisintegration.class,
		WandOfAvalanche.class
	};
	private static final String[] woods = 
		{Babylon.get().getFromResources("wand_holly") + " ",
				Babylon.get().getFromResources("wand_yew") + " ",
				Babylon.get().getFromResources("wand_ebony") + " ",
				Babylon.get().getFromResources("wand_cherry") + " ",
				Babylon.get().getFromResources("wand_teak") + " ",
				Babylon.get().getFromResources("wand_rowan") + " ",
				Babylon.get().getFromResources("wand_willow") + " ",
				Babylon.get().getFromResources("wand_mahogany") + " ",
				Babylon.get().getFromResources("wand_bamboo") + " ",
				Babylon.get().getFromResources("wand_purpleheart") + " ",
				Babylon.get().getFromResources("wand_oak") + " ",
				Babylon.get().getFromResources("wand_birch") + " "};
	private static final Integer[] images = {
		ItemSpriteSheet.WAND_HOLLY, 
		ItemSpriteSheet.WAND_YEW, 
		ItemSpriteSheet.WAND_EBONY, 
		ItemSpriteSheet.WAND_CHERRY, 
		ItemSpriteSheet.WAND_TEAK, 
		ItemSpriteSheet.WAND_ROWAN, 
		ItemSpriteSheet.WAND_WILLOW, 
		ItemSpriteSheet.WAND_MAHOGANY, 
		ItemSpriteSheet.WAND_BAMBOO, 
		ItemSpriteSheet.WAND_PURPLEHEART, 
		ItemSpriteSheet.WAND_OAK, 
		ItemSpriteSheet.WAND_BIRCH};
	
	private static ItemStatusHandler<Wand> handler;
	
	private String wood;
	
	@SuppressWarnings("unchecked")
	public static void initWoods() {
		handler = new ItemStatusHandler<Wand>( (Class<? extends Wand>[])wands, woods, images );
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<Wand>( (Class<? extends Wand>[])wands, woods, images, bundle );
	}
	
	public Wand() {
		super();

		try {
			image = handler.image( this );
			wood = handler.label( this );
		} catch (Exception e) {
			// Wand of Magic Missile
		}

		finish();
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (curCharges > 0 || !curChargeKnown) {
			actions.add(  Babylon.get().getFromResources("wand_zap") );
		}
		if (hero.heroClass != HeroClass.MAGE) {
			actions.remove( Babylon.get().getFromResources("item_acequip") );
			actions.remove( Babylon.get().getFromResources("item_acunequip") );
		}
		return actions;
	}
	
	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		onDetach();
		return super.doUnequip( hero, collect, single );
	}
	
	@Override
	public void activate( Hero hero ) {
		charge( hero );
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals(  Babylon.get().getFromResources("wand_zap") )) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			
		} else {
			
			super.execute( hero, action );
			
		}
	}
	
	protected abstract void onZap( int cell );
	
	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	};
	
	public void charge( Char owner ) {
		if (charger == null) {
			(charger = new Charger()).attachTo( owner );
		}
	}
	
	@Override
	public void onDetach( ) {
		stopCharging();
	}
	
	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}
	
	public int power() {
		int eLevel = effectiveLevel();
		if (charger != null) {
			Power power = charger.target.buff( Power.class );
			return power == null ? eLevel : Math.max( eLevel + power.level, 0 ); 
		} else {
			return eLevel;
		}
	}
	
	protected boolean isKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllWandsIdentified();
	}
	
	@Override
	public Item identify() {
		
		setKnown();
		curChargeKnown = true;
		super.identify();
		
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder( super.toString() );
		
		String status = status();
		if (status != null) {
			sb.append( " (" + status +  ")" );
		}
		
		if (isBroken()) {
			sb.insert( 0, Babylon.get().getFromResources("wand_broken") + " ");
		}
		
		return sb.toString();
	}
	
	@Override
	public String name() {
		return isKnown() ? name : wood + Babylon.get().getFromResources("wand_name");
	}
	
	@Override
	public String info() {
		StringBuilder info = new StringBuilder( isKnown() ? desc() : String.format( Babylon.get().getFromResources("wand_wood"), wood ) );
		if (Dungeon.hero.heroClass == HeroClass.MAGE) {
			info.append( "\n\n" );
			if (levelKnown) {
				int min = min();
				info.append( String.format( Babylon.get().getFromResources("wand_melee"), min + (max() - min) / 2 ) );
			} else {
				info.append(  String.format( Babylon.get().getFromResources("wand_weapon") ) );
			}
		}
		return info.toString();
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}
	
	@Override
	public Item upgrade() {

		super.upgrade();
		
		updateLevel();
		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public int maxDurability( int lvl ) {
		return 6 * (lvl < 16 ? 16 - lvl : 1);
	}
	
	protected void updateLevel() {
		maxCharges = Math.min( initialCharges() + level(), 9 );
		curCharges = Math.min( curCharges, maxCharges );
	}
	
	protected int initialCharges() {
		return 2;
	}
	
	@Override
	public int min() {
		int tier = 1 + effectiveLevel() / 3;
		return tier;
	}
	
	@Override
	public int max() {
		int level = effectiveLevel();
		int tier = 1 + level / 3;
		return (tier * tier - tier + 10) / 2 + level;
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.blueLight( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

	protected void wandUsed() {
		
		curCharges--;
		if (!isIdentified() && --usagesToKnow <= 0) {
			identify();
			GLog.w( Babylon.get().getFromResources("wand_autoid"), name() );
		} else {
			updateQuickslot();
		}
		
		use();
		
		curUser.spendAndNext( TIME_TO_ZAP );
	}
	
	@Override
	public Item random() {
		if (Random.Float() < 0.5f) {
			upgrade();
			if (Random.Float() < 0.15f) {
				upgrade();
			}
		}
		
		return this;
	}

	@Override
	public boolean collect() {
		if (Dungeon.hero.heroClass.equals(HeroClass.MAGE)) this.identify();

		return super.collect();
	}

	public static boolean allKnown() {
		return handler.known().size() == wands.length;
	}
	
	@Override
	public int price() {
		return considerState( 50 );
	}
	
	private static final String UNFAMILIRIARITY		= "unfamiliarity";
	private static final String MAX_CHARGES			= "maxCharges";
	private static final String CUR_CHARGES			= "curCharges";
	private static final String CUR_CHARGE_KNOWN	= "curChargeKnown";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, usagesToKnow );
		bundle.put( MAX_CHARGES, maxCharges );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((usagesToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		maxCharges = bundle.getInt( MAX_CHARGES );
		curCharges = bundle.getInt( CUR_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
	}
	
	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			
			if (target != null) {
				
				if (target == curUser.pos) {
					GLog.i( Babylon.get().getFromResources("wand_selftarget") );
					return;
				}
				
				final Wand curWand = (Wand)Wand.curItem;
				
				curWand.setKnown();
				
				final int cell = Ballistica.cast( curUser.pos, target, true, curWand.hitChars );
				curUser.sprite.zap( cell );
				
				QuickSlot.target( curItem, Actor.findChar( cell ) );
				
				if (curWand.curCharges > 0) {
					
					curUser.busy();
					
					curWand.fx( cell, new Callback() {
						@Override
						public void call() {
							curWand.onZap( cell );
							curWand.wandUsed();
						}
					} );
					
					Invisibility.dispel();
					
				} else {
					
					curUser.spendAndNext( TIME_TO_ZAP );
					GLog.w( Babylon.get().getFromResources("wand_outofcharges") );
					curWand.levelKnown = true;
					
					curWand.updateQuickslot();
				}
				
			}
		}
		
		@Override
		public String prompt() {
			return Babylon.get().getFromResources("wand_choosedir");
		}
	};
	
	protected class Charger extends Buff {
		
		private static final float TIME_TO_CHARGE = 40f;
		
		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			delay();
			
			return true;
		}
		
		@Override
		public boolean act() {
			
			if (curCharges < maxCharges) {
				curCharges++;
				updateQuickslot();
			}
			
			delay();
			
			return true;
		}
		
		protected void delay() {
			float time2charge = ((Hero)target).heroClass == HeroClass.MAGE ? 
				TIME_TO_CHARGE / (float)Math.sqrt( 1 + effectiveLevel() ) : 
				TIME_TO_CHARGE;
			spend( time2charge );
		}
	}

	@Override
	public void finish() {
		defaultAction =  Babylon.get().getFromResources("wand_zap");
	}
}

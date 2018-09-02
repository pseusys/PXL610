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
package com.ekdorn.pixel610.pixeldungeon.items.armor;

import java.util.ArrayList;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.items.EquipableItem;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.armor.glyphs.*;
import com.ekdorn.pixel610.pixeldungeon.sprites.HeroSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Bundlable;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Armor extends EquipableItem {
	
	private static final int HITS_TO_KNOW	= 10;
	
	public int tier;
	public int STR;
	
	private int hitsToKnow = HITS_TO_KNOW;
	
	public Glyph glyph;
	
	public Armor( int tier ) {
		super();

		this.tier = tier;
		
		STR = typicalSTR();
	}
	
	private static final String UNFAMILIRIARITY	= "unfamiliarity";
	private static final String GLYPH			= "glyph";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, hitsToKnow );
		bundle.put( GLYPH, glyph );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((hitsToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			hitsToKnow = HITS_TO_KNOW;
		}
		inscribe( (Glyph)bundle.get( GLYPH ) );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? Babylon.get().getFromResources("item_acunequip") : Babylon.get().getFromResources("item_acequip") );
		return actions;
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		
		detach( hero.belongings.backpack );
		
		if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
			
			hero.belongings.armor = this;
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Babylon.get().getFromResources("armor_cursed"), toString() );
			}
			
			((HeroSprite)hero.sprite).updateArmor();
			
			hero.spendAndNext( time2equip( hero ) );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
			
		}
	}
	
	@Override
	protected float time2equip( Hero hero ) {
		return 2 / hero.speed();
	}
	
	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			
			hero.belongings.armor = null;
			((HeroSprite)hero.sprite).updateArmor();
			
			return true;
			
		} else {
			
			return false;
			
		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.armor == this;
	}
	
	public int DR() {
		return tier * (2 + effectiveLevel() + (glyph == null ? 0 : 1));
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {
		
		if (glyph != null) {
			if (!inscribe && Random.Int( level() ) > 0) {
				GLog.w( Babylon.get().getFromResources("armor_incompatible") );
				inscribe( null );
			}
		} else {
			if (inscribe) {
				inscribe();
			}
		};
		
		STR--;
		
		return super.upgrade();
	}
	
	public Item safeUpgrade() {
		return upgrade( glyph != null );
	}
	
	@Override
	public Item degrade() {
		STR++;
		return super.degrade();
	}
	
	@Override
	public int maxDurability( int lvl ) {
		return 6 * (lvl < 16 ? 16 - lvl : 1);
	}
	
	public int proc( Char attacker, Char defender, int damage ) {
		
		if (glyph != null) {
			damage = glyph.proc( this, attacker, defender, damage );
		}
		
		if (!levelKnown) {
			if (--hitsToKnow <= 0) {
				levelKnown = true;
				GLog.w( Babylon.get().getFromResources("armor_autoid"), name(), toString() );
				Badges.validateItemLevelAquired( this );
			}
		}
		
		use();
		
		return damage;
	}
	
	@Override
	public String toString() {
		return levelKnown ? Utils.format( isBroken() ? Babylon.get().getFromResources("armor_broken") :
				Babylon.get().getFromResources("to_string"), super.toString(), STR ) : super.toString();
	}
	
	@Override
	public String name() {
		return glyph == null ? super.name() : glyph.name( super.name() );
	}
	
	@Override
	public String info() {
		String name = name();
		StringBuilder info = new StringBuilder( desc() );
		
		if (levelKnown) {
			info.append(Utils.format(Babylon.get().getFromResources("armor_absortion"), name, Math.max( DR(), 0 )));
			
			if (STR > Dungeon.hero.STR()) {
				
				if (isEquipped( Dungeon.hero )) {
					info.append(Babylon.get().getFromResources("armor_op"));
				} else {
					info.append(Babylon.get().getFromResources("armor_up"));
				}
				
			}
		} else {
			info.append(Utils.format(Babylon.get().getFromResources("armor_typical"), name, typicalDR(), typicalSTR()) );
			if (typicalSTR() > Dungeon.hero.STR()) {
				info.append(Babylon.get().getFromResources("armor_2heavy"));
			}
		}
		
		if (glyph != null) {
			info.append(Babylon.get().getFromResources("armor_enchanted"));
		}
		
		if (isEquipped( Dungeon.hero )) {
			info.append(Babylon.get().getFromResources("armor_wearing") + " " + name + (cursed ? Babylon.get().getFromResources("armor_ifcursed") : ".") );
		} else {
			if (cursedKnown && cursed) {
				info.append( Utils.format(Babylon.get().getFromResources("armor_cursedknown"), name) );
			}
		}
		
		return info.toString();
	}
	
	@Override
	public Item random() {
		if (Random.Float() < 0.4) {
			int n = 1;
			if (Random.Int( 3 ) == 0) {
				n++;
				if (Random.Int( 3 ) == 0) {
					n++;
				}
			}
			if (Random.Int( 2 ) == 0) {
				upgrade( n );
			} else {
				degrade( n );
				cursed = true;
			}
		}
		
		if (Random.Int( 10 ) == 0) {
			inscribe();
		}
		
		return this;
	}
	
	public int typicalSTR() {
		return 7 + tier * 2;
	}
	
	public int typicalDR() {
		return tier * 2;
	}
	
	@Override
	public int price() {
		int price = 10 * (1 << (tier - 1));
		if (glyph != null) {
			price *= 1.5;
		}
		return considerState( price );
	}
	
	public Armor inscribe( Glyph glyph ) {
		this.glyph = glyph;
		return this;
	}
	
	public Armor inscribe() {
		
		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random();
		while (gl.getClass() == oldGlyphClass) {
			gl = Armor.Glyph.random();
		}
		
		return inscribe( gl );
	}
	
	public boolean isInscribed() {
		return glyph != null;
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null ? glyph.glowing() : null;
	}
	
	public static abstract class Glyph implements Bundlable {
		
		private static final Class<?>[] glyphs = new Class<?>[]{ 
			Bounce.class, Affection.class, AntiEntropy.class, Multiplicity.class, 
			Potential.class, Metabolism.class, Stench.class, Viscosity.class,
			Displacement.class, Entanglement.class, AutoRepair.class };
		
		private static final float[] chances= new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );
		
		public String name( String armorName ) {
			return armorName;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {	
		}

		@Override
		public void storeInBundle( Bundle bundle ) {	
		}
		
		public ItemSprite.Glowing glowing() {
			return ItemSprite.Glowing.WHITE;
		}
		
		public boolean checkOwner( Char owner ) {
			if (!owner.isAlive() && owner instanceof Hero) {
				
				((Hero)owner).killerGlyph = this;
				Badges.validateDeathFromGlyph();
				return true;
				
			} else {
				return false;
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph random() {
			try {
				return ((Class<Glyph>)glyphs[ Random.chances( chances ) ]).newInstance();
			} catch (Exception e) {
				return null;
			}
		}
		
	}
}

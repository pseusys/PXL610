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

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Blindness;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Bestiary;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.items.Heap;
import com.ekdorn.pixel610.pixeldungeon.items.Heap.Type;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.HeroSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Random;

public class ScrollOfWipeOut extends Item {
	
	protected static final float TIME_TO_READ	= 1f;

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_wipeout");
		image = ItemSpriteSheet.SCROLL_WIPE_OUT;
		
		stackable = true;		
		defaultAction = Babylon.get().getFromResources("scroll_read");
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
	
	private void doRead() {
		GameScene.flash( 0xFF6644 );
		
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (!Bestiary.isBoss( mob )) {
				Sample.INSTANCE.play( Assets.SND_CURSED, 0.3f, 0.3f, Random.Float( 0.6f, 0.9f ) );
				mob.die( this );
			}
		}
		
		for (Heap heap : Dungeon.level.heaps.values()) {
			switch (heap.type) {
			case FOR_SALE:
				heap.type = Type.HEAP;
				if (Dungeon.visible[heap.pos]) {
					CellEmitter.center( heap.pos ).burst( Speck.factory( Speck.COIN ), 2 );
				}
				break;
			case MIMIC:
				heap.type = Type.HEAP;
				heap.sprite.link();
				Sample.INSTANCE.play( Assets.SND_CURSED, 0.3f, 0.3f, Random.Float( 0.6f, 0.9f ) );
				break;
			default:
			}
		}
		
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();
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
	public String desc() {
		return Babylon.get().getFromResources("scroll_wipeout_desc");
	}
	
	@Override
	public int price() {
		return 100 * quantity;
	}
}

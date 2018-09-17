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
package com.ekdorn.pixel610.classicdungeon.items.scrolls;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Sleep;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;

public class ScrollOfLullaby extends Scroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_lullaby");
	}
	
	@Override
	protected void doRead() {
		
		curUser.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
		Sample.INSTANCE.play( Assets.SND_LULLABY );
		Invisibility.dispel();
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				Buff.affect( mob, Sleep.class );
				if (mob.buff( Sleep.class ) != null) {
					affected = mob;
					count++;
				}
			}
		}
		
		switch (count) {
		case 0:
			GLog.i(Babylon.get().getFromResources("scroll_lullaby_aff0"));
			break;
		case 1:
			GLog.i(Babylon.get().getFromResources("scroll_lullaby_aff1"), affected.name );
			break;
		default:
			GLog.i(Babylon.get().getFromResources("scroll_lullaby_affmany"));
		}
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_lullaby_desc");
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}

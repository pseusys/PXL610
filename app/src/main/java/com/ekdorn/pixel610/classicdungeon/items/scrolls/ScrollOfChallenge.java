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
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Rage;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mimic;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.items.Heap;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;

public class ScrollOfChallenge extends Scroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_challenge");
	}
	
	@Override
	protected void doRead() {
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			mob.beckon( curUser.pos );
			if (Dungeon.visible[mob.pos]) {
				Buff.affect( mob, Rage.class, Level.distance( curUser.pos, mob.pos ) );
			}
		}
		
		for (Heap heap : Dungeon.level.heaps.values()) {
			if (heap.type == Heap.Type.MIMIC) {
				Mimic m = Mimic.spawnAt( heap.pos, heap.items );
				if (m != null) {
					m.beckon( curUser.pos );
					heap.destroy();
				}
			}
		}
		
		GLog.w(Babylon.get().getFromResources("scroll_challenge_read"));
		setKnown();
		
		curUser.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );		
		Sample.INSTANCE.play( Assets.SND_CHALLENGE );
		Invisibility.dispel();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_challenge_desc");
	}
}

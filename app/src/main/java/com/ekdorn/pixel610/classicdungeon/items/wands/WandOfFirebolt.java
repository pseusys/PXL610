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
package com.ekdorn.pixel610.classicdungeon.items.wands;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.ResultDescriptions;
import com.ekdorn.pixel610.classicdungeon.actors.Actor;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.blobs.Blob;
import com.ekdorn.pixel610.classicdungeon.actors.blobs.Fire;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.classicdungeon.effects.MagicMissile;
import com.ekdorn.pixel610.classicdungeon.effects.particles.FlameParticle;
import com.ekdorn.pixel610.classicdungeon.levels.Level;
import com.ekdorn.pixel610.classicdungeon.mechanics.Ballistica;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Callback;
import com.ekdorn.pixel610.utils.Random;

public class WandOfFirebolt extends Wand {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("wand_firebolt");
	}
	
	@Override
	protected void onZap( int cell ) {

		int level = power();
		
		for (int i=1; i < Ballistica.distance - 1; i++) {
			int c = Ballistica.trace[i];
			if (Level.flamable[c]) {
				GameScene.add( Blob.seed( c, 1, Fire.class ) );
			}
		}
		
		GameScene.add( Blob.seed( cell, 1, Fire.class ) );
					
		Char ch = Actor.findChar( cell );
		if (ch != null) {	
			
			ch.damage( Random.Int( 1, 8 + level * level ), this );
			Buff.affect( ch, Burning.class ).reignite( ch );

			ch.sprite.emitter().burst( FlameParticle.FACTORY, 5 );
			
			if (ch == curUser && !ch.isAlive()) {
				Dungeon.fail( Utils.format( ResultDescriptions.WAND, name, Dungeon.depth ) );
				GLog.n(Babylon.get().getFromResources("wand_firebolt_suicide"));
			}
		}
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.fire( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("wand_firebolt_desc");
	}
}

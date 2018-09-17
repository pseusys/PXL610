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
package com.ekdorn.pixel610.classicdungeon.levels.features;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.effects.CellEmitter;
import com.ekdorn.pixel610.classicdungeon.effects.particles.ElmoParticle;
import com.ekdorn.pixel610.classicdungeon.levels.DeadEndLevel;
import com.ekdorn.pixel610.classicdungeon.levels.Terrain;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.windows.WndMessage;

public class Sign {
	
	private static final String[] TIPS = {
			Babylon.get().getFromResources("feature_sign_tip0"),
			Babylon.get().getFromResources("feature_sign_tip1"),
			Babylon.get().getFromResources("feature_sign_tip2"),
			Babylon.get().getFromResources("feature_sign_tip3"),
			Babylon.get().getFromResources("feature_sign_tip_4fin"),

			Babylon.get().getFromResources("feature_sign_tip5"),
			Babylon.get().getFromResources("feature_sign_tip6"),
			Babylon.get().getFromResources("feature_sign_tip7"),
			Babylon.get().getFromResources("feature_sign_tip8"),
			Babylon.get().getFromResources("feature_sign_tip9fin"),

			Babylon.get().getFromResources("feature_sign_tip10"),
			Babylon.get().getFromResources("feature_sign_tip11"),
			Babylon.get().getFromResources("feature_sign_tip12"),
			Babylon.get().getFromResources("feature_sign_tip13"),
			Babylon.get().getFromResources("feature_sign_tip14fin"),

			Babylon.get().getFromResources("feature_sign_tip15"),
			Babylon.get().getFromResources("feature_sign_tip16"),
			Babylon.get().getFromResources("feature_sign_tip17"),
			Babylon.get().getFromResources("feature_sign_tip18"),
			Babylon.get().getFromResources("feature_sign_tip19fin"),

			Babylon.get().getFromResources("feature_sign_tip20")
	};
	
	public static void read( int pos ) {
		
		if (Dungeon.level instanceof DeadEndLevel) {
			
			GameScene.show( new WndMessage( Babylon.get().getFromResources("feature_sign_deadend") ) );
			
		} else {
			
			int index = Dungeon.depth - 1;
			
			if (index < TIPS.length) {
				GameScene.show( new WndMessage( TIPS[index] ) );
			} else {
				
				Dungeon.level.destroy( pos );
				GameScene.updateMap( pos );
				GameScene.discoverTile( pos, Terrain.SIGN );
				
				CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 6 );
				Sample.INSTANCE.play( Assets.SND_BURNING );
				
				GLog.w( Babylon.get().getFromResources("feature_sign_burn") );
				
			}
		}
	}
}

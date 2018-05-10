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

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ElmoParticle;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;

public class MageArmor extends ClassArmor {
	
	{
		name = Babylon.get().getFromResources("armor_mage");
		image = ItemSpriteSheet.ARMOR_MAGE;
	}
	
	@Override
	public String special() {
		return Babylon.get().getFromResources("armor_mage_buff");
	}
	
	@Override
	public String desc() {
		return
				Babylon.get().getFromResources("armor_mage_desc");
	}
	
	@Override
	public void doSpecial() {	

		for (Mob mob : Dungeon.level.mobs) {
			if (Level.fieldOfView[mob.pos]) {
				Buff.affect( mob, Burning.class ).reignite( mob );
				Buff.prolong( mob, Roots.class, 3 );
			}
		}
		
		curUser.HP -= (curUser.HP / 3);
		
		curUser.spend( Actor.TICK );
		curUser.sprite.operate( curUser.pos );
		curUser.busy();
		
		curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
		Sample.INSTANCE.play( Assets.SND_READ );
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		if (hero.heroClass == HeroClass.MAGE) {
			return super.doEquip( hero );
		} else {
			GLog.w( Babylon.get().getFromResources("armor_notamage") );
			return false;
		}
	}
}
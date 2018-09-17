package com.ekdorn.pixel610.classicdungeon.plants;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.Dungeon;
import com.ekdorn.pixel610.classicdungeon.actors.Char;
import com.ekdorn.pixel610.classicdungeon.actors.blobs.Blob;
import com.ekdorn.pixel610.classicdungeon.actors.blobs.ToxicGas;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.classicdungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.classicdungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.classicdungeon.effects.CellEmitter;
import com.ekdorn.pixel610.classicdungeon.effects.Speck;
import com.ekdorn.pixel610.classicdungeon.items.bags.Bag;
import com.ekdorn.pixel610.classicdungeon.items.potions.PotionOfStrength;
import com.ekdorn.pixel610.classicdungeon.scenes.GameScene;
import com.ekdorn.pixel610.classicdungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.classicdungeon.utils.GLog;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;

public class Rotberry extends Plant {
	
	{
		image = 7;
		plantName = Utils.capitalize(Babylon.get().getFromResources("plants_plantof")) + " " + Babylon.get().getFromResources("plants_rotberry");
	}
	
	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		
		GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
		
		Dungeon.level.drop( new Seed(), pos ).sprite.drop();
		
		if (ch != null) {
			Buff.prolong( ch, Roots.class, Roots.TICK * 3 );
		}
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("plants_rotberry_desc");
	}
	
	public static class Seed extends Plant.Seed {
		{
			plantName = Babylon.get().getFromResources("plants_rotberry");
			
			name = Babylon.get().getFromResources("plants_seedof") + " " + plantName;
			image = ItemSpriteSheet.SEED_ROTBERRY;
			
			plantClass = Rotberry.class;
			alchemyClass = PotionOfStrength.class;
		}
		
		@Override
		public boolean collect( Bag container ) {
			if (super.collect( container )) {
				
				if (Dungeon.level != null) {
					for (Mob mob : Dungeon.level.mobs) {
						mob.beckon( Dungeon.hero.pos );
					}
					
					GLog.w(Babylon.get().getFromResources("plants_rotberry_seedroar"));
					CellEmitter.center( Dungeon.hero.pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
					Sample.INSTANCE.play( Assets.SND_CHALLENGE );
				}
				
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String desc() {
			return Babylon.get().getFromResources("plants_rotberry_desc");
		}
	}
}
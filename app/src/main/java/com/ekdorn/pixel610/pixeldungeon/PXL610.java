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
package com.ekdorn.pixel610.pixeldungeon;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.audio.Music;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.additional.GameMode;
import com.ekdorn.pixel610.pixeldungeon.internet.Authentication;
import com.ekdorn.pixel610.pixeldungeon.internet.FireBaser;
import com.ekdorn.pixel610.pixeldungeon.internet.InDev;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;
import com.ekdorn.pixel610.pixeldungeon.scenes.TitleScene;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class PXL610 extends Game {
	
	public PXL610() {
		super( TitleScene.class );
		
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfUpgrade.class,
			"com.watabou.pixeldungeon.items.scrolls.ScrollOfEnhancement" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.blobs.WaterOfHealth.class,
			"com.watabou.pixeldungeon.actors.blobs.Light" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfMending.class,
			"com.watabou.pixeldungeon.items.rings.RingOfRejuvenation" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfReach.class,
			"com.watabou.pixeldungeon.items.wands.WandOfTelekenesis" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.blobs.Foliage.class,
			"com.watabou.pixeldungeon.actors.blobs.Blooming" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.buffs.Shadows.class,
			"com.watabou.pixeldungeon.actors.buffs.Rejuvenation" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfPsionicBlast.class,
			"com.watabou.pixeldungeon.items.scrolls.ScrollOfNuclearBlast" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero.class,
			"com.watabou.pixeldungeon.actors.Hero" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.Shopkeeper.class,
			"com.watabou.pixeldungeon.actors.mobs.Shopkeeper" );
		// 1.6.1
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.quest.DriedRose.class,
			"com.watabou.pixeldungeon.items.DriedRose" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.MirrorImage.class,
			"com.watabou.pixeldungeon.items.scrolls.ScrollOfMirrorImage$MirrorImage" );
		// 1.6.4
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfElements.class,
			"com.watabou.pixeldungeon.items.rings.RingOfCleansing" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfElements.class,
			"com.watabou.pixeldungeon.items.rings.RingOfResistance" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.weapon.missiles.Boomerang.class,
			"com.watabou.pixeldungeon.items.weapon.missiles.RangersBoomerang" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.rings.RingOfPower.class,
			"com.watabou.pixeldungeon.items.rings.RingOfEnergy" );
		// 1.7.2
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.plants.Dreamweed.class,
			"com.watabou.pixeldungeon.plants.Blindweed" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.plants.Dreamweed.Seed.class,
			"com.watabou.pixeldungeon.plants.Blindweed$Seed" );
		// 1.7.4
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.weapon.enchantments.Shock.class,
			"com.watabou.pixeldungeon.items.weapon.enchantments.Piercing" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.weapon.enchantments.Shock.class,
			"com.watabou.pixeldungeon.items.weapon.enchantments.Swing" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfEnchantment.class,
			"com.watabou.pixeldungeon.items.scrolls.ScrollOfWeaponUpgrade" );
		// 1.7.5
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.scrolls.ScrollOfEnchantment.class,
			"com.watabou.pixeldungeon.items.Stylus" );
		// 1.8.0
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.actors.mobs.FetidRat.class,
			"com.watabou.pixeldungeon.actors.mobs.npcs.Ghost$FetidRat" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.plants.Rotberry.class,
			"com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker$Rotberry" );
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.plants.Rotberry.Seed.class,
			"com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker$Rotberry$Seed" );
		// 1.9.0
		com.ekdorn.pixel610.utils.Bundle.addAlias(
			com.ekdorn.pixel610.pixeldungeon.items.wands.WandOfReach.class,
			"com.watabou.pixeldungeon.items.wands.WandOfTelekinesis" );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		updateImmersiveMode();

		DisplayMetrics metrics = new DisplayMetrics();
		instance.getWindowManager().getDefaultDisplay().getMetrics( metrics );
		boolean landscape = metrics.widthPixels > metrics.heightPixels;

		this.gameMode = GameMode.init(PXL610.gamemode()); // PXL610: update gamemode;

		//try{
			//if (getPackageManager().getPackageInfo(getPackageName(), 0 ).lastUpdateTime > lastlaunch()) {
				Updater.update(getApplicationContext()); // PXL610: updating old functions directly;
			//}
		//} catch (PackageManager.NameNotFoundException nne) {
			//Updater.update(getApplicationContext());
		//}
		lastlaunch(Calendar.getInstance().getTimeInMillis());

		if (FirebaseAuth.getInstance().getCurrentUser() == null) { // PXL610: update id;
			Babylon.get().updateLocale();
			/*String id = Inviter.updateID();
			PXL610.user_name( id );
			Inviter.publishID(id);*/

			Log.e("TAG", "onCreate: AUTH");
			Authentication auth = new Authentication(PXL610.this);
			auth.show();
		} else {
			FireBaser.loadBonus(PXL610.user_name());
			InDev.loadSuperuserName();
		}

		Babylon.get().load();

		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_LANDSCAPE, false ) != landscape) {
			landscape( !landscape );
		}
		
		Music.INSTANCE.enable( music() );
		Sample.INSTANCE.enable( soundFx() );
		
		Sample.INSTANCE.load( 
			Assets.SND_CLICK, 
			Assets.SND_BADGE, 
			Assets.SND_GOLD,
			
			Assets.SND_DESCEND,
			Assets.SND_STEP,
			Assets.SND_WATER,
			Assets.SND_OPEN,
			Assets.SND_UNLOCK,
			Assets.SND_ITEM,
			Assets.SND_DEWDROP, 
			Assets.SND_HIT, 
			Assets.SND_MISS,
			Assets.SND_EAT,
			Assets.SND_READ,
			Assets.SND_LULLABY,
			Assets.SND_DRINK,
			Assets.SND_SHATTER,
			Assets.SND_ZAP,
			Assets.SND_LIGHTNING,
			Assets.SND_LEVELUP,
			Assets.SND_DEATH,
			Assets.SND_CHALLENGE,
			Assets.SND_CURSED,
			Assets.SND_EVOKE,
			Assets.SND_TRAP,
			Assets.SND_TOMB,
			Assets.SND_ALERT,
			Assets.SND_MELD,
			Assets.SND_BOSS,
			Assets.SND_BLAST,
			Assets.SND_PLANT,
			Assets.SND_RAY,
			Assets.SND_BEACON,
			Assets.SND_TELEPORT,
			Assets.SND_CHARMS,
			Assets.SND_MASTERY,
			Assets.SND_PUFF,
			Assets.SND_ROCKS,
			Assets.SND_BURNING,
			Assets.SND_FALLING,
			Assets.SND_GHOST,
			Assets.SND_SECRET,
			Assets.SND_BONES,
			Assets.SND_BEE,
			Assets.SND_DEGRADE,
			Assets.SND_MIMIC );
	}

	@Override
	public void onWindowFocusChanged( boolean hasFocus ) {
		
		super.onWindowFocusChanged( hasFocus );
		
		if (hasFocus) {
			updateImmersiveMode();
		}
	}
	
	public static void switchNoFade( Class<? extends PixelScene> c ) {
		PixelScene.noFade = true;
		switchScene( c );
	}
	
	/*
	 * ---> Prefernces
	 */
	
	public static void landscape( boolean value ) {
		Game.instance.setRequestedOrientation( value ?
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		Preferences.INSTANCE.put( Preferences.KEY_LANDSCAPE, value );
	}
	
	public static boolean landscape() {
		return width > height;
	}
	
	// *** IMMERSIVE MODE ****
	
	private static boolean immersiveModeChanged = false;
	
	@SuppressLint("NewApi")
	public static void immerse( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_IMMERSIVE, value );
		
		instance.runOnUiThread( new Runnable() {
			@Override
			public void run() {
				updateImmersiveMode();
				immersiveModeChanged = true;
			}
		} );
	}
	
	@Override
	public void onSurfaceChanged( GL10 gl, int width, int height ) {
		super.onSurfaceChanged( gl, width, height );
		
		if (immersiveModeChanged) {
			requestedReset = true;
			immersiveModeChanged = false;
		}
	}
	
	@SuppressLint("NewApi")
	public static void updateImmersiveMode() {
		if (android.os.Build.VERSION.SDK_INT >= 19) {
			try {
				// Sometime NullPointerException happens here
				instance.getWindow().getDecorView().setSystemUiVisibility( 
					immersed() ?
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE | 
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | 
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | 
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
					View.SYSTEM_UI_FLAG_FULLSCREEN | 
					View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY 
					:
					0 );
			} catch (Exception e) {
				reportException( e );
			}
		}
	}
	
	public static boolean immersed() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_IMMERSIVE, false );
	}
	
	// *****************************
	
	public static void scaleUp( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SCALE_UP, value );
		switchScene( TitleScene.class );
	}
	
	public static boolean scaleUp() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_SCALE_UP, true );
	}

	public static void zoom( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_ZOOM, value );
	}
	
	public static int zoom() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_ZOOM, 0 );
	}
	
	public static void music( boolean value ) {
		Music.INSTANCE.enable( value );
		Preferences.INSTANCE.put( Preferences.KEY_MUSIC, value );
	}
	
	public static boolean music() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_MUSIC, true );
	}
	
	public static void soundFx( boolean value ) {
		Sample.INSTANCE.enable( value );
		Preferences.INSTANCE.put( Preferences.KEY_SOUND_FX, value );
	}
	
	public static boolean soundFx() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_SOUND_FX, true );
	}
	
	public static void brightness( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_BRIGHTNESS, value );
		if (scene() instanceof GameScene) {
			((GameScene)scene()).brightness( value );
		}
	}
	
	public static boolean brightness() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_BRIGHTNESS, false );
	}
	
	public static void invited( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_INVITED, value );
	}

	public static boolean invited() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_INVITED, false );
	}

	public static void bonus( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_BONUS, value );
	}

	public static int bonus() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_BONUS, 0 );
	}
	
	public static void lastClass( String mode, int value ) {
		int result = Preferences.INSTANCE.getInt( Preferences.KEY_LAST_CLASS, 0 );
		switch (mode) {
			case GameMode.original:
				result -= ((result % 10) - value);
				break;
			case GameMode.dlc1:
				result -= ((result % 100) - value*10);
				break;
		}
		Preferences.INSTANCE.put( Preferences.KEY_LAST_CLASS, result );
	}
	
	public static int lastClass( String mode ) {
		switch (mode) {
			case GameMode.original:
				return (Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0) % 10);
			case GameMode.dlc1:
				return (Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0) / 10) % 10;
			default:
				return Preferences.INSTANCE.getInt(Preferences.KEY_LAST_CLASS, 0);
		}
	}
	
	public static void challenges( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_CHALLENGES, value );
	}
	
	public static int challenges() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_CHALLENGES, 0 );
	}
	
	public static void gameIntro( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_GAME_INTRO, value );
	}
	
	public static boolean gameIntro() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_GAME_INTRO, true );
	}

	public static void localisation( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LOCALISATION, value );
	}

	public static String localisation() {
		return Preferences.INSTANCE.getString( Preferences.KEY_LOCALISATION, "ru" );
	}

	public static void user_name( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_USER_NAME, value );
	}

	public static String user_name() {
		return Preferences.INSTANCE.getString( Preferences.KEY_USER_NAME, "" );
	}

	public static void gamemode( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_GAMEMODE, value );
	}

	public static String gamemode() {
		return Preferences.INSTANCE.getString( Preferences.KEY_GAMEMODE, GameMode.original);
	}

	public static void lastlaunch( Long value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LAST_LAUNCH, value );
	}

	public static long lastlaunch() {
		return Preferences.INSTANCE.getLong( Preferences.KEY_LAST_LAUNCH, -1L);
	}

	public static void superuser_name( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SUPERUSER_NAME, value );
	}

	public static String superuser_name() {
		return Preferences.INSTANCE.getString( Preferences.KEY_SUPERUSER_NAME, InDev.developer_key );
	}
	
	/*
	 * <--- Preferences
	 */
	
	public static void reportException( Throwable tr ) {
		Log.e( "PD", Log.getStackTraceString( tr ) ); 
	}
}
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
package com.watabou.pixeldungeon.windows;

import android.util.Log;

import com.watabou.noosa.Camera;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Babylon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Toolbar;
import com.watabou.pixeldungeon.ui.Window;

import java.util.Locale;

public class WndSettings extends Window {
	
	private static final String TXT_PLUS			= "+";
	private static final String TXT_MINUS		    = "-";

	private static final String TXT_UP			    = "->";
	private static final String TXT_BACK		    = "<-";
	
	private static final int WIDTH		= 112;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP 		= 2;
	
	private RedButton btnZoomOut;
	private RedButton btnZoomIn;
	private RedButton btnLocalisation;

	public WndSettings( boolean inGame ) {
		super();
		
		CheckBox btnImmersive = null;


		
		if (inGame) {
			int w = BTN_HEIGHT;
			
			btnZoomOut = new RedButton( TXT_MINUS, true ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom - 1 );
				}
			};
			add( btnZoomOut.setRect( 0, 0, w, BTN_HEIGHT) );
			
			btnZoomIn = new RedButton( TXT_PLUS, true ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom + 1 );
				}
			};
			add( btnZoomIn.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );
			
			add( new RedButton("sett_default_zoom", false ) {
				@Override
				protected void onClick() {
					zoom( PixelScene.defaultZoom );
				}
			}.setRect( btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT ) );
			
			updateEnabled();
			
		} else {

			int w = BTN_HEIGHT;

			// Difficulty Levels
			RedButton btnLanguageBack = new RedButton(TXT_BACK, true) {
				@Override
				protected void onClick() {
					btnLocalisation.text(Babylon.get().changeLocale(-1));
					WndSettings.this.langUpdate();
					((TitleScene) PixelDungeon.scene()).localUpdate();
				}
			};
			add( btnLanguageBack.setRect( 0, 0, w, BTN_HEIGHT) );

			RedButton btnLanguageUp = new RedButton(TXT_UP, true) {
				@Override
				protected void onClick() {
					btnLocalisation.text(Babylon.get().changeLocale(1));
					WndSettings.this.langUpdate();
					((TitleScene) PixelDungeon.scene()).localUpdate();
				}
			};
			add (btnLanguageUp.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );

			btnLocalisation = new RedButton( Babylon.get().getLanguageName(), true ) {
				@Override
				protected void onClick() {
				}
			};
			add(btnLocalisation.setRect(btnLanguageBack.right(), 0, WIDTH - btnLanguageBack.width() - btnLanguageBack.width(), BTN_HEIGHT) );

			// Previous settings
			CheckBox btnScaleUp = new CheckBox( "sett_scale_up", false ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.scaleUp( checked() );
				}
			};
			btnScaleUp.setRect( 0, BTN_HEIGHT + GAP, WIDTH, BTN_HEIGHT );
			btnScaleUp.checked( PixelDungeon.scaleUp() );
			add( btnScaleUp );
			
			btnImmersive = new CheckBox( "sett_immersive", false ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.immerse( checked() );
				}
			};
			btnImmersive.setRect( 0, btnScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnImmersive.checked( PixelDungeon.immersed() );
			btnImmersive.enable( android.os.Build.VERSION.SDK_INT >= 19 );
			add( btnImmersive );
			
		}
		
		CheckBox btnMusic = new CheckBox( "sett_music", false ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.music( checked() );
			}
		};
		btnMusic.setRect( 0, (btnImmersive != null ? btnImmersive.bottom() : BTN_HEIGHT) + GAP, WIDTH, BTN_HEIGHT );
		btnMusic.checked( PixelDungeon.music() );
		add( btnMusic );
		
		CheckBox btnSound = new CheckBox( "sett_sound", false ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.soundFx( checked() );
				Sample.INSTANCE.play( Assets.SND_CLICK );
			}
		};
		btnSound.setRect( 0, btnMusic.bottom() + GAP, WIDTH, BTN_HEIGHT );
		btnSound.checked( PixelDungeon.soundFx() );
		add( btnSound );
		
		if (inGame) {
			
			CheckBox btnBrightness = new CheckBox( "sett_brightness", false ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.brightness( checked() );
				}
			};
			btnBrightness.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnBrightness.checked( PixelDungeon.brightness() );
			add( btnBrightness );
			
			CheckBox btnQuickslot = new CheckBox( "sett_quick_slot", false ) {
				@Override
				protected void onClick() {
					super.onClick();
					Toolbar.secondQuickslot( checked() );
				}
			};
			btnQuickslot.setRect( 0, btnBrightness.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnQuickslot.checked( Toolbar.secondQuickslot() );
			add( btnQuickslot );
			
			resize( WIDTH, (int)btnQuickslot.bottom() );
			
		} else {
			
			RedButton btnOrientation = new RedButton( orientationText(), false ) {
				@Override
				protected void onClick() {
					PixelDungeon.landscape( !PixelDungeon.landscape() );
				}
			};
			btnOrientation.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnOrientation );
			
			resize( WIDTH, (int)btnOrientation.bottom() );
			
		}
	}

	public void langUpdate() {
		for (Gizmo gizmo: WndSettings.this.members) {
			if (gizmo instanceof RedButton) {
				((RedButton) gizmo).localUpdate();
			}
		}
	}

	private void zoom(float value ) {

		Camera.main.zoom( value );
		PixelDungeon.zoom( (int)(value - PixelScene.defaultZoom) );

		updateEnabled();
	}
	
	private void updateEnabled() {
		float zoom = Camera.main.zoom;
		btnZoomIn.enable( zoom < PixelScene.maxZoom );
		btnZoomOut.enable( zoom > PixelScene.minZoom );
	}
	
	private String orientationText() {
		return PixelDungeon.landscape() ? "sett_switch_port" : "sett_switch_land";
	}
}

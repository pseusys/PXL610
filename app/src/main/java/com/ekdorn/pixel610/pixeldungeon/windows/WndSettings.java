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
package com.ekdorn.pixel610.pixeldungeon.windows;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;
import com.ekdorn.pixel610.pixeldungeon.scenes.TitleScene;
import com.ekdorn.pixel610.pixeldungeon.ui.CheckBox;
import com.ekdorn.pixel610.pixeldungeon.ui.RedButton;
import com.ekdorn.pixel610.pixeldungeon.ui.Toolbar;
import com.ekdorn.pixel610.pixeldungeon.ui.Window;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

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

	public WndSettings( boolean inGame ) {
		super();

		CheckBox btnImmersive = null;



		if (inGame) {
			int w = BTN_HEIGHT;

			btnZoomOut = new RedButton( TXT_MINUS ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom - 1 );
				}
			};
			add( btnZoomOut.setRect( 0, 0, w, BTN_HEIGHT) );

			btnZoomIn = new RedButton( TXT_PLUS ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom + 1 );
				}
			};
			add( btnZoomIn.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );

			add( new RedButton(Babylon.get().getFromResources("set_default_zoom") ) {
				@Override
				protected void onClick() {
					zoom( PixelScene.defaultZoom );
				}
			}.setRect( btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT ) );

			updateEnabled();

		} else {

			int w = BTN_HEIGHT;

			// Localisation Settings
			RedButton btnLanguageBack = new RedButton(TXT_BACK) {
				@Override
				protected void onClick() {
					Babylon.get().changeLocale(-1);
					WndSettings.this.hide();
					((TitleScene) PXL610.scene()).localUpdate();
				}
			};
			add( btnLanguageBack.setRect( 0, 0, w, BTN_HEIGHT) );

			RedButton btnLanguageUp = new RedButton(TXT_UP) {
				@Override
				protected void onClick() {
					Babylon.get().changeLocale(1);
					WndSettings.this.hide();
					((TitleScene) PXL610.scene()).localUpdate();
				}
			};
			add (btnLanguageUp.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );

			RedButton btnLocalisation = new RedButton(Babylon.get().getLanguageName()) {
				@Override
				protected void onClick() {}
			};
			add(btnLocalisation.setRect(btnLanguageBack.right(), 0, WIDTH - btnLanguageBack.width() - btnLanguageBack.width(), BTN_HEIGHT) );

			// Name Settings
			RedButton btnName = new RedButton(PXL610.user_name()) {
				@Override
				protected void onClick() {
					hide();
					dialog(false);
				}
			};
			add( btnName.setRect( 0, btnLocalisation.bottom() + GAP, WIDTH, BTN_HEIGHT) );

			// Previous settings
			CheckBox btnScaleUp = new CheckBox( Babylon.get().getFromResources("sett_scale_up") ) {
				@Override
				protected void onClick() {
					super.onClick();
					PXL610.scaleUp( checked() );
				}
			};
			btnScaleUp.setRect( 0, btnName.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnScaleUp.checked( PXL610.scaleUp() );
			add( btnScaleUp );

			btnImmersive = new CheckBox( Babylon.get().getFromResources("sett_immersive") ) {
				@Override
				protected void onClick() {
					super.onClick();
					PXL610.immerse( checked() );
				}
			};
			btnImmersive.setRect( 0, btnScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnImmersive.checked( PXL610.immersed() );
			btnImmersive.enable( android.os.Build.VERSION.SDK_INT >= 19 );
			add( btnImmersive );

		}

		CheckBox btnMusic = new CheckBox( Babylon.get().getFromResources("sett_music") ) {
			@Override
			protected void onClick() {
				super.onClick();
				PXL610.music( checked() );
			}
		};
		btnMusic.setRect( 0, (btnImmersive != null ? btnImmersive.bottom() : BTN_HEIGHT) + GAP, WIDTH, BTN_HEIGHT );
		btnMusic.checked( PXL610.music() );
		add( btnMusic );

		CheckBox btnSound = new CheckBox( Babylon.get().getFromResources("sett_sound") ) {
			@Override
			protected void onClick() {
				super.onClick();
				PXL610.soundFx( checked() );
				Sample.INSTANCE.play( Assets.SND_CLICK );
			}
		};
		btnSound.setRect( 0, btnMusic.bottom() + GAP, WIDTH, BTN_HEIGHT );
		btnSound.checked( PXL610.soundFx() );
		add( btnSound );

		if (inGame) {

			CheckBox btnBrightness = new CheckBox( Babylon.get().getFromResources("sett_brightness") ) {
				@Override
				protected void onClick() {
					super.onClick();
					PXL610.brightness( checked() );
				}
			};
			btnBrightness.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnBrightness.checked( PXL610.brightness() );
			add( btnBrightness );

			CheckBox btnQuickslot = new CheckBox( Babylon.get().getFromResources("sett_quick_slot") ) {
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

			RedButton btnOrientation = new RedButton( orientationText() ) {
				@Override
				protected void onClick() {
					PXL610.landscape( !PXL610.landscape() );
				}
			};
			btnOrientation.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
			add( btnOrientation );

			resize( WIDTH, (int)btnOrientation.bottom() );

		}
	}

	private void zoom(float value ) {

		Camera.main.zoom( value );
		PXL610.zoom( (int)(value - PixelScene.defaultZoom) );

		updateEnabled();
	}

	private void updateEnabled() {
		float zoom = Camera.main.zoom;
		btnZoomIn.enable( zoom < PixelScene.maxZoom );
		btnZoomOut.enable( zoom > PixelScene.minZoom );
	}

	private String orientationText() {
		return PXL610.landscape() ? Babylon.get().getFromResources("sett_switch_port") : Babylon.get().getFromResources("sett_switch_land");
	}

	public static void dialog(final boolean onLoad) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		Runnable dialog = new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(Game.instance);
				builder.setTitle(Babylon.get().getFromResources("name_change_dialog_title"));

				builder.setCancelable(false);
				// Set up the input
				final EditText input = new EditText(Game.instance);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				input.setInputType(InputType.TYPE_CLASS_TEXT);
				input.setText(PXL610.user_name());
				input.setSelection(input.getText().length());
				input.setFocusable(true);
				builder.setView(input);

				// Set up the buttons
				builder.setPositiveButton(Babylon.get().getFromResources("name_change_dialog_agreed"), null);
				final AlertDialog act = builder.show();
				input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						}
					}
				});
				input.requestFocus();
				act.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!input.getText().toString().equals("")) {
							String pseudoname = input.getText().toString();
							boolean nameSuggest = true;
							char wrongChar = 'I';
							for (int i = 0; i < pseudoname.length(); i++) {
								if (!BitmapText.Font.FULL.contains(Character.toString(pseudoname.charAt(i)))) {
									nameSuggest = false;
									wrongChar = pseudoname.charAt(i);
								}
							}
							if (nameSuggest) {
								PXL610.user_name(pseudoname);
								if (onLoad) {
									act.dismiss();
								} else {
									act.dismiss();
									Game.scene().add(new WndSettings( false ));
								}
							} else {
								Toast.makeText(Game.instance, Utils.format(Babylon.get().getFromResources("name_change_dialog_error"), Character.toString(wrongChar)), Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			}
		};
		mainHandler.post(dialog);
	}
}
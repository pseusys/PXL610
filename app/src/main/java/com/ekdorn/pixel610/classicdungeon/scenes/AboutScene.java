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
package com.ekdorn.pixel610.classicdungeon.scenes;

import android.content.Intent;
import android.net.Uri;

import com.ekdorn.pixel610.input.Touchscreen.Touch;
import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.TouchArea;
import com.ekdorn.pixel610.classicdungeon.Babylon;
import com.ekdorn.pixel610.classicdungeon.PXL610;
import com.ekdorn.pixel610.classicdungeon.effects.Flare;
import com.ekdorn.pixel610.classicdungeon.ui.Archs;
import com.ekdorn.pixel610.classicdungeon.ui.ExitButton;
import com.ekdorn.pixel610.classicdungeon.ui.Icons;
import com.ekdorn.pixel610.classicdungeon.ui.Window;

public class AboutScene extends PixelScene {
	
	private static final String LNK = "www.instagram.com/ek.dorn";
	
	@Override
	public void create() {
		super.create();
		
		BitmapTextMultiline text = createMultiline( Babylon.get().getFromResources("aboutscene_titles"), 8 );
		text.maxWidth = Math.min( Camera.main.width, 120 );
		text.measure();
		add( text );
		
		text.x = align( (Camera.main.width - text.width()) / 2 );
		text.y = align( (Camera.main.height - text.height()) / 2 );
		
		BitmapTextMultiline link = createMultiline( LNK, 8 );
		link.maxWidth = Math.min( Camera.main.width, 120 );
		link.measure();
		link.hardlight( Window.TITLE_COLOR );
		add( link );
		
		link.x = text.x;
		link.y = text.y + text.height();
		
		TouchArea hotArea = new TouchArea( link ) {
			@Override
			protected void onClick( Touch touch ) {
				Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://" + LNK ) );
				Game.instance.startActivity( intent );
			}
		};
		add( hotArea );
		
		Image wata = Icons.WATA.get();
		Image ek = Icons.EK.get();
		Image Nkg = Icons.NKG.get();

		float binX = ek.width + 16 + wata.width + 16 + Nkg.width;
		float binY = Math.max(ek.height, wata.height);

		wata.x = align( (Camera.main.width - binX) / 2 );
		wata.y = align( text.y - (binY + wata.height)/2 - 8);
		add( wata );
		ek.x = align( wata.x + wata.width + 16 );
		ek.y = text.y - (binY + ek.height)/2 - 8;
		add( ek );
		Nkg.x = align( ek.x + ek.width + 16 );
		Nkg.y = text.y - (binY + ek.height)/2 - 8;
		add( Nkg );
		
		new Flare( 7, 32 ).color( 0x112233, true ).show( wata, 0 ).angularSpeed = +40;
		new Flare( 7, 32 ).color( 0x332211, true ).show( ek, 0 ).angularSpeed = -45;
		new Flare( 7, 32 ).color( 0x661278, true ).show( Nkg, 0 ).angularSpeed = +100;
		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		PXL610.switchNoFade( TitleScene.class );
	}
}

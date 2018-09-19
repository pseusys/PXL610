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
package com.ekdorn.pixel610.pixeldungeon.ui;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.scenes.ModeScene;
import com.ekdorn.pixel610.pixeldungeon.scenes.StartScene;
import com.ekdorn.pixel610.pixeldungeon.scenes.TitleScene;
import com.ekdorn.pixel610.utils.InDev;

public class ExitButton extends Button {
	
	private Image image;
	
	public ExitButton() {
		super();
		
		width = image.width;
		height = image.height;
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		
		image = Icons.EXIT.get();
		add( image );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		image.x = x;
		image.y = y;
	}
	
	@Override
	protected void onTouchDown() {
		image.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}
	
	@Override
	protected void onTouchUp() {
		image.resetColor();
	}
	
	@Override
	protected void onClick() {
		if (Game.scene() instanceof TitleScene) {
			Game.instance.finish();
		} else if ((Game.scene() instanceof StartScene) && (InDev.isDeveloper())) {
			PXL610.switchNoFade( ModeScene.class );
		} else {
			PXL610.switchNoFade( TitleScene.class );
		}
	}
}

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 EK DORN
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

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;

public class TextButton extends Button {
    private BitmapText bmptxt;

    public TextButton(String text) {
        super();

        this.bmptxt.text( text );
        this.bmptxt.measure();

        width = bmptxt.width;
        height = bmptxt.height;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        bmptxt = PixelScene.createText(9);
        add( bmptxt );
    }

    @Override
    protected void layout() {
        super.layout();

        bmptxt.x = x;
        bmptxt.y = y;
    }

    @Override
    protected void onTouchDown() {
        bmptxt.brightness( 1.5f );
        Sample.INSTANCE.play( Assets.SND_CLICK );
    }

    @Override
    protected void onTouchUp() {
        bmptxt.resetColor();
    }

    @Override
    protected void onClick() {}

    public BitmapText useText() {
        return this.bmptxt;
    }
}

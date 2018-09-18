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

package com.ekdorn.pixel610.pixeldungeon.windows;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.ui.Component;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.CharSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class WndScript extends WndTitledMessage {
    public WndScript( Mob mob, String script ) {

        super( new MobTitle( mob ), script );

    }

    private static class MobTitle extends Component {

        private static final int GAP	= 2;

        private CharSprite image;
        private BitmapText name;

        public MobTitle( Mob mob ) {

            name = PixelScene.createText( Utils.capitalize( mob.name ), 9 );
            name.hardlight( TITLE_COLOR );
            name.measure();
            add( name );

            image = mob.sprite();
            add( image );
        }

        @Override
        protected void layout() {

            image.x = 0;
            image.y = Math.max( 0, name.height() - image.height );

            name.x = image.width + GAP;
            name.y = image.y + (image.height() - name.height()) / 2;

            height = image.y + image.height();
        }
    }
}

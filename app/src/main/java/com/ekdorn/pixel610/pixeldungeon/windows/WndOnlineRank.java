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
import com.ekdorn.pixel610.noosa.Group;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.OnlineRatinger;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.HeroSprite;
import com.ekdorn.pixel610.pixeldungeon.ui.Window;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

import java.util.Map;

public class WndOnlineRank extends Window {
    private static final int WIDTH	= 112;
    private static final int GAP	= 4;

    public WndOnlineRank( final Map<String, Object> rec ) {

        super();

        IconTitle title = new IconTitle();
        System.out.println((String) rec.get(OnlineRatinger.CLASS) + " " + ((Number) rec.get(OnlineRatinger.SCORE)).intValue());
        title.icon( HeroSprite.avatar( OnlineRatinger.getClassById((String) rec.get(OnlineRatinger.CLASS)), ((Number) rec.get(OnlineRatinger.TIER)).intValue() ) );
        title.label( Utils.format( Babylon.get().getFromResources("wnd_hero_title"), ((Number) rec.get(OnlineRatinger.LEVEL)).intValue(),
                OnlineRatinger.getClassById((String) rec.get(OnlineRatinger.CLASS)).title() ).toUpperCase( Babylon.get().getCurrent() ) );
        title.setRect( 0, 0, WIDTH, 0 );
        add( title );

        float pos = title.bottom();

        pos += GAP + GAP;

        pos = statSlot( this, "",  (String) rec.get(OnlineRatinger.INFO), pos );

        pos += GAP + GAP;

        pos = statSlot( this, Babylon.get().getFromResources("wnd_rankings_gameduration"), Long.toString(((Number) rec.get(OnlineRatinger.DURATION)).longValue()), pos );
        pos = statSlot( this, Babylon.get().getFromResources("wnd_rankings_score"), ((Number) rec.get(OnlineRatinger.SCORE)).toString(), pos );
        pos = statSlot( this, Babylon.get().getFromResources("wnd_rankings_name"), (String) rec.get(OnlineRatinger.SENDER), pos );

        resize( WIDTH, (int) (pos + GAP) );
    }

    private float statSlot( Group parent, String label, String value, float pos ) {

        BitmapText txt = PixelScene.createText( label, 7 );
        txt.y = pos;
        parent.add( txt );

        txt = PixelScene.createText( value, 7 );
        txt.measure();
        txt.x = PixelScene.align( (label.equals("")) ? (0) : (WIDTH * 0.65f) );
        txt.y = pos;
        parent.add( txt );

        return pos + GAP + txt.baseLine();
    }
}

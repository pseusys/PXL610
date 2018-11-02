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
import com.ekdorn.pixel610.noosa.SkinnedBlock;
import com.ekdorn.pixel610.noosa.ui.Component;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.additional.GameMode;

public class Archs extends Component {

	private static final float SCROLL_SPEED	= 20f;
	
	private SkinnedBlock arcsBg;
	private SkinnedBlock arcsFg;
	
	private static float offsBy = 0;
	private static float offsFy = 0;
	private static float offsBx = 0;
	private static float offsFx = 0;
	
	public boolean reversed = false;
	private boolean vertical;

	public Archs() {
		this.vertical =  Game.instance.gameMode.outlook.vertical;
	}

	@Override
	protected void createChildren() {
		arcsBg = new SkinnedBlock( 1, 1, Game.instance.gameMode.outlook.archs0Asset );
		arcsBg.autoAdjust = true;
		arcsBg.offsetTo( vertical ? offsBx : 0, vertical ? 0 : offsBy );
		add( arcsBg );
		
		arcsFg = new SkinnedBlock( 1, 1, Game.instance.gameMode.outlook.archs1Asset );
		arcsFg.autoAdjust = true;
		arcsFg.offsetTo( vertical ? offsFx : 0, vertical ? 0 : offsFy );
		add( arcsFg );
	}
	
	@Override
	protected void layout() {
		arcsBg.size( width, height );
		arcsFg.size( width, height );

		arcsBg.offset(arcsBg.texture.width / 4 - (width % arcsBg.texture.width) / 2, 0);
		arcsFg.offset(arcsFg.texture.width / 4 - (width % arcsFg.texture.width) / 2, 0);
	}
	
	@Override
	public void update() {
		
		super.update();
		
		float shift = Game.elapsed * SCROLL_SPEED;
		if (reversed) {
			shift = -shift;
		}

		arcsBg.offset( shift * Game.instance.gameMode.outlook.archs0Xmult, shift * Game.instance.gameMode.outlook.archs0Ymult );
		arcsFg.offset( shift * Game.instance.gameMode.outlook.archs1Xmult, shift * Game.instance.gameMode.outlook.archs1Ymult );

		offsBy = arcsBg.offsetY();
		offsFy = arcsFg.offsetY();
		offsBx = arcsBg.offsetX();
		offsFx = arcsFg.offsetX();
	}
}

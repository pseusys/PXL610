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
package com.ekdorn.pixel610.pixeldungeon.scenes;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.audio.Music;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.OnlineRatinger;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.Rankings;
import com.ekdorn.pixel610.pixeldungeon.effects.Flare;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.ui.Archs;
import com.ekdorn.pixel610.pixeldungeon.ui.ExitButton;
import com.ekdorn.pixel610.pixeldungeon.ui.Icons;
import com.ekdorn.pixel610.pixeldungeon.ui.TextButton;
import com.ekdorn.pixel610.pixeldungeon.ui.Window;
import com.ekdorn.pixel610.pixeldungeon.windows.WndError;
import com.ekdorn.pixel610.pixeldungeon.windows.WndOnlineRank;
import com.ekdorn.pixel610.pixeldungeon.windows.WndRanking;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RankingsScene extends PixelScene {
	
	private static final int DEFAULT_COLOR	= 0xCCCCCC;
	
	private static final float ROW_HEIGHT_L	= 22;
	private static final float ROW_HEIGHT_P	= 28;
	
	private static final float MAX_ROW_WIDTH	= 180;
	
	private static final float GAP	= 4;

    private int w = 0;
	private int h = 0;

	private int pos;

	private TextButton switcher;
	private static boolean isLocal = true;
	
	@Override
	public void create() {
		
		super.create();
		
		Music.INSTANCE.play( Assets.THEME, true );
		Music.INSTANCE.volume( 1f );
		
		uiCamera.visible = false;
		
		w = Camera.main.width;
		h = Camera.main.height;

        Archs archs = new Archs();
		archs.setSize( w, h );
		add(archs);

		if (isLocal) {
			showLocalRanking();
		} else {
			showGlobalRanking();
		}
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}

	private void showLocalRanking() {
		Rankings.INSTANCE.load();

		if (Rankings.INSTANCE.records.size() > 0) {

			float rowHeight = PXL610.landscape() ? ROW_HEIGHT_L : ROW_HEIGHT_P;

			float left = (w - Math.min( MAX_ROW_WIDTH, w )) / 2 + GAP;
			float top = align( (h - rowHeight  * Rankings.INSTANCE.records.size()) / 2 );

			BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingscene_toprankings"), 9 );
			title.hardlight( Window.TITLE_COLOR );
			title.measure();
			title.x = align( (w - title.width()) / 2 );
			title.y = align( top - title.height() - GAP );
			add( title );

			switcher = new TextButton("->") {
				@Override
				protected void onClick() {
					isLocal = !isLocal;
					PXL610.switchNoFade( RankingsScene.class );
				}
			};
			switcher.useText().hardlight(Window.TITLE_COLOR);
			switcher.setPos(w - (w - title.width()) / 2 + GAP, top - title.height() - GAP);
			add(switcher);

			pos = 0;

			for (Rankings.Record rec : Rankings.INSTANCE.records) {
				LocalRecord row = new LocalRecord( pos, pos == Rankings.INSTANCE.lastRecord, rec );
				row.setRect( left, top + pos * rowHeight, w - left * 2, rowHeight );
				add( row );

				pos++;
			}

			if (Rankings.INSTANCE.totalNumber >= Rankings.TABLE_SIZE) {
				BitmapText label = PixelScene.createText( Babylon.get().getFromResources("rankingscene_total"), 8 );
				label.hardlight( DEFAULT_COLOR );
				label.measure();
				add( label );

				BitmapText won = PixelScene.createText( Integer.toString( Rankings.INSTANCE.wonNumber ), 8 );
				won.hardlight( Window.TITLE_COLOR );
				won.measure();
				add( won );

				BitmapText total = PixelScene.createText( "/" + Rankings.INSTANCE.totalNumber, 8 );
				total.hardlight( DEFAULT_COLOR );
				total.measure();
				total.x = align( (w - total.width()) / 2 );
				total.y = align( top + pos * rowHeight + GAP );
				add( total );

				float tw = label.width() + won.width() + total.width();
				label.x = align( (w - tw) / 2 );
				won.x = label.x + label.width();
				total.x = won.x + won.width();
				label.y = won.y = total.y = align( top + pos * rowHeight + GAP );
			}

		} else {

			BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingsscene_nogames"), 8 );
			title.hardlight( DEFAULT_COLOR );
			title.measure();
			title.x = align( (w - title.width()) / 2 );
			title.y = align( (h - title.height()) / 2 );
			add( title );

		}
	}

	private void showGlobalRanking() {
		final float rowHeight = PXL610.landscape() ? ROW_HEIGHT_L : ROW_HEIGHT_P;

		final float left = (w - Math.min( MAX_ROW_WIDTH, w )) / 2 + GAP;
		final float top = align( (h - rowHeight  * Rankings.INSTANCE.records.size()) / 2 );

		BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingscene_toprankings"), 9 );
		title.hardlight( Window.TITLE_COLOR );
		title.measure();
		title.x = align( (w - title.width()) / 2 );
		title.y = align( top - title.height() - GAP );
		add( title );

		switcher = new TextButton("<-") {
			@Override
			protected void onClick() {
				isLocal = !isLocal;
				PXL610.switchNoFade( RankingsScene.class );
			}
		};
		switcher.useText().hardlight(Window.TITLE_COLOR);
		switcher.setPos(align((w - title.width()) / 2 - switcher.useText().width() - GAP), align(top - title.height() - GAP));
		add(switcher);

		pos = 0;
		final Image busy= Icons.BUSY.get();

		OnlineRatinger.INSTANCE.get();
		OnlineRatinger.INSTANCE.setListener(new OnlineRatinger.OnLoadedListener() {
			@Override
			public void scream(List<Map<String, Object>> data) {
				remove( busy );
				Collections.sort(data, new Comparator<Map<String, Object>>() {
					@Override
					public int compare( Map<String, Object> lhs, Map<String, Object> rhs ) {
						return (int)Math.signum( ((Number) rhs.get(OnlineRatinger.SCORE)).intValue() - ((Number) lhs.get(OnlineRatinger.SCORE)).intValue() );
					}
				});
				for (int i = 0; i < data.size(); i++) {
					GlobalRecord row = new GlobalRecord( pos, data.get(i) );
					row.setRect( left, top + pos * rowHeight, w - left * 2, rowHeight );
					add( row );

					pos++;
				}
			}
		});

		busy.origin.set( busy.width / 2, busy.height / 2 );
		busy.angularSpeed = 720;
		busy.x = (w - busy.width) / 2;
		busy.y = (h - busy.height) / 2;
		add( busy );
	}

	@Override
	protected void onBackPressed() {
		PXL610.switchNoFade( TitleScene.class );
	}
	
	private static class LocalRecord extends Button {
		
		private static final float GAP	= 4;
		
		private static final int TEXT_WIN	= 0xFFFF88;
		private static final int TEXT_LOSE	= 0xCCCCCC;
		private static final int FLARE_WIN	= 0x888866;
		private static final int FLARE_LOSE	= 0x666666;
		
		private Rankings.Record rec;
		
		private ItemSprite shield;
		private Flare flare;
		private BitmapText position;
		private BitmapTextMultiline desc;
		private Image classIcon;

        private LocalRecord(int pos, boolean latest, Rankings.Record rec) {
			super();
			
			this.rec = rec;
			
			if (latest) {
				flare = new Flare( 6, 24 );
				flare.angularSpeed = 90;
				flare.color( rec.win ? FLARE_WIN : FLARE_LOSE );
				addToBack( flare );
			}
			
			position.text( Integer.toString( pos+1 ) );
			position.measure();
			
			desc.text( rec.info );
			desc.measure();
			
			if (rec.win) {
				shield.view( ItemSpriteSheet.AMULET, null );
				position.hardlight( TEXT_WIN );
				desc.hardlight( TEXT_WIN );
			} else {
				position.hardlight( TEXT_LOSE );
				desc.hardlight( TEXT_LOSE );
			}
			
			classIcon.copy( Icons.get( rec.heroClass ) );
		}
		
		@Override
		protected void createChildren() {
			
			super.createChildren();
			
			shield = new ItemSprite( ItemSpriteSheet.TOMB, null );
			add( shield );
			
			position = new BitmapText( PixelScene.font1x );
			add( position );
			
			desc = createMultiline( 9 );		
			add( desc );
			
			classIcon = new Image();
			add( classIcon );
		}
		
		@Override
		protected void layout() {
			
			super.layout();
			
			shield.x = x;
			shield.y = y + (height - shield.height) / 2;
			
			position.x = align( shield.x + (shield.width - position.width()) / 2 );
			position.y = align( shield.y + (shield.height - position.height()) / 2 + 1 );
			
			if (flare != null) {
				flare.point( shield.center() );
			}
			
			classIcon.x = align( x + width - classIcon.width );
			classIcon.y = shield.y;
			
			desc.x = shield.x + shield.width + GAP;
			desc.maxWidth = (int)(classIcon.x - desc.x);
			desc.measure();
			desc.y = position.y + position.baseLine() - desc.baseLine();
		}
		
		@Override
		protected void onClick() {
			if (rec.gameFile.length() > 0) {
				parent.add( new WndRanking( rec.gameFile ) );
			} else {
				parent.add( new WndError( Babylon.get().getFromResources("rankingsscene_noinfo") ) );
			}
		}
	}

    private static class GlobalRecord extends Button {

		private static final float GAP	= 4;

		private static final int TEXT_WIN	= 0xFFFF88;
		private static final int TEXT_LOSE	= 0xCCCCCC;
		private static final int FLARE_WIN	= 0x888866;
		private static final int FLARE_LOSE	= 0x666666;

		private Map<String, Object> rec;

		private ItemSprite shield;
		private Flare flare;
		private BitmapText position;
		private BitmapTextMultiline desc;
		private Image classIcon;

        private GlobalRecord( int pos, Map<String, Object> rec ) {
			super();

			this.rec = rec;

			if (((String) rec.get(OnlineRatinger.ID)).equals(PXL610.user_id())) {
				flare = new Flare( 6, 24 );
				flare.angularSpeed = 90;
				flare.color( ((boolean) rec.get(OnlineRatinger.WIN)) ? FLARE_WIN : FLARE_LOSE );
				addToBack( flare );
			}

			position.text( Integer.toString( pos+1 ) );
			position.measure();

			desc.text( (String) rec.get(OnlineRatinger.SENDER) + "\n" + rec.get(OnlineRatinger.SCORE) );
			desc.measure();

			if ((boolean) rec.get(OnlineRatinger.WIN)) {
				shield.view( ItemSpriteSheet.AMULET, null );
				position.hardlight( TEXT_WIN );
				desc.hardlight( TEXT_WIN );
			} else {
				position.hardlight( TEXT_LOSE );
				desc.hardlight( TEXT_LOSE );
			}

			classIcon.copy( Icons.get( OnlineRatinger.getClassById((String) rec.get(OnlineRatinger.CLASS))) );
		}

		@Override
		protected void createChildren() {

			super.createChildren();

			shield = new ItemSprite( ItemSpriteSheet.TOMB, null );
			add( shield );

			position = new BitmapText( PixelScene.font1x );
			add( position );

			desc = createMultiline( 9 );
			add( desc );

			classIcon = new Image();
			add( classIcon );
		}

		@Override
		protected void layout() {

			super.layout();

			shield.x = x + width / 6;
			shield.y = y + (height - shield.height) / 2;

			position.x = align( shield.x + (shield.width - position.width()) / 2 );
			position.y = align( shield.y + (shield.height - position.height()) / 2 + 1 );

			if (flare != null) {
				flare.point( shield.center() );
			}

			classIcon.x = align( x + width * 5 / 6 - classIcon.width );
			classIcon.y = shield.y;

			desc.x = shield.x + shield.width + GAP;
			desc.maxWidth = (int)(classIcon.x - desc.x);
			desc.measure();
			desc.y = position.y + position.baseLine() - desc.baseLine();
		}

		@Override
		protected void onClick() {
			parent.add( new WndOnlineRank( rec ) );
		}
	}
}

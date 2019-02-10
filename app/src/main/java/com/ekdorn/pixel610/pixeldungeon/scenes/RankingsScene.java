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

import android.os.Handler;
import android.os.Looper;

import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.audio.Music;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.internet.OnlineRatinger;
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
	private boolean loaded0, loaded1 = false;

	float rowHeight;
	float left;
	float top;
	
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

		rowHeight = PXL610.landscape() ? ROW_HEIGHT_L : ROW_HEIGHT_P;

        Rankings.INSTANCE.load();

		if (isLocal) {
			showLocalRanking();
		} else {
			final Image busy = Icons.BUSY.get();
			busy.origin.set( busy.width / 2, busy.height / 2 );
			busy.angularSpeed = 720;
			busy.x = (w - busy.width) / 2;
			busy.y = (h - busy.height) / 2;
			add( busy );
			Handler mainHandler = new Handler(Looper.getMainLooper());
			Runnable dialog = new Runnable() {
				@Override
				public void run() {
					OnlineRatinger.INSTANCE.get();
					OnlineRatinger.INSTANCE.setListener(new OnlineRatinger.OnLoadedListener() {
						@Override
						public void scream() {
							RankingsScene.this.remove(busy);
							loaded0 = true;
						}

                        @Override
                        public void cry() {
							loaded1 = true;
                        }
                    });
				}
			};
			mainHandler.post(dialog);
		}
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		fadeIn();
	}

	private void showLocalRanking() {
		if (Rankings.INSTANCE.records.size() > 0) {

			left = (w - Math.min( MAX_ROW_WIDTH, w )) / 2 + GAP;
			top = align( (h - rowHeight  * Rankings.INSTANCE.records.size()) / 2 );

			BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingscene_localrankings"), 9 );
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
				BitmapText label = PixelScene.createText( Babylon.get().getFromResources("rankingscene_total") + " ", 8 );
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
			isLocal = !isLocal;
			PXL610.switchNoFade( RankingsScene.class );
		}
	}

	private void showGlobalRanking() {
		List<Map<String, Object>> data = OnlineRatinger.INSTANCE.getTopData();

		if (data.size() > 0) {

			left = (w - (Math.min( MAX_ROW_WIDTH, w ) / 3 * 2)) / 2 + GAP;
			top = align( (h - rowHeight  * data.size()) / 2 );

			BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingscene_globalrankings"), 9 );
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
			if (Rankings.INSTANCE.records.size() > 0) add(switcher);

			pos = 0;

			Collections.sort(data, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
					return (int) Math.signum(((Number) rhs.get(OnlineRatinger.SCORE)).intValue() - ((Number) lhs.get(OnlineRatinger.SCORE)).intValue());
				}
			});

			for (int i = 0; i < data.size(); i++) {
				GlobalRecord row = new GlobalRecord(pos, data.get(i));
				row.setRect(left, top + pos * rowHeight, w - left * 2, rowHeight);
				RankingsScene.this.add(row);

				pos++;
			}

			BitmapText games = PixelScene.createText( Babylon.get().getFromResources("rankingscene_global") + " ", 8 );
			games.hardlight( DEFAULT_COLOR );
			games.measure();
			add( games );

			BitmapText won = PixelScene.createText( Long.toString( OnlineRatinger.INSTANCE.getCountGlobal() ), 8 );
			won.hardlight( Window.TITLE_COLOR );
			won.measure();
			add( won );


            BitmapText score = PixelScene.createText(Babylon.get().getFromResources("rankingscene_better") + " ", 8 );
            score.hardlight( DEFAULT_COLOR );
            score.measure();
            add( score );

            int highscore = Rankings.INSTANCE.records.size() > 0 ? Rankings.INSTANCE.records.get(0).score : 1;
            long percent = (Rankings.INSTANCE.records.size() > 0) ? ((highscore / (OnlineRatinger.INSTANCE.getBestGlobal() / 100)) - 100) : (0);
            BitmapText perc = PixelScene.createText( Long.toString( percent ) + "%", 8 );
            perc.hardlight( Window.TITLE_COLOR );
            perc.measure();
            add( perc );


			float tw0 = games.width() + won.width();
			games.x = align( (w - tw0) / 2 );
			won.x = games.x + games.width();
			games.y = won.y = align( top + pos * rowHeight + GAP );

            float tw1 = score.width() + perc.width();
            score.x = align( (w - tw1) / 2 );
            perc.x = score.x + score.width();
			score.y = perc.y = align( games.y + GAP + games.height() );

		} else {
			BitmapText title = PixelScene.createText( Babylon.get().getFromResources("rankingsscene_nogames"), 8 );
			title.hardlight( DEFAULT_COLOR );
			title.measure();
			title.x = align( (w - title.width()) / 2 );
			title.y = align( (h - title.height()) / 2 );
			add( title );
		}
	}

	@Override
	public void update() {
		super.update();
		if (loaded0 && loaded1) {
			loaded0 = loaded1 = false;
			showGlobalRanking();
		}
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

			if (((String) rec.get(OnlineRatinger.ID)).equals(PXL610.user_name())) {
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

			classIcon.copy( Icons.get( HeroClass.valueOf(((String) rec.get(OnlineRatinger.CLASS)))) );
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
			parent.add( new WndOnlineRank( rec ) );
		}
	}
}

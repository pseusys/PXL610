package com.ekdorn.pixel610.pixeldungeon.scenes;

import android.util.Log;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.Group;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.particles.BitmaskEmitter;
import com.ekdorn.pixel610.noosa.particles.Emitter;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.ui.Archs;
import com.ekdorn.pixel610.pixeldungeon.ui.ExitButton;
import com.ekdorn.pixel610.pixeldungeon.ui.RedButton;
import com.ekdorn.pixel610.utils.Callback;

import java.util.HashMap;

public class ModeScene extends PixelScene {

    private static final float BUTTON_HEIGHT	= 24;
    private static final float GAP				= 2;

    private static final float WIDTH_P	= 150;
    private static final float HEIGHT_P	= 220;

    private static final float WIDTH_L	= 224;
    private static final float HEIGHT_L	= 152;

    private static HashMap<String, ModeShield> shields = new HashMap<String, ModeShield>();
    private static String curName;

    private float buttonX;
    private float buttonY;

    private GameButton btnNewGame;
    private RedButton btnBack;
    private RedButton btnForth;

    private Group unlock;

    @Override
    public void create() {

        super.create();

        Badges.loadGlobal();

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;

        float width, height;
        if (PXL610.landscape()) {
            width = WIDTH_L;
            height = HEIGHT_L;
        } else {
            width = WIDTH_P;//
            height = HEIGHT_P;
        }

        float left = (w - width) / 2;
        float top = (h - height) / 2;
        float bottom = h - top;

        Archs archs = new Archs();
        archs.setSize( w, h );
        add( archs );

        buttonX = left + 16;
        buttonY = bottom - BUTTON_HEIGHT;

        btnNewGame = new GameButton( Babylon.get().getFromResources("startscene_new") ) {
            @Override
            protected void onClick() {
                PXL610.switchNoFade( StartScene.class );
            }
        };
        add( btnNewGame );

        btnBack = new RedButton( "<" ) {
            @Override
            protected void onClick() {
            }
        };
        add( btnBack );
        btnForth = new RedButton( ">" ) {
            @Override
            protected void onClick() {
            }
        };
        add( btnForth );

        String[] modes = {
                "Original", "DLC1"
        };

        ModeShield shielder = new ModeShield( modes[0] );
        shields.put( modes[0], shielder );
        add( shielder );

        if (PXL610.landscape()) {
            float shieldW = width/2;
            ModeShield shield = shields.get( modes[0] );
            shield.setRect( left + (width - shieldW)/2, (buttonY + top - shieldW)/2, shieldW, shieldW );
            btnBack.setRect(left, (buttonY + top - shieldW/3)/2, width/15, shieldW/3);
            btnForth.setRect(w - left - width/15, (buttonY + top - shieldW/3)/2, width/15, shieldW/3);

        } else {
            float shieldW = width - width/5 - 10;
            ModeShield shield = shields.get( modes[0] );
            shield.setRect(left + 5 + width/10, (h - shieldW)/2, shieldW, shieldW );
            Log.e("TAG", "create: " + left );
            btnBack.setRect(left + 5, (h - shieldW/3)/2, width/10, shieldW/3);
            btnForth.setRect(w - left - 5 - width/10, (h - shieldW/3)/2, width/10, shieldW/3);
        }

        unlock = new Group();
        add( unlock );

        if (!(/*huntressUnlocked = Badges.isUnlocked( Badges.Badge.BOSS_SLAIN_3 )*/false)) {

            BitmapTextMultiline text = PixelScene.createMultiline( Babylon.get().getFromResources("startscene_unlock"), 9 );
            text.maxWidth = (int)width;
            text.measure();

            float pos = (bottom - BUTTON_HEIGHT) + (BUTTON_HEIGHT - text.height()) / 2;
            for (BitmapText line : text.new LineSplitter().split()) {
                line.measure();
                line.hardlight( 0xFFFF00 );
                line.x = PixelScene.align( w / 2 - line.width() / 2 );
                line.y = PixelScene.align( pos );
                unlock.add( line );

                pos += line.height();
            }
        }

        ExitButton btnExit = new ExitButton();
        btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
        add( btnExit );

        curName = null;
        updateClass( modes[0] );
        fadeIn();

        Badges.loadingListener = new Callback() {
            @Override
            public void call() {
                if (Game.scene() == ModeScene.this) {
                    PXL610.switchNoFade( ModeScene.class );
                }
            }
        };
    }

    @Override
    public void destroy() {

        Badges.saveGlobal();
        Badges.loadingListener = null;

        super.destroy();
    }

    private void updateClass( String name ) {

        if ((curName != null) && (curName.equals(name))) {
            //add( new WndClass( cl ) );
            return;
        }

        if (curName != null) {
            shields.get( curName ).highlight( false );
        }
        shields.get( curName = name ).highlight( true );

        if (name != "DLC1") {

            unlock.visible = false;

            btnNewGame.visible = true;
            btnNewGame.secondary( null, false );
            btnNewGame.setRect( buttonX, buttonY, Camera.main.width - buttonX * 2, BUTTON_HEIGHT );

        } else {

            unlock.visible = true;
            btnNewGame.visible = false;

        }
    }

    private void startNewGame() {

        Dungeon.hero = null;
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND;

        if (PXL610.gameIntro()) {
            PXL610.gameIntro( false );
            Game.switchScene( IntroScene.class );
        } else {
            Game.switchScene( InterlevelScene.class );
        }
    }

    @Override
    protected void onBackPressed() {
        PXL610.switchNoFade( TitleScene.class );
    }

    public static class GameButton extends RedButton {

        private static final int SECONDARY_COLOR_N	= 0xCACFC2;
        private static final int SECONDARY_COLOR_H	= 0xFFFF88;

        private BitmapText secondary;

        public GameButton( String primary ) {
            super( primary );

            this.secondary.text( null );
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            secondary = createText( 6 );
            add( secondary );
        }

        @Override
        protected void layout() {
            super.layout();

            if (secondary.text().length() > 0) {
                text.y = align( y + (height - text.height() - secondary.baseLine()) / 2 );

                secondary.x = align( x + (width - secondary.width()) / 2 );
                secondary.y = align( text.y + text.height() );
            } else {
                text.y = align( y + (height - text.baseLine()) / 2 );
            }
        }

        public void secondary( String text, boolean highlighted ) {
            secondary.text( text );
            secondary.measure();

            secondary.hardlight( highlighted ? SECONDARY_COLOR_H : SECONDARY_COLOR_N );
        }
    }

    private class ModeShield extends Button {

        private static final float MIN_BRIGHTNESS	= 0.6f;

        private static final int BASIC_NORMAL		= 0x444444;
        private static final int BASIC_HIGHLIGHTED	= 0xCACFC2;

        private static final int MASTERY_NORMAL		= 0x666644;
        private static final int MASTERY_HIGHLIGHTED= 0xFFFF88;

        private static final int WIDTH	= 32;
        private static final int HEIGHT	= 32;
        private static final int SCALE	= 3;

        private String modeName;

        private Image avatar;
        private BitmapText name;
        private Emitter emitter;

        private float brightness;

        private int normal;
        private int highlighted;

        public ModeShield( String mode ) {
            super();

            this.modeName = mode;

            avatar.frame( 0, 0, WIDTH, HEIGHT );
            avatar.scale.set( SCALE );

            if (true) {
                normal = MASTERY_NORMAL;
                highlighted = MASTERY_HIGHLIGHTED;
            } else {
                normal = BASIC_NORMAL;
                highlighted = BASIC_HIGHLIGHTED;
            }

            name.text( modeName );
            name.measure();
            name.hardlight( normal );

            brightness = MIN_BRIGHTNESS;
            updateBrightness();
        }

        @Override
        protected void createChildren() {

            super.createChildren();

            avatar = new Image( Assets.MODES );
            add( avatar );

            name = PixelScene.createText( 9 );
            add( name );

            emitter = new BitmaskEmitter( avatar );
            add( emitter );
        }

        @Override
        protected void layout() {

            super.layout();

            avatar.x = align( x + (width - avatar.width()) / 2 );
            avatar.y = align( y + (height - avatar.height() - name.height()) / 2 );

            name.x = align( x + (width - name.width()) / 2 );
            name.y = avatar.y + avatar.height() + SCALE;
        }

        @Override
        protected void onTouchDown() {

            emitter.revive();
            emitter.start( Speck.factory( Speck.LIGHT ), 0.05f, 7 );

            Sample.INSTANCE.play( Assets.SND_CLICK, 1, 1, 1.2f );
            updateClass( modeName );
        }

        @Override
        public void update() {
            super.update();

            if (brightness < 1.0f && brightness > MIN_BRIGHTNESS) {
                if ((brightness -= Game.elapsed) <= MIN_BRIGHTNESS) {
                    brightness = MIN_BRIGHTNESS;
                }
                updateBrightness();
            }
        }

        public void highlight( boolean value ) {
            if (value) {
                brightness = 1.0f;
                name.hardlight( highlighted );
            } else {
                brightness = 0.999f;
                name.hardlight( normal );
            }

            updateBrightness();
        }

        private void updateBrightness() {
            avatar.gm = avatar.bm = avatar.rm = avatar.am = brightness;
        }
    }
}


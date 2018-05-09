package com.watabou.pixeldungeon.windows;

import android.util.Log;

import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Babylon;
import com.watabou.pixeldungeon.Chrome;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith;
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.ui.ItemSlot;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;

public class WndGhost extends Window {

    private static final int BTN_SIZE	= 36;
    private static final float GAP		= 2;
    private static final float BTN_GAP	= 10;
    private static final int WIDTH		= 116;

    private ItemButton btnItem1;
    private ItemButton btnItem2;

    private Item questItem;
    private Ghost ghost;

    public WndGhost( Ghost ghost, String text, Item armor, Item weapon, Item detachItem ) {

        super();

        this.ghost = ghost;
        this.questItem = detachItem;

        IconTitle titlebar = new IconTitle( ghost.sprite(), Utils.capitalize( ghost.name ) );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        BitmapTextMultiline message = PixelScene.createMultiline( text, 6 );
        message.maxWidth = WIDTH;
        message.measure();
        message.y = titlebar.bottom() + GAP;
        add( message );

        btnItem1 = new ItemButton(armor) {
            @Override
            protected void onClick() {
                /*btnPressed = btnItem1;
                GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, Babylon.get().getFromResources("wnd_blacksmith_select") );*/
                click(item);
            }
        };
        btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.y + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
        add( btnItem1 );

        btnItem2 = new ItemButton(weapon) {
            @Override
            protected void onClick() {
                /*btnPressed = btnItem2;
                GameScene.selectItem( itemSelector, WndBag.Mode.UPGRADEABLE, Babylon.get().getFromResources("wnd_blacksmith_select") );*/
                click(item);
            }
        };
        btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
        add( btnItem2 );


        resize( WIDTH, (int)btnItem2.bottom() );
    }

    private void click(Item reward) {
        if (WndGhost.this.questItem != null) {
            WndGhost.this.questItem.detach( Dungeon.hero.belongings.backpack );
        }

        if (reward.doPickUp( Dungeon.hero )) {
            GLog.i( Babylon.get().getFromResources("hero_you_have"), reward.name() );
        } else {
            Dungeon.level.drop( reward, ghost.pos ).sprite.drop();
        }

        ghost.yell(Babylon.get().getFromResources("wnd_ghost_farewell"));
        ghost.die( null );

        Ghost.Quest.complete();
    }

    /*protected WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                btnPressed.item( item );

                if (btnItem1.item != null && btnItem2.item != null) {
                    String result = Blacksmith.verify( btnItem1.item, btnItem2.item );
                    if (result != null) {
                        GameScene.show( new WndMessage( result ) );
                        btnReforge.enable( false );
                    } else {
                        btnReforge.enable( true );
                    }
                }
            }
        }
    };*/

    public static class ItemButton extends Component {

        protected NinePatch bg;
        protected ItemSlot slot;

        protected Item item;

        public ItemButton(Item item) {
            this.item = item;
            slot.item( this.item );
            Log.e("TAG", "itemButton: " + this.item );
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            bg = Chrome.get( Chrome.Type.BUTTON );
            add( bg );

            slot = new ItemSlot() {
                @Override
                protected void onTouchDown() {
                    bg.brightness( 1.2f );
                    Sample.INSTANCE.play( Assets.SND_CLICK );
                };
                @Override
                protected void onTouchUp() {
                    bg.resetColor();
                }
                @Override
                protected void onClick() {
                    ItemButton.this.onClick();
                }
            };
            //slot.item( this.item );
            //Log.e("TAG", "createChildren: " + this.item );
            add( slot );
        }

        protected void onClick() {};

        @Override
        protected void layout() {
            super.layout();

            bg.x = x;
            bg.y = y;
            bg.size( width, height );

            slot.setRect( x + 2, y + 2, width - 4, height - 4 );
        };
    }
}

package com.ekdorn.pixel610.pixeldungeon.items;

import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.pixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class Pen3D extends Item {

    @Override
    public void finish() {
        name = Babylon.get().getFromResources("item_3dpen");
        image = ItemSpriteSheet.PEN3D;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( Babylon.get().getFromResources("item_3dpen_duplicate") );
        return actions;
    }

    @Override
    public void execute( final Hero hero, String action ) {
        if (action.equals( Babylon.get().getFromResources("item_3dpen_duplicate") )) {

            curUser = hero;
            GameScene.selectItem( itemSelector, WndBag.Mode.ALL, Babylon.get().getFromResources("item_3dpen_select") );

        } else {
            super.execute( hero, action );
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public String info() {
        return Babylon.get().getFromResources("item_3dpen_desc");
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                Item duplicate = null;
                try {
                    duplicate = item.getClass().newInstance();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                if (duplicate != null) {
                    duplicate.collect();
                }
            }
        }
    };
}

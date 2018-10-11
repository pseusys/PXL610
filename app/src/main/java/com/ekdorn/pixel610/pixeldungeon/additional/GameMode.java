package com.ekdorn.pixel610.pixeldungeon.additional;

import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.PXL610;

public class GameMode {

    public static final String original = "original";
    public static final String dlc1 = "dlc1";

    public String title;
    public String tag;
    public OutLook outlook;

    public GameMode(String tag) {
        this.tag = tag;
    }

    // etwas:

    // INITIATERS:

    private static GameMode initOriginal(String tag) {
        GameMode initOriginal = new GameMode(tag);

        initOriginal.tag = original;
        initOriginal.title = "Оригинал";

        initOriginal.outlook = new OutLook(25, 13, 0.75F,
                Assets.ARCS_BG_0, Assets.ARCS_FG_0,
                0, 0, 1, 2,
                Assets.CHROME);

        return initOriginal;
    }

    private static GameMode initDLC1(String tag) {
        GameMode initDLC1 = new GameMode(tag);

        initDLC1.tag = dlc1;
        initDLC1.title = "ДЛЦ 1";

        initDLC1.outlook = new OutLook(16, 29, 0.5F,
                Assets.ARCS_BG_1, Assets.ARCS_FG_1,
                1, 2, 0, 0,
                Assets.CHROME);

        return initDLC1;
    }

    public static GameMode init(String tag) {
        PXL610.gamemode(tag);
        switch (tag) {
            case original:
                return initOriginal(tag);

            case dlc1:
                return initDLC1(tag);

            default:
                return initOriginal(tag);
        }
    }

    public static class OutLook {
        public int torchX;
        public int torchY;
        public float torchInt;

        public String archs0Asset;
        public int archs0Xmult, archs0Ymult;
        public String archs1Asset;
        public int archs1Xmult, archs1Ymult;

        public String uiAsset;

        public OutLook(int torchX, int torchY, float torchInt,
                       String archs0Asset, String archs1Asset,
                       int x0, int x1, int y0, int y1,
                       String uiAsset) {

            this.torchX = torchX;
            this.torchY = torchY;
            this.torchInt = torchInt;

            this.archs0Asset = archs0Asset;
            this.archs0Xmult = x0;
            this.archs0Ymult = y0;
            this.archs1Xmult = x1;
            this.archs1Ymult = y1;
            this.archs1Asset = archs1Asset;

            this.uiAsset = uiAsset;
        }
    }
}

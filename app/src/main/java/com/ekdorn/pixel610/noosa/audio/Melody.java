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

package com.ekdorn.pixel610.noosa.audio;

import com.ekdorn.pixel610.classicdungeon.Assets;
import com.ekdorn.pixel610.classicdungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Random;

public class Melody { // For future music packs;

    protected static String getRandomNameForPack(String packName) {
        return Utils.format(packName, Random.NormalIntRange(0, Assets.FIRST_PACK_COUNT));
    }

    protected static int getRandomNameForPackExceptioned(int last, int prelast) {
        int nova;
        nova = Random.NormalIntRange(0, Assets.FIRST_PACK_COUNT);
        while ((nova == last) || (nova == prelast)) {
            nova = Random.NormalIntRange(0, Assets.FIRST_PACK_COUNT);
        }
        return nova;
    }
}

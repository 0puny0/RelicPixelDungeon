package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Confusion extends FlavourBuff{
    public static final float DURATION	= 1f;

    @Override
    public int icon() {
        return BuffIndicator.CONFUSION;
    }

    @Override
    public void tintIcon(Image icon) {icon.hardlight(1f, 1f, 1f);}
}

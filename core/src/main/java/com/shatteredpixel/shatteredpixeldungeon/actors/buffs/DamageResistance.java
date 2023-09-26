package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class DamageResistance extends FlavourBuff{
    {
        type = buffType.POSITIVE;
    }
    public static final float DURATION = 1f;
    public int minResistance=0;
    public int maxResistance=0;

    public void set( int min,int max ) {
        minResistance=min;
        maxResistance=max;
    }
    public int randomResistance(){
        return Random.NormalIntRange(minResistance,maxResistance);
    }
}

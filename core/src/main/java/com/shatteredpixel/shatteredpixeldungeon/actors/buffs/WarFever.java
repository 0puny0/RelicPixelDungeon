package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class WarFever extends FlavourBuff{
    {
        type = buffType.POSITIVE;
        announced = true;
    }
    public int level=0;
    public static final float DURATION	= 4.5f;
    @Override
    public int icon() {
        return BuffIndicator.AMOK;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }
    @Override
    public String desc() {
        return Messages.get(this, "desc",level,dispTurns());
    }
    public  void addLevel(){
        if(level>= (Dungeon.hero.pointsInTalent(Talent.WAR_FEVER)>=3?3:2)){
            return;
        }
            level++;
    }

    @Override
    public void detach() {
        super.detach();
        updateQuickslot();
    }

    private static final String LEVEL = "level";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL );
    }
}

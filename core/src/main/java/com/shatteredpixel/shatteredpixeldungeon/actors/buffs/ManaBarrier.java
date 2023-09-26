package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class ManaBarrier extends ShieldBuff{


    private int countdown;
    private  boolean countStop=false;
    @Override
    public synchronized boolean act() {
        if (target.buff(MagicImmune.class) != null){
            spend(TICK);
            return true;
        }
        countdown++;
        int maxShield=2+((Hero)target).lvl/2;
        if(((Hero) target).hasTalent(Talent.COAGULATION_SHIELD)){
            maxShield+= Math.floor(maxShield* (0.05f + 0.1f*((Hero)target).pointsInTalent(Talent.COAGULATION_SHIELD)));
        }
        if(countStop&&shielding()<maxShield){
            countStop=false;
            countdown=1;
        }
        if(countdown >=CD()&&!countStop){
            fillBarrier();
        }

        spend(TICK);
        return true;
    }
    public void fillBarrier(){
        setShield(2+((Hero)target).lvl/2);
        countStop=true;
    }

    @Override
    //logic edited slightly as buff should not detach
    public int absorbDamage(int dmg) {
        if (shielding() <= 0) return dmg;

        if (shielding() >= dmg){
            decShield(dmg);
            dmg = 0;
        } else {
            dmg -= shielding();
            decShield(shielding());
        }
        return dmg;
    }
    public int CD(){
        int initial=15;
        Armor a=Dungeon.hero.belongings.armor();
        if(a!=null&&a.STRReq()>10){
            return (int) Math.ceil(initial*(1+0.2f*(a.STRReq()-10)));
        }else {
            return initial;
        }

    }
    @Override
    public int icon() {
        return BuffIndicator.MANABARRIER;
    }

    @Override
    public float iconFadePercent() {
        return countStop?0:Math.max(0, 1-countdown*1f/ CD());
    }
    @Override
    public String desc() {
        return Messages.get(this, "desc",CD()-countdown>0?CD()-countdown:CD());
    }
    private static final String COUNTDOWN = "countdown";
    private static final String COUNTSTOP = "countstop";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( COUNTDOWN, countdown);
        bundle.put( COUNTSTOP, countStop);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        countdown = bundle.getInt( COUNTDOWN );
        countStop = bundle.getBoolean( COUNTSTOP );
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class KnightLance extends MeleeWeapon{
    {
        image = ItemSpriteSheet.KNIGHT_LANCE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=0.8f;
        DMG=0.75f;
        RCH=2;

    }

    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",buffedLvl()+2);
        }else {
            return  Messages.get(this, "typical_stats_desc",2);
        }

    }
    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (hero.buff(Impetus.class)!=null){
            hero.buff(Impetus.class).detach();
        }
        return super.doUnequip(hero, collect, single);
    }

    @Override
    public int damageRoll(Char owner) {

        int damage ;
        if(owner.buff(Impetus.class)!=null){
            int diff = max() - min();
            damage=Random.NormalIntRange(min()+ Math.round(diff*0.25f*owner.buff(Impetus.class).level),
                    max());
        }else {
            damage = Random.NormalIntRange(min() , max());
        }
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            if (owner instanceof Hero) {
                int exStr = ((Hero)owner).STR() - STRReq();
                if (exStr > 0) {
                    damage +=   (int)(exStr * RingOfForce.extraStrengthBonus(hero ));
                }
            }
            return damage;
        } else {
            return damage;
        }

    }
    @Override
    public float accuracyFactor(Char owner, Char target) {
        float accuracy=super.accuracyFactor(owner, target);
        if(owner.buff(KnightLance.Impetus.class)!=null){
            accuracy*=owner.buff(KnightLance.Impetus.class).level*1.5f;
        }
        return accuracy;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero&&attacker.buff(Impetus.class)!=null){
            attacker.buff(Impetus.class).detach();
        }
        return super.proc(attacker, defender, damage);
    }

    public static class Impetus extends FlavourBuff {

        public int level = 0;
        private int MaxLevel=3;


        public void raise() {

            if (level++ >= MaxLevel) {
                level = MaxLevel;
            }
        }

        @Override
        public void detach() {
            level=0;
            super.detach();
        }
        @Override
        public int icon() {
            return BuffIndicator.IMPETUS;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 0.5f, 2f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, 1-1f*level / MaxLevel);
        }
        private static final String LEVEL	    = "level";
        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( LEVEL, level );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            level = bundle.getInt( LEVEL );
        }

    }

}

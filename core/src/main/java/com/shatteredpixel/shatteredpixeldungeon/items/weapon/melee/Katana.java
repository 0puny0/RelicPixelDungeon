package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Katana extends MeleeWeapon{
    {
        image = ItemSpriteSheet.KATANA;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch=0.95f;
        DMG=1.4f;
        ACC=0.75f;

    }

    @Override
    public int damageRoll(Char owner) {
        int diff = max() - min();
        int damage ;
        if( owner.buff(Weakness.class)==null){
            damage =Random.NormalIntRange(min()+ Math.round(diff*0.15f), max());
        }else {
            damage =Random.NormalIntRange(min(), max()-Math.round(diff*0.15f));
        }
        if(owner instanceof Hero){
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage +=   (int)(exStr * RingOfForce.extraStrengthBonus(owner));
            }
        }
        return damage;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.prolong(attacker, Weakness.class,4);
        return super.proc(attacker, defender, damage);
    }
}

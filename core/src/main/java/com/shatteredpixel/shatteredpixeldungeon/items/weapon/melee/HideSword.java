package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HideSword extends MeleeWeapon{
    {
        image = ItemSpriteSheet.HIDE_SWORD;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        ACC=1.5f;
        defaultAction = AC_MARK;
        usesTargeting = true;
    }

    public static final String AC_MARK	= "MARK";

    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) {
            Buff.prolong( defender, Vulnerable.class,3+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2) ;
            Buff.prolong(defender, Cripple.class,2*(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
        }
        return super.proc(attacker, defender, damage);
    }
    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",3+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2,0+2*(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
        }else {
            return  Messages.get(this, "typical_stats_desc",3,0);
        }

    }
}

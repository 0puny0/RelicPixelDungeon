package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DamageResistance;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class MagicShield extends MeleeWeapon{

    {
        image = ItemSpriteSheet.MAGIC_SHIELD;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        hasSkill=true;
        defaultAction=AC_WEAPONSKILL;
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_WEAPONSKILL)) {
            if (!isEquipped(hero)){
                GLog.i( Messages.get(MeleeWeapon.class, "need_to_equip") );
                return;
            }
            if (hero.buff(MagicImmune.class)!=null){
                GLog.i( Messages.get(MeleeWeapon.class, "magic_immune") );
                return;
            }
            onGUARD(hero);
        }
    }
    protected  void onGUARD(Hero hero ){
        Buff.affect(hero, DamageResistance.class,1f).set(2+2*buffedLvl(),4+4*buffedLvl());
        Buff.affect(hero, BlobImmunity.class,0.0f);
        hero.spendAndNext( 1f );
    }

    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",2+2*buffedLvl());
        }else {
            return  Messages.get(this, "typical_stats_desc",2);
        }

    }
    @Override
    public int defenseFactor( Char owner ) {
        return 2+2*buffedLvl();
    }
}

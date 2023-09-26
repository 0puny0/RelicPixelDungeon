package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RedtasselledSpear extends MeleeWeapon{
    {
        image = ItemSpriteSheet.REDTASSELLED_SPEAR;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=0.9f;
        DMG=0.75f;
        RCH=2;
    }
    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",3+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
        }else {
            return  Messages.get(this, "typical_stats_desc",3);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if(!Dungeon.level.adjacent(attacker.pos, defender.pos)){
            Buff.affect( defender, Hex.class ,3+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
        }
        return super.proc(attacker, defender, damage);
    }
}

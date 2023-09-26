package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TekkoKagi extends MeleeWeapon{
    {
        image = ItemSpriteSheet.TEKKO_KAGI;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch=1.3f;
        DMG=0.5f;
        DLY=0.5f;
        ACC=0.75f;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) {
            Buff.affect( defender, Bleeding.class ).set( Math.round(damage) *0.75f);
            }
        return super.proc(attacker, defender, damage);
    }
}

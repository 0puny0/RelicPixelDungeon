package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class HolyShield extends MeleeWeapon{
    {
        image = ItemSpriteSheet.HOLY_SHIELD;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        bones = false;
    }

    @Override
    public int defenseFactor( Char owner ) {
        return 1+1*buffedLvl();
    }
}

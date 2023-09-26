package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class PiercingBlade extends MeleeWeapon {
    {
        image = ItemSpriteSheet.PIERCING_BLADE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        ACC=1f;

    }
}

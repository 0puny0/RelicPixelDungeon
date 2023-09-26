package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class Rapier extends MeleeWeapon{
    {
        image = ItemSpriteSheet.RAPIER;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=1.15f;
        DMG=0.75f;
        DLY=0.5f;
        ACC=0.5f;

    }
}

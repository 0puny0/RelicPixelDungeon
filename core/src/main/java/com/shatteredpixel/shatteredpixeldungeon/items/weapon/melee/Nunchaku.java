package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class Nunchaku extends MeleeWeapon{
    {
        image = ItemSpriteSheet.NUNCHAKU;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch=1.3f;
        DMG=0.25f;
        DLY=0.5f;
        ACC=0.5f;
    }

}

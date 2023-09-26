package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ApothecaryKnife extends MeleeWeapon {

    {
        image = ItemSpriteSheet.APOTHECARY_KNIFE;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.9f;
        DMG=0.75f;
        DLY=0.75f;
        ACC=1.5f;
        bones = false;


    }
}
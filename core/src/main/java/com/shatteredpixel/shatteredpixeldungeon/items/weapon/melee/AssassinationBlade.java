package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class AssassinationBlade extends MeleeWeapon{
    {
        image = ItemSpriteSheet.ASSASSINATION_BLADE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1f;
        DMG = 0.75f;
    }
    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                int diff = max() - min();
                int damage = Random.NormalIntRange(min()+ Math.round(diff*0.55f),
                        max());
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage +=   (int)(exStr * RingOfForce.extraStrengthBonus(hero ));
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

}

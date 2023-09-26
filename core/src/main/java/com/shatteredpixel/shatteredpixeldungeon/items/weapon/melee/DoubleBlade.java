package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class DoubleBlade extends MeleeWeapon{
    {
        image = ItemSpriteSheet.DOUBLE_BLADE;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch=1.2f;
        DMG=0.5f;
        DLY=0.5f;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if(form==Form.FORM2){
            Buff.affect( defender, Poison.class ).set(super.min(buffedLvl()) );
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int min(int lvl) {
       int min= super.min(lvl);
       if(form==Form.FORM2){
           min=1;
       }
       return min;
    }
}

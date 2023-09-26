package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class CursedSword extends MeleeWeapon {
    {
        image = ItemSpriteSheet.CURSED_SWORD_;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;
        DMG = 1.4f;
        isBonus=true;
    }
    public boolean isBonus;
    public String nowDesc="desc";
    protected int elseImage = ItemSpriteSheet.CURSED_SWORD;
    protected float elseDMG = 0.75f;
    protected String elseDesc="typical_desc";
    @Override
    public void initialize() {
        getCurse(true);
        super.initialize();
    }

    @Override
    public Weapon enchant( Enchantment ench) {
        if(!isBonus&&ench!=null&&ench.curse()){
            modeSwitch();
        }
        if (isBonus&&(ench == null || !ench.curse())){
            modeSwitch();
        }
        return super.enchant( ench);
    }

    @Override
    public boolean removeCurse(boolean extraEffect) {
        if(isBonus&&!hasCurseEnchant()){
            modeSwitch();
        }
        return super.removeCurse(extraEffect);
    }

    public void modeSwitch() {
        int Image0 = image;
        float DMG0 = DMG;
        String Desc0=nowDesc;
        image = elseImage;
        DMG = elseDMG;
        nowDesc=elseDesc;
        elseImage = Image0;
        elseDMG = DMG0;
        elseDesc=Desc0;
        isBonus=!isBonus;
    }
    public String desc() {
            return Messages.get(this,nowDesc);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        if(hasCurseEnchant()!=isBonus){
            modeSwitch();
        }
    }
}

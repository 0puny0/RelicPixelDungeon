package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SpiritualCane extends MeleeWeapon{
    {
        image = ItemSpriteSheet.SPIRITUAL_CANE;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        ACC=0.75f;

    }

    @Override
    public boolean doEquip(Hero hero) {
        Buff.affect(hero,SpiritualShield.class).setCane(this);
        return super.doEquip(hero);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (hero.buff(SpiritualShield.class)!=null){
            hero.buff(SpiritualShield.class).detach();
        }
        return super.doUnequip(hero, collect, single);
    }


    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",String.valueOf((1+buffedLvl())*0.5),3+buffedLvl()*3);
        }else {
            return  Messages.get(this, "typical_stats_desc",String.valueOf(1),3);
        }
    }
    public static class SpiritualShield extends ShieldBuff {

        private SpiritualCane cane;
        private float partialShield;

        @Override
        public synchronized boolean act() {
            if(cane==null&&Dungeon.hero.belongings.weapon instanceof SpiritualCane){
                setCane((SpiritualCane)Dungeon.hero.belongings.weapon );
            }
            if (shielding() < maxShield()) {
                partialShield +=(1+cane.buffedLvl())*0.5;
            }

            while (partialShield >= 1){
                incShield();
                partialShield--;
            }

            if (shielding() <= 0 && maxShield() <= 0){
                detach();
            }

            spend(TICK);
            return true;
        }

        public synchronized void setCane(SpiritualCane cane){
            this.cane = cane;
        }

        public synchronized int maxShield() {

            if (cane != null && cane.isEquipped((Hero)target) ) {
                return 3+cane.buffedLvl()*3;
            } else {
                return 0;
            }
        }

        @Override
        //logic edited slightly as buff should not detach
        public int absorbDamage(int dmg) {
            if (shielding() <= 0) return dmg;

            if (shielding() >= dmg){
                decShield(dmg);
                dmg = 0;
            } else {
                dmg -= shielding();
                decShield(shielding());
            }
            return dmg;
        }

    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class DriveWhip extends MeleeWeapon{
    {
        image = ItemSpriteSheet.DRIVE_WHIP;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch=0.9f;
        DMG=0.75f;
        RCH=3;
        hasSkill=true;
        defaultAction=AC_WEAPONSKILL;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_WEAPONSKILL)) {

            if (!isEquipped(hero)){
                GLog.i( Messages.get(MeleeWeapon.class, "need_to_equip") );
                return;
            }
            GameScene.selectCell( shooter );

        }
    }

    @Override
    public String statsInfo() {
        if (isIdentified()){
            return  Messages.get(this, "stats_desc",2+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
        }else {
            return  Messages.get(this, "typical_stats_desc",2);
        }

    }

    public int targetingPos(Hero user, int dst) {
        return knockArrow().targetingPos(user, dst);
    }

    public DriveWhip.DriveHit knockArrow(){
        return new DriveWhip.DriveHit();
    }
    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {

            if (target != null) {
                /*
                Mob m=getEligibleMobs();
                if(m!=null){
                    QuickSlotButton.target(m);
                }
                 */
                if(target == curUser.pos||Dungeon.level.distance(curUser.pos,target)>DriveWhip.this.RCH||Actor.findChar( target )==null){
                    GLog.i( Messages.get(DriveWhip.class, "target_error") );
                    return;
                }
                knockArrow().cast(curUser, target);
            }
        }
        @Override
        public String prompt() {
            return Messages.get(MeleeWeapon.class, "prompt");
        }
    };
    public class DriveHit extends MissileWeapon {

        {
            image=ItemSpriteSheet.NULL;
        }

        @Override
        public int damageRoll(Char owner) {
            return DriveWhip.this.min();
        }


        public int throwPos(Hero user, int dst) {
            if (Dungeon.level.distance(user.pos, dst) <= DriveWhip.this.RCH){
                return dst;
            } else {
                return super.throwPos(user, dst);
            }
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar( cell );
            curUser.shoot( enemy, this );
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return DriveWhip.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            Buff.affect(defender, Adrenaline.class,2+0.5f*(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);
            return DriveWhip.this.proc(attacker, defender, damage);
        }

        @Override
        public float delayFactor(Char user) {
            return DriveWhip.this.delayFactor(user);
        }

        @Override
        public int STRReq(int lvl) {
            return DriveWhip.this.STRReq(lvl);
        }

    }

}

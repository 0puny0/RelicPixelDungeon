package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RuneSword extends MeleeWeapon{
    {
        image = ItemSpriteSheet.RUNE_SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch=1f;
        DMG=0.75f;
        ACC=0.75f;
        usesTargeting = true;
        hasSkill=true;
        defaultAction=AC_WEAPONSKILL;
    }

    private SwordQi swordQi;
    public RuneSword() {
        swordQi = null;
    }
    public static final String AC_FILL		="FILL";
    @Override
    public void initialize() {
        swordQi=new SwordQi();
        swordQi.cursed = false;
        swordQi.identify();
        updateSwordQi(tier);
        swordQi.curCharges=swordQi.maxCharges;
        super.initialize();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions=super.actions(hero);
        actions.add(AC_FILL);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (!isEquipped(hero)){
            GLog.i( Messages.get(MeleeWeapon.class, "need_to_equip") );
            return;
        }
        if (action.equals(AC_WEAPONSKILL)) {
            if(swordQi.curCharges>0){
                swordQi.Hit(hero);
            }else {
                Fill();
                updateQuickslot();
            }
        }
        if (action.equals(AC_FILL)){
            Fill();
            updateQuickslot();
        }
    }

    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",swordQi.min(),swordQi.max());
        }else {
            return  Messages.get(this, "typical_stats_desc",1,7);
        }

    }
    private static final String SWORDQI="swordqi";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(SWORDQI,swordQi);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        swordQi=(SwordQi) bundle.get(SWORDQI);
    }
    @Override
    public int damageRoll(Char owner) {
        int damage=super.damageRoll(owner);
        if(swordQi.curCharges>0){
            swordQi.curCharges--;
            updateQuickslot();
            damage+= Random.NormalIntRange( min()/2, max()/2);
        }
        return damage;
    }

    @Override
    public Item upgrade(boolean enchant) {
         super.upgrade(enchant);
         updateSwordQi(tier);
        return this;
    }
    @Override
    public Item degrade() {
        super.degrade();
        updateSwordQi(tier);
        return this;
    }
    @Override
    public String status() {
        if (swordQi == null) return super.status();
        else return swordQi.status();
    }

    @Override
    public int buffedLvl() {
        if(swordQi==null) return super.buffedLvl();
        return super.buffedLvl()+(swordQi.buffedLvl()-swordQi.level());
    }
    private void Fill(){
        swordQi.curCharges=swordQi.maxCharges;
        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.Sounds.READ );
        curUser.spendAndNext( 1f );
    }
    public void updateSwordQi(int tier){
        if (swordQi!= null) {
            int curCharges = swordQi.curCharges;
            swordQi.level(level());
            swordQi.tier=tier;
            //gives the wand one additional max charge
            swordQi.maxCharges = Math.min(swordQi.maxCharges, 10);
            swordQi.curCharges = Math.min(curCharges , swordQi.maxCharges);
            updateQuickslot();
        }
    }


    public  static class SwordQi extends DamageWand {
        public int tier;
        public void Hit(Hero hero){
            execute(hero,"ZAP");
        }

        @Override
        public int min(int lvl) {
            return (tier+lvl)/2;
        }

        @Override
        public int max(int lvl) {
            return (int) Math.ceil((tier+1)*(6+lvl)*0.375f);
        }

        @Override
        public void onZap(Ballistica bolt) {

            Char ch = Actor.findChar( bolt.collisionPos );
            if (ch != null) {
                wandProc(ch, chargesPerCast());
                int dis= Dungeon.level.distance(bolt.collisionPos,bolt.sourcePos)-1;
                if(dis<0)dis=0;
                int dmg=Math.round(damageRoll()*(1f-dis*0.15f));
                if(dmg<0)dmg=0;
                ch.damage(dmg, this);
                Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1-dis*0.2f, Random.Float(0.87f, 1.15f) );

                ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
            } else {
                Dungeon.level.pressCell(bolt.collisionPos);
            }
        }
        @Override
        public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

        }
        @Override
        public void updateLevel() {
            maxCharges = Math.min( initialCharges() + (int)(Math.sqrt(8 * level() + 1) - 1)/2, 10 );
            curCharges = Math.min( curCharges, maxCharges );
        }
        private static final String TIER="tier";
        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put(TIER,tier);
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            tier=bundle.getInt(TIER);
        }
    }
}

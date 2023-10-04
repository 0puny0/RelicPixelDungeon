package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.awt.dnd.DnDConstants;

public class BlessingPower extends Buff implements ActionIndicator.Action {

    public int charge;
    int chargeCap=10;
    float partialCharge = 0;
    public static MeleeWeapon holyWeapon=null;
    public static MeleeWeapon blessWeapon=null;
    static boolean  blessingEquip=false,blessedEquip=false;
    @Override
    public int icon() {
        if(abilityCanUse()){
            if (charge>0&&ActionIndicator.action==null){
                ActionIndicator.setAction( this );
            }
            return BuffIndicator.BLESSING_POWER;
        }else {
            ActionIndicator.clearAction();
            return BuffIndicator.NONE;
        }
    }
    @Override
    public String desc() {
        return Messages.get(this,"desc", charge);
    }
    private static final String CHARGE = "charge";
    private static final String PARTIALCHARGE  = "partialcharge";
//    private static final String HOLYWEAPON = "holyweapon";
//    private static final String BLESSWEAPON = "blessweapon";
    private static final String BLESSINGEQUIP = "blessingequip";
    private static final String BLESSEDEQUIP = "blessedequip";




    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charge);
        bundle.put(PARTIALCHARGE, partialCharge);
        bundle.put(BLESSINGEQUIP, blessingEquip);
        bundle.put(BLESSEDEQUIP, blessedEquip);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge=bundle.getInt(CHARGE);
        partialCharge=bundle.getFloat(PARTIALCHARGE);
        blessingEquip=bundle.getBoolean(BLESSINGEQUIP);
        blessedEquip=bundle.getBoolean(BLESSEDEQUIP);
    }
    @Override
    public boolean act() {
        if (charge <chargeCap&&abilityCanUse()){
            LockedFloor lock = target.buff(LockedFloor.class);
            if (target.buff(Blessing.class)==null && (lock == null || lock.regenOn())) {
                float missing = (chargeCap - charge);
                float turnsToCharge = (35 - missing);
                float chargeToGain = ((1f-Dungeon.hero.pointsInTalent(Talent.ANCESTOR_BLESSING)*0.1f) / turnsToCharge);
                partialCharge += chargeToGain;
            }
            if (partialCharge >= 1) {
                charge++;
                ActionIndicator.refresh();
                partialCharge -= 1;
                if (charge == chargeCap){
                    partialCharge = 0;
                }

            }
        } else {
            partialCharge = 0;
        }
        updateQuickslot();

        spend( TICK );

        return true;
    }
    public static void setHolyWeapon(MeleeWeapon weapon){
        holyWeapon=weapon;
    }
    public static void setBlessWeapon(MeleeWeapon weapon){
        blessWeapon=weapon;
    }
    public void  twinSacredObjects(){
        if(holyWeapon!=null){
            holyWeapon.actions(Dungeon.hero);
        }
        if(blessWeapon!=null){
            blessWeapon.actions(Dungeon.hero);
        }
        ActionIndicator.refresh();
        QuickSlotButton.refresh();
    }
    public boolean twinTakeEffect(){
        return Dungeon.hero.hasTalent(Talent.TWIN_SACRED_OBJECTS)&&holyWeapon!=null&&blessWeapon!=null;
    }
    public boolean abilityCanUse(){

        if(blessingEquip || (Dungeon.hero.hasTalent(Talent.TWIN_SACRED_OBJECTS)&&blessedEquip)){
            return true;
        }else {
            return false;
        }
    }

    public static  void setBlessingEquip(boolean equip){
        blessingEquip=equip;
    }
    public static void setBlessedEquip(boolean equip){
        blessedEquip=equip;
    }
    public boolean isOpen(){
        return Dungeon.hero.buff(Blessing.class)!=null;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }
    @Override
    public int actionIcon() {
        return HeroIcon.BLESSING;
    }

    @Override
    public Visual primaryVisual() {

        if (twinTakeEffect()&& Dungeon.hero.hasTalent(Talent.TWIN_SACRED_OBJECTS)){
            if (blessWeapon.isEquipped(Dungeon.hero)||holyWeapon.isEquipped(Dungeon.hero)){
                Image ico;
                ico = new  ItemSprite(Dungeon.hero.belongings.weapon);
                ico.width += 4;
                return ico;
            }
        }
        return new HeroIcon(this);
    }
    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(charge) );
        txt.hardlight(CharSprite.POSITIVE);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (twinTakeEffect()&& Dungeon.hero.hasTalent(Talent.TWIN_SACRED_OBJECTS)){
            if (holyWeapon.isEquipped(Dungeon.hero)){
                return 0xffff00;
            }
            if(blessWeapon.isEquipped(Dungeon.hero)){
                return 0xfb4d4d;
            }
        }
        return 0xFFFFFF;
    }
    @Override
    public void doAction() {
        if(isOpen()){
            Blessing blessing=Dungeon.hero.buff(Blessing.class);
            if(blessing!=null){
                if (blessing.turnsToCost==0){
                    charge++;
                }
                blessing.detach();
            }
        }else {
            charge--;
            Blessing blessing=new Blessing();
            blessing.attachTo(Dungeon.hero);
        }
        Sample.INSTANCE.play(Assets.Sounds.MISS, 1f, 0.8f);
        target.sprite.emitter().burst(Speck.factory(Speck.JET), 5);
        BuffIndicator.refreshHero();
        ActionIndicator.refresh();
    }

    public  class Blessing extends Buff {
        {
            type = buffType.POSITIVE;
            announced=true;
        }

        int turnsToCost = 0;

        @Override
        public int icon() {
            return BuffIndicator.BLESS;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.brightness(1.5f);
        }

        @Override
        public String desc() {
            return Messages.get(this,"desc",extraLevel(),blessTime()-turnsToCost);
        }

        @Override
        public float iconFadePercent() {
            return 1-(blessTime()-turnsToCost)*1f/blessTime();
        }
        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
            else target.sprite.remove(CharSprite.State.ILLUMINATED);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(turnsToCost);
        }
        public int blessTime(){
            return 6;
        }
        public int extraLevel(){
            if(holyWeapon.buffedLvl()<4){
                return 1+Dungeon.hero.pointsInTalent(Talent.BESTOW_RESONANCE);
            }else {
                return holyWeapon.buffedLvl()/4+Dungeon.hero.pointsInTalent(Talent.BESTOW_RESONANCE) ;
            }
        }
        @Override
        public boolean act(){
            if(!abilityCanUse()){
                detach();
            }
            turnsToCost++;
            if (turnsToCost >=blessTime()){
                charge--;
                if(charge==0){
                    ActionIndicator.clearAction(BlessingPower.this);
                }
                if (charge < 0) {
                    charge = 0;
                    detach();
                    GLog.w(Messages.get(this, "no_charge"));
                    ((Hero) target).interrupt();
                } else {
                    turnsToCost =0;
                }
                ActionIndicator.refresh();
            }
            spend( TICK );

            return true;
        }
        public void dispel(){
            detach();
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo( target )) {
                Dungeon.refreshView();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            if(charge<=0){
                ActionIndicator.clearAction(BlessingPower.this);
            }
            super.detach();
            Dungeon.refreshView();
        }

        private static final String TURNSTOCOST = "turnsToCost";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( TURNSTOCOST , turnsToCost);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            turnsToCost = bundle.getInt( TURNSTOCOST );
        }
    }
}

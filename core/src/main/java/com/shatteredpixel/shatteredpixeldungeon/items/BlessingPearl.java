package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlessingPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class BlessingPearl extends Item{
    {
        image = ItemSpriteSheet.BLESSING_PEARL;

        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;
    }
    public static final String AC_INLAY = "INLAY";
    public static final String AC_INFO = "INFO_WINDOW";
    //为物品添加镶嵌按钮
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions =  super.actions(hero);
        actions.add(AC_INLAY);
        return actions;
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_INLAY)){
            curItem = this;
            GameScene.selectItem(meleeSelector);
        } else if (action.equals(AC_INFO)) {
            GameScene.show(new WndUseItem(null, this));
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
    protected static WndBag.ItemSelector meleeSelector = new WndBag.ItemSelector() {
        @Override
        public String textPrompt() {
            return  Messages.get(BlessedPearl.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof MeleeWeapon
                    &&((MeleeWeapon) item).inlay== EquipableItem.Inlay.noThing;
        }

        @Override
        public void onSelect( Item item ) {
            if (item !=null&&item instanceof MeleeWeapon) {
                GameScene.show(new WndOptions(new ItemSprite(new  BlessingPearl()),
                        Messages.get(BlessingPearl.class, "name"),
                        Messages.get(BlessingPearl.class, "affix_intro"),
                        Messages.get(BlessingPearl.class,"confirm"),
                        Messages.get(BlessingPearl.class,"cancel")){
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0){
                            MeleeWeapon i=(MeleeWeapon) item;
                            if (!i.levelKnown){
                                i.identify();
                                GLog.p(Messages.get(BlessingPearl.class, "unknown_equipment")+"\n");
                            }
                            if (i.cursed ||i.haveLose()){
                                i.removeCurse(true);
                                GLog.p(Messages.get(BlessedPearl.class, "cursed_equipment")+"\n");
                            }
                            GLog.p(Messages.get(BlessingPearl.class, "inlay"));
                            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                            Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                            i.inlay= EquipableItem.Inlay.blessingPeal;
                            BlessingPower.setHolyWeapon(i);
                            Buff.affect(Dungeon.hero, BlessingPower.class);
                            curItem.detach(Dungeon.hero.belongings.backpack);
                            if(item.isEquipped(Dungeon.hero)){
                                BlessingPower.setBlessingEquip(true);
                            }
                        }
                        updateQuickslot();
                    }

                    @Override
                    public void onBackPressed() {
                        //do nothing
                    }
                });


            }
        }
    };

}

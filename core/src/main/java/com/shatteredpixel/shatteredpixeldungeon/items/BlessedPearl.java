package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
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
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class BlessedPearl extends Item{
    {
        image = ItemSpriteSheet.BLESSED_PEARL;

        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;
    }

    public static final String AC_INLAY = "INLAY";

    //only to be used from the quickslot, for tutorial purposes mostly.
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
            GameScene.selectItem(equipSelector);
        } else if (action.equals(AC_INFO)) {
            GameScene.show(new WndUseItem(null, this));
        }
    }
    @Override
    public boolean isUpgradable() {
        if (Dungeon.hero.hasTalent(Talent.UPGRADE_TRANSFER)){
            return level()<Dungeon.hero.pointsInTalent(Talent.UPGRADE_TRANSFER)+1;
        }else {
            return false;
        }

    }

    protected static WndBag.ItemSelector equipSelector = new WndBag.ItemSelector() {
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
            return item instanceof EquipableItem&&!(item instanceof MissileWeapon||item instanceof Artifact)
                    &&((EquipableItem) item).inlay== EquipableItem.Inlay.noThing;
        }

        @Override
        public void onSelect( Item item ) {
            if (item !=null&&item instanceof EquipableItem) {
                EquipableItem i=(EquipableItem) item;
                if (!i.levelKnown){
                    GLog.w(Messages.get(BlessedPearl.class, "unknown_equipment"));
                } else if (i.cursed ){
                    GLog.w(Messages.get(BlessedPearl.class, "cursed_equipment"));
                }  else {
                    GameScene.show(new WndOptions(new ItemSprite(new  BlessedPearl()),
                            Messages.get(BlessedPearl.class, "name"),
                            Messages.get(BlessedPearl.class, "affix_intro"),
                            Messages.get(BlessedPearl.class,"confirm_affix"),
                            Messages.get(BlessedPearl.class,"cancel")){
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0){
                                GLog.p(Messages.get(BlessedPearl.class, "inlay"));
                                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                                i.affixPearl((BlessedPearl) curItem);
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
        }
    };


}

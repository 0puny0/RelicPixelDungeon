package com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Dazzling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestMelee extends TestGenerator {
    {
        image = ItemSpriteSheet.WEAPON_HOLDER;
    }

    private int number = 0;
    private boolean cursed = false;
    private int levelToGen = 0;
    private  int tierToGen=1;
    private int enchant_id = 0;
    private int enchant_rarity = 0;
    private int weapon_id = 0;
    private Class<? extends Weapon>[] weapons;

    @Override
    public ArrayList<String> actions(Hero hero) {
        return super.actions(hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_GIVE)) {
            GameScene.show(new SettingsWindow());
        }
    }

    private Weapon modifyWeapon(Weapon w) {
        w.initialize();
        if(w instanceof MagesStaff) w = new MagesStaff((Wand)Generator.random(Generator.Category.WAND));
        w.level(levelToGen);
        w.tier=tierToGen;
        Class<? extends Weapon.Enchantment> ench = generateEnchant(enchant_rarity, enchant_id);
        if (ench == null) {
            w.enchant(null);
        } else {
            w.enchant(Reflection.newInstance(ench));
        }
        w.cursed = cursed;
        return w;
    }

    private void createWeapon(){
        Weapon melee = Reflection.newInstance(weaponList(number)[weapon_id]);
        melee = modifyWeapon(melee);
        melee.identify();
        if(melee.collect()){
            GLog.i(Messages.get(this, "collect_success", melee.name()));
        }else{
            melee.doDrop(curUser);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("number", number);
        bundle.put("is_cursed", cursed);
        bundle.put("level_to_gen", levelToGen);
        bundle.put("tier_to_gen", tierToGen);
        bundle.put("enchant_rarity", enchant_rarity);
        bundle.put("enchant_id", enchant_id);
        bundle.put("weapon_id", weapon_id);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        number = bundle.getInt("number");
        cursed = bundle.getBoolean("is_cursed");
        levelToGen = bundle.getInt("level_to_gen");
        tierToGen = bundle.getInt("tier_to_gen");
        enchant_rarity = bundle.getInt("enchant_rarity");
        enchant_id = bundle.getInt("enchant_id");
        weapon_id = bundle.getInt("weapon_id");
    }

    private Class<? extends Weapon.Enchantment> generateEnchant(int category, int id) {
        if (category == 1) switch (id) {
            case 0:
                return Blazing.class;
            case 1:
                return Shocking.class;
            case 2:
                return Chilling.class;
            case 3:
                return Kinetic.class;
            default:
                return null;
        }
        else if (category == 2) switch (id) {
            case 0:
                return Blocking.class;
            case 1:
                return Blooming.class;
            case 2:
                return Projecting.class;
            case 3:
                return Elastic.class;
            case 4:
                return Lucky.class;
            case 5:
                return Unstable.class;
            default:
                return null;
        }
        else if (category == 3) switch (id) {
            case 0:
                return Corrupting.class;
            case 1:
                return Grim.class;
            case 2:
                return Vampiric.class;
            default:
                return null;
        }
        else if (category == 4) switch (id) {
            case 0:
                return Annoying.class;
            case 1:
                return Displacing.class;
            case 2:
                return Explosive.class;
            case 3:
                return Dazzling.class;
            case 4:
                return Friendly.class;
            case 5:
                return Polarized.class;
            case 6:
                return Sacrificial.class;
            case 7:
                return Wayward.class;
            default:
                return null;
        }
        return null;
    }
    private void checkWeapons(){
        if(weapons==null){
            Class<? extends Weapon>[] weaponsA=(Class<? extends Weapon>[]) Generator.Category.WEP_R0.classes.clone();
            Class<? extends Weapon>[] weaponsB=(Class<? extends Weapon>[]) Generator.Category.WEP_R1.classes.clone();
            Class<? extends Weapon>[] weaponsC=(Class<? extends Weapon>[]) Generator.Category.WEP_R2.classes.clone();
            weapons = Arrays.copyOf(weaponsA, weaponsA.length+weaponsB.length+weaponsC.length);
            System.arraycopy(weaponsB, 0, weapons, weaponsA.length, weaponsB.length);
            System.arraycopy(weaponsC, 0, weapons, weaponsA.length+weaponsB.length, weaponsC.length);
        }
    }
    private Class<? extends Weapon>[] weaponList(int t) {
        checkWeapons();
        if(t<weapons.length/6){
            Class<? extends Weapon>[] list=Arrays.copyOf(weapons, 6);
            System.arraycopy(weapons, t*6, list, 0, 6);
            return list;
        }else {
            Class<? extends Weapon>[] list=Arrays.copyOf(weapons, weapons.length%6);
            System.arraycopy(weapons, t*6, list, 0, weapons.length%6);
            return list;
        }

    }

    private String currentEnchName(Class<? extends Weapon.Enchantment> ench) {
        if (enchant_rarity < 4)
            return currentEnchName(ench, Messages.get(Weapon.Enchantment.class, "enchant"));
        else
            return currentEnchName(ench, Messages.get(Item.class, "curse"));
    }

    private String currentEnchName(Class<? extends Weapon.Enchantment> ench, String wepName) {
        return Messages.get(ench, "name", wepName);
    }

    private int maxSlots(int t){
        checkWeapons();
        if(t<weapons.length/6) return 6;
        else return weapons.length%6;
    }

    private class SettingsWindow extends Window {
        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 19;
        private static final int GAP = 2;
        private OptionSlider o_number;
        private  OptionSlider o_tier;
        private OptionSlider o_level;
        private OptionSlider o_enchant_rarity;
        private OptionSlider o_enchant_id;
        private CheckBox c_curse;
        private RenderedTextBlock t_selectedWeapon;
        private RenderedTextBlock t_infoEnchant;
        private Class<? extends Weapon>[] all;
        private ArrayList<IconButton> iconButtons = new ArrayList<IconButton>();
        private RedButton b_create;

        private void createWeaponArray() {
            all = weaponList(number);
        }

        public SettingsWindow() {
            super();
            createWeaponArray();
            o_number = new OptionSlider(Messages.get(this, "number"), "0", "5", 0, 5) {
                @Override
                protected void onChange() {
                    number = getSelectedValue();
                    for(IconButton ib : iconButtons.toArray(new IconButton[0])){
                        ib.destroy();
                    }
                    createWeaponArray();
                    createWeaponImage(all);
                }
            };
            o_number.setSelectedValue(number);
            add(o_number);
            o_number.setRect(0, GAP, WIDTH, 24);
            //this is executed in layout because the pos of buttom is affected by the whole window.
            createWeaponImage(all);

            t_selectedWeapon = PixelScene.renderTextBlock("", 6);
            t_selectedWeapon.text(Messages.get(this, "selected", Messages.get(all[Math.min(weapon_id, all.length-1)], "name")));
            t_selectedWeapon.maxWidth(WIDTH);
            add(t_selectedWeapon);
            o_tier= new OptionSlider(Messages.get(this, "tier"), "1", "5", 1, 5) {
                @Override
                protected void onChange() {
                    tierToGen = getSelectedValue();
                }
            };
            o_tier.setSelectedValue(tierToGen);
            add(o_tier);
            o_level = new OptionSlider(Messages.get(this, "level"), "0", "12", 0, 12) {
                @Override
                protected void onChange() {
                    levelToGen = getSelectedValue();
                }
            };
            o_level.setSelectedValue(levelToGen);
            add(o_level);

            t_infoEnchant = PixelScene.renderTextBlock("", 6);
            t_infoEnchant.text(enchantDesc());
            add(t_infoEnchant);

            o_enchant_rarity = new OptionSlider(Messages.get(this, "enchant_rarity"), "0", "4", 0, 4) {
                @Override
                protected void onChange() {
                    enchant_rarity = getSelectedValue();
                    updateEnchantText();
                }
            };
            o_enchant_rarity.setSelectedValue(enchant_rarity);
            add(o_enchant_rarity);

            o_enchant_id = new OptionSlider(Messages.get(this, "enchant_id"), "0", "7", 0, 7) {
                @Override
                protected void onChange() {
                    enchant_id = getSelectedValue();
                    updateEnchantText();
                }
            };
            o_enchant_id.setSelectedValue(enchant_id);
            add(o_enchant_id);

            c_curse = new CheckBox(Messages.get(this, "curse")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    cursed = checked();
                }
            };
            c_curse.checked(cursed);
            add(c_curse);

            b_create = new RedButton(Messages.get(this, "create_button")) {
                @Override
                protected void onClick() {
                    createWeapon();
                }
            };
            add(b_create);

            layout();
        }

        private void createWeaponImage(Class<? extends Weapon>[] all) {
            float left;
            float top = o_number.bottom() + GAP;
            int placed = 0;
            int row = 1;
            int picPerRow = 6;
            int length = all.length;
            left = (WIDTH - BTN_SIZE * Math.min(length, picPerRow)) / 2f;
            for (int i = 0; i < length; ++i) {
                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    protected void onClick() {
                        weapon_id = Math.min(maxSlots(number)-1, j);
                        updateSelectedWeaponText();
                        super.onClick();
                    }
                };
                Image im = new Image(Assets.Sprites.ITEMS);
                im.frame(ItemSpriteSheet.film.get(Objects.requireNonNull(Reflection.newInstance(all[i])).image));
                im.scale.set(1f);
                btn.icon(im);
                btn.setRect(left + placed * BTN_SIZE, top + (row - 1) * (BTN_SIZE + GAP), BTN_SIZE, BTN_SIZE);
                add(btn);
                placed++;
                if (placed > 0 && placed % picPerRow == 0) {
                    placed = 0;
                    left = (WIDTH - BTN_SIZE * Math.min(length - row * picPerRow, picPerRow)) / 2f;
                    row++;
                }
                iconButtons.add(btn);
            }
        }

        private void layout() {
            o_number.setRect(0, GAP, WIDTH, 24);
            //createWeaponImage(all);
            t_selectedWeapon.setPos(0, GAP * 2 + o_number.bottom() + BTN_SIZE);
            o_tier.setRect(0, t_selectedWeapon.bottom() + GAP, WIDTH, 24);
            o_level.setRect(0, GAP+o_tier.bottom(), WIDTH, 24);
            t_infoEnchant.setPos(0, GAP + o_level.bottom());
            o_enchant_rarity.setRect(0, GAP + t_infoEnchant.bottom(), WIDTH, 24);
            o_enchant_id.setRect(0, GAP + o_enchant_rarity.bottom(), WIDTH, 24);
            c_curse.setRect(0, GAP + o_enchant_id.bottom(), WIDTH/2f - GAP/2f, 16);
            b_create.setRect(WIDTH/2f+GAP/2f, c_curse.top(), WIDTH/2f - GAP/2f, 16);
            resize(WIDTH, (int) (c_curse.bottom() + GAP));
        }

        private void updateSelectedWeaponText() {
            t_selectedWeapon.text(Messages.get(this, "selected", Messages.get(all[Math.min(weapon_id, all.length-1)], "name")));
            layout();
        }

        private void updateEnchantText() {
            t_infoEnchant.text(enchantDesc());
            layout();
        }

        private String enchantDesc() {
            //String desc = Messages.get(BossRushMelee.class, "enchant_id_pre", enchant_rarity);
            String desc = "";
            String key = "enchant_id_e" + String.valueOf(enchant_rarity);
            Class<? extends Weapon.Enchantment> ench = generateEnchant(enchant_rarity, enchant_id);
            desc += Messages.get(TestMelee.class, key, (ench == null ? Messages.get(TestMelee.class, "null_enchant") : currentEnchName(ench)));
            return desc;
        }
    }
}

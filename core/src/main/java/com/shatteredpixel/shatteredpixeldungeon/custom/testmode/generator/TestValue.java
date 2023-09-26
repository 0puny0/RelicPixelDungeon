package com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class TestValue  extends TestGenerator{
    {
        image = ItemSpriteSheet.REMAINS;
    }

    private int lvl = 1;
    private int str = 10;

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_GIVE)) {
            GameScene.show(new SettingsWindow());
        }
    }
    private void changeHeroValue(){
        Dungeon.hero.STR=str;
        Dungeon.hero.changeLevel(lvl);
    }
    private class SettingsWindow extends Window {
        private static final int WIDTH = 120;
        private static final int GAP = 2;
        private OptionSlider o_lvl;
        private RenderedTextBlock t_lvl;
        private OptionSlider o_str;
        private RenderedTextBlock t_str;
        private RedButton b_reset;
        private RedButton b_confirm;
        private void resetValue() {
            lvl= Dungeon.hero.lvl;
            str=Dungeon.hero.STR;
            o_lvl.setSelectedValue(lvl);
            o_str.setSelectedValue(str);
        }
        public SettingsWindow() {
            super();

            o_lvl = new OptionSlider(Messages.get(this, "lvl"), "1", "30", 1, 30) {
                @Override
                protected void onChange() {
                    lvl = getSelectedValue();
                    updateText();
                }
            };
            o_lvl.setSelectedValue(lvl);
            add(o_lvl);
            t_lvl = PixelScene.renderTextBlock("", 6);
            t_lvl.text(Messages.get(this, "t_lvl",lvl,Dungeon.hero.lvl));
            t_lvl.visible = true;
            t_lvl.maxWidth(WIDTH);
            add(t_lvl);
            o_str = new OptionSlider(Messages.get(this, "str"), "10", "20", 10, 20) {
                @Override
                protected void onChange() {
                    str = getSelectedValue();
                    updateText();
                }
            };
            o_str.setSelectedValue(str);
            add(o_str);
            t_str = PixelScene.renderTextBlock("", 6);
            t_str.text(Messages.get(this, "t_str",str,Dungeon.hero.STR));
            t_str.visible = true;
            t_str.maxWidth(WIDTH);
            add(t_str);
            b_reset = new RedButton(Messages.get(this, "reset_button")) {
                @Override
                protected void onClick() {
                    resetValue();
                    updateText();
                }
            };
            add(b_reset);
            b_confirm = new RedButton(Messages.get(this, "confirm_button")) {
                @Override
                protected void onClick() {
                    changeHeroValue();
                    updateText();
                }
            };
            add(b_confirm);
            layout();
        }

        private void layout() {
            o_lvl.setRect(0, GAP, WIDTH, 24);
            t_lvl.setPos(0, GAP + o_lvl.top() + o_lvl.height());
            o_str.setRect(0, GAP + t_lvl.bottom(), WIDTH, 24);
            t_str.setPos(0, GAP + o_str.bottom());
            b_reset.setRect(0, GAP + t_str.bottom(), WIDTH / 2f - GAP / 2f, 16);
            b_confirm.setRect(WIDTH / 2f + GAP / 2f, t_str.bottom() + GAP, WIDTH / 2f - GAP / 2f, 16);
            resize(WIDTH, (int) b_confirm.bottom());
        }
        private void updateText() {
            t_lvl.text(Messages.get(this, "t_lvl",lvl,Dungeon.hero.lvl));
            t_str.text(Messages.get(this, "t_str",str,Dungeon.hero.STR));
        }
    }
}

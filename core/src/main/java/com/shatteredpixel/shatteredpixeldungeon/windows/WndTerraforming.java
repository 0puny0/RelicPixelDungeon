package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terraforming;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndTerraforming extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 160;

    private static final int MARGIN  = 2;

    public WndTerraforming( Terraforming terraforming ){
        super();

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        float pos = MARGIN;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(terraforming, "title")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width-title.width())/2, pos);
        title.maxWidth(width - MARGIN * 2);
        add(title);
        pos = title.bottom() + 3*MARGIN;


        RedButton grassBtn = new RedButton(Messages.get(terraforming,"grass",terraforming.grassOpps[Dungeon.depth]), 6){
            @Override
            protected void onClick() {
                super.onClick();
                hide();
                terraforming.produceGrass();
            }
        };
        grassBtn.leftJustify = true;
        grassBtn.multiline = true;
        grassBtn.setSize(width, grassBtn.reqHeight());
        grassBtn.setRect(0, pos, width, grassBtn.reqHeight());
        grassBtn.enable(true);
        add(grassBtn);
        pos = grassBtn.bottom() + MARGIN;

        RedButton waterBtn = new RedButton(Messages.get(terraforming,"water",terraforming.waterOpps[Dungeon.depth]), 6){
            @Override
            protected void onClick() {
                super.onClick();
                hide();
                terraforming.produceWater();
            }
        };
        waterBtn.leftJustify = true;
        waterBtn.multiline = true;
        waterBtn.setSize(width, waterBtn.reqHeight());
        waterBtn.setRect(0, pos, width, waterBtn.reqHeight());
        waterBtn.enable(true);
        add(waterBtn);
        pos = waterBtn.bottom() + MARGIN;

        RedButton doorBtn = new RedButton(Messages.get(terraforming,"door",terraforming.doorOpps[Dungeon.depth]), 6){
            @Override
            protected void onClick() {
                super.onClick();
                hide();
                terraforming.produceDoor();
            }
        };
        doorBtn.leftJustify = true;
        doorBtn.multiline = true;
        doorBtn.setSize(width, doorBtn.reqHeight());
        doorBtn.setRect(0, pos, width, doorBtn.reqHeight());
        doorBtn.enable(true);
        add(doorBtn);
        pos = doorBtn.bottom() + MARGIN;

        resize(width, (int)pos);

    }
}

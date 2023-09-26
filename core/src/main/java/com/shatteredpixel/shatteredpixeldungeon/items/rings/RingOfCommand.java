package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.text.DecimalFormat;

public class RingOfCommand extends Ring{

    {
        icon = ItemSpriteSheet.Icons.RING_COMMAND;
    }

    @Override
    protected RingBuff buff( ) {
        return new Command();
    }

    public static int armedDamageBonus( Char ch ){
        return getBuffedBonus( Dungeon.hero, Command.class);
    }
    public  static float damageResistanceBonus(Char ch){
        return (float)Math.pow(0.90, getBuffedBonus(Dungeon.hero, Command.class)*((float)(ch.HT - ch.HP)/ch.HT));
    }

    @Override
    public String statsInfo() {
        if (isIdentified()) {
            int level = soloBuffedBonus();
            return Messages.get(this, "stats", level,new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.85f, level))));
        } else {
            return Messages.get(this, "typical_stats", 1,new DecimalFormat("#.##").format(10f));
        }
    }

    public class Command extends RingBuff {
    }
}

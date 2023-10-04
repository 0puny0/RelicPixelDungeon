package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;

public class ReinforcedDoor extends Char{
    {
        alignment=Alignment.ENEMY;
        HP=HT=1;
    }

    @Override
    public void damage(int dmg, Object src) {

        if (HP < 0) HP = 0;
        HP -= dmg;
        if (!isAlive()) {
            die( src );
        } else if (HP == 0 && buff(DeathMark.DeathMarkTracker.class) != null){
            DeathMark.processFearTheReaper(this);
        }
    }
}

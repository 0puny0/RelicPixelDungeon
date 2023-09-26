package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ThrowingPlate extends MissileWeapon{
    {
        image = ItemSpriteSheet.THORWING_PLATE;
        bones = false;
        tier = 1;
        baseUses = 1;
        sticky = false;
    }
    @Override
    public void hitSound(float pitch) {
        //no hitsound as it never hits enemies directly
    }
    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.pit[cell]){
            super.onThrow(cell);
            return;
        }

        rangedHit( null, cell );
        Dungeon.level.pressCell(cell);

        ArrayList<Char> targets = new ArrayList<>();
        if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));

        for (int i : PathFinder.NEIGHBOURS8){
            if (!(Dungeon.level.traps.get(cell+i) instanceof TenguDartTrap)) Dungeon.level.pressCell(cell+i);
            if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
        }

        for (Char target : targets){
            curUser.shoot(target, this);
            if (target == Dungeon.hero && !target.isAlive()){
                Badges.validateDeathFromFriendlyMagic();
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "ondeath"));
            }
        }
        Splash.at( cell, 0xf2f1f0,5 );
        Sample.INSTANCE.play( Assets.Sounds.SHATTER);
    }
    @Override
    public int max(int lvl) {
        return  6 * tier +                      //6 base, up from 5
                (tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
    }

    @Override
    public int min(int lvl) {
        return  2 * tier +                      //base
                (tier == 1 ? lvl : 2*lvl);      //level scaling
    }
    @Override
    public int proc( Char attacker, Char defender, int damage ) {
        Buff.affect( defender, Bleeding.class ).set( damage );
        return super.proc( attacker, defender, damage );
    }
    @Override
    public int value() {
        return super.value()/2; //half normal value
    }
}

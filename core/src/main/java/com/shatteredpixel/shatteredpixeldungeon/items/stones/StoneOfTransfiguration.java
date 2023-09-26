package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;

public class StoneOfTransfiguration extends Runestone{
    {
        image = ItemSpriteSheet.STONE_TRANSFIGURATION;
    }
    @Override
    protected void activate(int cell) {

        Sample.INSTANCE.play(Assets.Sounds.PUFF);
        CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 4);
        if (Actor.findChar(cell) != null) {

            Char c = Actor.findChar(cell);
            //TODO boss层逻辑
            if (c instanceof Mob&&!(c instanceof NPC) ){
                if(c.properties().contains(Char.Property.BOSS)){
                    ((Mob) c).yell(Messages.get(c.getClass(),"trans"));
                    return;
                }
                //生成新敌人
                Mob mob=null;
                int depth=Dungeon.depth;
                if(depth%5==0){
                    depth--;
                }
                ArrayList<Class<? extends Mob>> mobs = Bestiary.getMobRotation(depth);
                for (Class<? extends Mob> m:mobs){
                    if(c.getClass()!=m){
                        mob=Reflection.newInstance(m);
                        break;
                    }
                }
                if (mob==null){
                    GLog.i("mobnull");
                    return;
                }
                //复制属性
                mob.pos=c.pos;
                if (((Mob) c).state == ((Mob) c).SLEEPING) {
                        mob.state = mob.SLEEPING;
                    } else if (((Mob) c).state == ((Mob) c).HUNTING) {
                    mob.state = mob.HUNTING;
                    } else {
                    mob.state = mob.WANDERING;
                    }
                mob.alignment=c.alignment;
                HashSet<Buff> buffs = c.buffs();
                //
                if(Dungeon.depth==20 ){
                    c.destroy();
                    if(Statistics.envDangerous){
                        c.sprite.die();
                        GLog.n(Messages.get(this, "crown"));
                        return;
                    }
                }
                for (Buff buff : buffs){
                    c.remove(buff);
                    if(buff instanceof AllyBuff) buff.attachTo(mob);
                }
                Actor.remove( c );
                c.sprite.clearAura();
                c.sprite.killAndErase();
                Dungeon.level.mobs.remove(c);
                GameScene.add(mob);

                TargetHealthIndicator.instance.target(null);
                CellEmitter.get(mob.pos).burst(Speck.factory(Speck.WOOL), 4);
                Dungeon.level.occupyCell(mob);
            }
        }

    }
}

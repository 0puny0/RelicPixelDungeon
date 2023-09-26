package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DemonSword extends MeleeWeapon{
    {
        image = ItemSpriteSheet.DEMON_SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch=1f;
        DMG=0.75f;

    }

    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",Math.min(100,40+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2*15));
        }else {
            return  Messages.get(this, "typical_stats_desc",40);
        }
    }
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        float chance=0.4f+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2*0.15f;
        float random=Random.Float();
        if(random<chance){
            if(Random.Float()<0.5f){
                if(defender.buff(Chill.class)!=null){
                    defender.buff(Chill.class).detach();
                    explosion(defender);
                }else {
                    Buff.affect(defender, Burning.class).reignite(defender);
                }
            }else {
                if(defender.buff(Burning.class)!=null){
                    defender.buff(Burning.class).detach();
                    explosion(defender);
                }else {
                    if (Dungeon.level.water[defender.pos])
                        Buff.affect(defender, Chill.class, 6 + buffedLvl() / 2);
                    else
                        Buff.affect(defender, Chill.class, 3 + buffedLvl() / 2);
                }
            }

        }
        return super.proc(attacker, defender, damage);
    }
    public void explosion(Char defender){
            defender.damage(5+Dungeon.depth, Bomb.class);
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
            CellEmitter.get(defender.pos).burst(ElmoParticle.FACTORY, 10);
    }
}

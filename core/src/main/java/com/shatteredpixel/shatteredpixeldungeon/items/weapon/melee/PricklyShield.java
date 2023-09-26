package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class PricklyShield extends MeleeWeapon{
    {
        image = ItemSpriteSheet.PRICKLY_SHIELD;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch=1f;
        DMG=0.75f;
        DLY=1.5f;

    }

    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",2+2*buffedLvl(),1+buffedLvl(),2+2*buffedLvl());
        }else {
            return  Messages.get(this, "typical_stats_desc",2,1,2);
        }

    }

    public int defenseFactor( Char owner ) {
        return 2+2*buffedLvl();
    }
    public void Reflect(Char owner,Char enemy){
        if(Dungeon.level.distance(owner.pos,enemy.pos)<=1){
            if (owner instanceof Hero&& ((Hero)owner).subClass == HeroSubClass.GLADIATOR ){
                Buff.affect( owner, Combo.class ).hit( enemy );
            }
            int damage=Random.NormalIntRange( 1+1*buffedLvl(), 2+2*buffedLvl() );
            enemy.damage(damage,PricklyShield.class);
            if (!enemy.isAlive() && Dungeon.level.heroFOV[enemy.pos]) {
                if (enemy == Dungeon.hero) {
                    if (owner instanceof WandOfLivingEarth.EarthGuardian
                            || owner instanceof MirrorImage || owner instanceof PrismaticImage){
                        Badges.validateDeathFromFriendlyMagic();
                    }
                    Dungeon.fail( getClass() );
                    GLog.n( Messages.capitalize(Messages.get(enemy.getClass(), "kill", name())) );

                } else if (owner == Dungeon.hero) {
                    GLog.i( Messages.capitalize(Messages.get(owner.getClass(), "defeat", enemy.name())) );
                }
            }
        }
    }

}

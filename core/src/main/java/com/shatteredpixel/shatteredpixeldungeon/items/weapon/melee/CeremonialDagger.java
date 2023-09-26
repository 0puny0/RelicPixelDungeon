package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CeremonialDagger extends MeleeWeapon{
    {
        image = ItemSpriteSheet.CEREMONIAL_DAGGER;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch=1.1f;
        DMG=0.75f;
        hasSkill=true;
        defaultAction=AC_WEAPONSKILL;
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_WEAPONSKILL)) {
            ArrayList<Integer> respawnPoints = new ArrayList<>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                    respawnPoints.add( p );
                }
            }
            if(respawnPoints.size()<=0){
                GLog.i(Messages.get(this,"full"));
                return;
            }
            int needHp= 4+(hero.HT/8+Dungeon.depth+hero.lvl)/(3+buffedLvl());
            if(hero.HP>needHp){
                hero.damage(needHp,this);
                onSUMMON(hero);
            }
        }
    }
    protected  void onSUMMON(Hero hero ){
        if (hero.buff(MagicImmune.class)!=null){
            GLog.i( Messages.get(MeleeWeapon.class, "magic_immune") );
            return;
        }
        Sample.INSTANCE.play(Assets.Sounds.CURSED);
        ArrayList<Integer> respawnPoints = new ArrayList<>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                respawnPoints.add( p );
            }
        }

        if (respawnPoints.size() > 0) {
            int index = Random.index( respawnPoints );

            Wraith w = new Wraith();
            w.adjustStats( Dungeon.scalingDepth() );
            w.state = w.HUNTING;
            Buff.affect(w, Corruption.class);
            GameScene.add( w);
            ScrollOfTeleportation.appear( w, respawnPoints.get( index ) );

            w.sprite.alpha( 0 );
            w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );
            w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
            Invisibility.dispel();
            curUser.spendAndNext( 1f );
        }

    }

    @Override
    public String statsInfo() {
        if (isIdentified()){
            return  Messages.get(this, "stats_desc",4+(Dungeon.hero.HT/8+Dungeon.depth*2+Dungeon.hero.lvl*3)/(6+buffedLvl()));
        }else {
            return  Messages.get(this, "typical_stats_desc",3);
        }

    }
}

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTerraforming;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class Terraforming  extends Buff implements ActionIndicator.Action {
        public  int[] grassOpps=new int[26];
        public  int[] waterOpps=new int[26];
        public  int[] doorOpps=new int[26];
    private static final String GRASSOPPS="grassopps";
    private static final String WATEROPPS="wateropps";
    private static final String DOOROPPS="dooropps";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(GRASSOPPS, grassOpps);
        bundle.put(WATEROPPS, waterOpps);
        bundle.put(DOOROPPS, doorOpps);
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        grassOpps = bundle.getIntArray( GRASSOPPS );
        waterOpps = bundle.getIntArray( WATEROPPS );
        doorOpps = bundle.getIntArray( DOOROPPS );
        ActionIndicator.setAction(this);
    }
    public void produceGrass(){
        if(grassOpps[Dungeon.depth]>0){
            Ballistica aim;
            //Basically the direction of the aim only matters if it goes outside the map
            //So we just ensure it won't do that.
            if (Dungeon.hero.pos % Dungeon.level.width() > 10){
                aim = new Ballistica(Dungeon.hero.pos, Dungeon.hero.pos - 1, Ballistica.WONT_STOP);
            } else {
                aim = new Ballistica(Dungeon.hero.pos, Dungeon.hero.pos + 1, Ballistica.WONT_STOP);
            }
            int aoeSize = 3 ;

            int projectileProps = Ballistica.PENETRATE_BOLT;

            ConeAOE aoe = new ConeAOE(aim, aoeSize, 360, projectileProps);

            for (Ballistica ray : aoe.outerRays){
                ((MagicMissile)Dungeon.hero.sprite.parent.recycle( MagicMissile.class )).reset(
                        MagicMissile.FOLIAGE_CONE,
                        Dungeon.hero.sprite,
                        ray.path.get(ray.dist),
                        null
                );
            }
            for (int cell : aoe.cells) {
                int t = Dungeon.level.map[cell];
                if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
                        || t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
                        && Dungeon.level.plants.get(cell) == null) {
                    Level.set(cell, Terrain.HIGH_GRASS);
                    GameScene.updateMap(cell);
                    Char mob = Actor.findChar(cell);
                    if (mob != null && mob != Dungeon.hero){
                        Buff.prolong( mob, Roots.class, Roots.DURATION );
                    }
                }
            }
            Dungeon.hero.spendAndNext(TICK);
            grassOpps[Dungeon.depth]--;
        }else {
            GLog.n(Messages.get(this,"no_odds"));
        }
    }
    public void produceWater(){
        if(waterOpps[Dungeon.depth]>0){
            int centerVolume = 90;
            for (int i : PathFinder.NEIGHBOURS8){
                if (!Dungeon.level.solid[target.pos+i]){
                    GameScene.add( Blob.seed( target.pos+i, 90, StormCloud.class ) );
                } else {
                    centerVolume += 90;
                }
            }
            GameScene.add( Blob.seed( target.pos, centerVolume, StormCloud.class ) );
            Buff.prolong(Dungeon.hero, ProduceWaterTracker.class, TICK);
            Dungeon.hero.spendAndNext(TICK);
            waterOpps[Dungeon.depth]--;
        }else {
            GLog.n(Messages.get(this,"no_odds"));
        }
    }
    public static class ProduceWaterTracker extends FlavourBuff{};
    public void produceDoor(){
        if(doorOpps[Dungeon.depth]>0){
            GameScene.selectCell(doorListener);
        }else {
            GLog.n(Messages.get(this,"no_odds"));
        }
    }
    private CellSelector.Listener doorListener = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (!Dungeon.level.adjacent(Dungeon.hero.pos, cell)){
                return;
            }
            if(Dungeon.level.map[cell]!=Terrain.DOOR&&Dungeon.level.map[cell]!=Terrain.OPEN_DOOR){
                GLog.i(Messages.get(Terraforming.class, "door_prompt"));
                return;
            }
            final Char enemy = Actor.findChar( cell );
            Hero hero=Dungeon.hero;
            if(Dungeon.level.map[cell]== Terrain.OPEN_DOOR&&enemy!=null){
                Ballistica trajectory = new Ballistica(hero.pos, enemy.pos, Ballistica.STOP_TARGET);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(enemy, trajectory, 1, true, true, getClass());
            }
            IronKey key=new IronKey(Dungeon.depth);
            GameScene.pickUpJournal(key, cell);
            WndJournal.last_index = 2;
            Notes.add(key);
            Sample.INSTANCE.play( Assets.Sounds.ITEM );
            Level.set(cell, Terrain.LOCKED_DOOR);
            GameScene.updateKeyDisplay();
            GameScene.updateMap(cell);
            doorOpps[Dungeon.depth]--;
            hero.sprite.attack(cell, new Callback() {
                @Override
                public void call() {
                    Dungeon.hero.onOperateComplete();
                }
            }) ;
            hero.spendAndNext(TICK);
            isClosed();
        }

        @Override
        public String prompt() {
            return Messages.get(Terraforming.class, "door_prompt");
        }
    };
    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }
    @Override
    public int actionIcon() {
        return HeroIcon.TERRAFORMING;
    }
    @Override
    public int indicatorColor() {
        return 0x00b300;
    }

    @Override
    public void doAction() {
        GameScene.show(new WndTerraforming(this));
    }
    public static void isClosed(){
        if(Dungeon.hero.buff(RelaxVigilance.class)!=null){
            Dungeon.hero.buff(RelaxVigilance.class).detach();
        }
        if(Dungeon.depth%5==0){
            return;
        }
        PathFinder.buildDistanceMap(Dungeon.level.entrance(), BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
        if (PathFinder.distance[Dungeon.hero.pos] != Integer.MAX_VALUE){
            return;
        }
        PathFinder.buildDistanceMap(Dungeon.level.exit(), BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
        if (PathFinder.distance[Dungeon.hero.pos] != Integer.MAX_VALUE){
            return;
        }
        Buff.affect(Dungeon.hero,RelaxVigilance.class);
    }
    public static class RelaxVigilance extends Buff {
        {
            announced = true;
        }
        @Override
        public int icon() {
            return BuffIndicator.DROWSY;
        }
        @Override
        public void tintIcon(Image icon) { icon.hardlight(0f, 0.4f, 0.8f); }
    }
}

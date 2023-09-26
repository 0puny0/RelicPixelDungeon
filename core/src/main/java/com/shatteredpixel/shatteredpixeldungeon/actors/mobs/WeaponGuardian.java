package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class WeaponGuardian extends Mob{
    {
        spriteClass = StatueSprite.class;

        EXP = 0;
        state = PASSIVE;

        properties.add(Property.INORGANIC);
    }
    public WeaponGuardian() {
        super();
        weapon =(MeleeWeapon) Generator.random(Generator.wepRarity[2]);
        weapon.tier=3;
        weapon.removeCurse(true);
        if (weapon.level() == 0 && Random.Int(2) == 0){
            weapon.upgrade();
        }
        HP = HT = 18 + Dungeon.depth * 6;
        defenseSkill = 4 + Dungeon.depth;
    }

    public MeleeWeapon weapon;
    public boolean levelGenStatue = true;
    private static final String WEAPON	= "weapon";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( WEAPON, weapon );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        weapon = (MeleeWeapon) bundle.get( WEAPON );
    }
    @Override
    protected boolean act() {
        if (levelGenStatue && Dungeon.level.heroFOV[pos]) {
            Notes.add( Notes.Landmark.STATUE );
        }
        return super.act();
    }
    protected void activate(){
        state = HUNTING;
    }
    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1+Dungeon.depth, 2+Dungeon.depth*2 );
    }

    @Override
    public int attackSkill( Char target ) {
        return (int)(9 + Dungeon.depth);

    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(Dungeon.depth/5, Dungeon.depth );
    }

    @Override
    public void add(Buff buff) {
        super.add(buff);
        if (state == PASSIVE && buff.type == Buff.buffType.NEGATIVE){
           activate();
        }
    }
    @Override
    public void damage( int dmg, Object src ) {
        if (state == PASSIVE) {
            activate();
        }
        super.damage( dmg, src );
    }
    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (!enemy.isAlive() && enemy == Dungeon.hero){
            Dungeon.fail(getClass());
            GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
        }
        return damage;
    }
    @Override
    public void beckon( int cell ) {
        // Do nothing
    }
    @Override
    public void die( Object cause ) {
        weapon.identify(false);
        Dungeon.level.drop( weapon, pos ).sprite.drop();
        super.die( cause );
    }
    @Override
    public void destroy() {
        if (levelGenStatue) {
            Notes.remove( Notes.Landmark.STATUE );
        }
        super.destroy();
    }
    @Override
    public float spawningWeight() {
        return 0f;
    }
    @Override
    public boolean reset() {
        state = PASSIVE;
        return true;
    }

    @Override
    public String description() {
        return Messages.get(WeaponGuardian.class, "desc", weapon.name())
                +"\n\n" + Messages.get(this,"desc");
    }

    {
        resistances.add(Grim.class);
    }

    public static WeaponGuardian random(){
        switch (Random.Int(6)){
            case 0:
                return new Pursuit();
            case 1:
                return new Control();
            case 2:
                return new Guard();
            case 3:
                return new Bomb();
            case 4:
                return new Fury();
            case 5:
                return new Fear();
        }
        return null;
    }
    public static class Pursuit extends WeaponGuardian{
        {
            baseSpeed = 2f;
            spriteClass=PursuitSprite.class;
        }
        @Override
        public float attackDelay() {
            return super.attackDelay()*0.5f;
        }
        @Override
        public int damageRoll() {
            return super.damageRoll()/2;
        }
        public static class PursuitSprite extends StatueSprite {

            public PursuitSprite(){
                super();
                tint(0.7f, 0f, 0.6f, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0.7f, 0f, 0.6f,0.3f);
            }
        }
    }
    public static class Control extends WeaponGuardian{
        {
            HUNTING = new Hunting();
            spriteClass = ControlSprite.class;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage =super.attackProc(enemy, damage);
            Ballistica trajectory = new Ballistica(this.pos, enemy.pos, Ballistica.STOP_TARGET);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
            WandOfBlastWave.throwChar(enemy, trajectory, 1, true, true, getClass());
            return damage;
        }
        private class Hunting extends Mob.Hunting{
            @Override
            public boolean act( boolean enemyInFOV, boolean justAlerted ) {
                enemySeen = enemyInFOV;
                if (    enemyInFOV
                        && !isCharmedBy( enemy )
                        && !canAttack( enemy )
                        && Dungeon.level.distance( pos, enemy.pos ) > 2
                        && chain(enemy.pos)){
                    return !(sprite.visible || enemy.sprite.visible);
                } else {
                    return super.act( enemyInFOV, justAlerted );
                }

            }
        }
        private boolean chain(int target){
            if ( enemy.properties().contains(Property.IMMOVABLE))
                return false;

            Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

            if (chain.collisionPos != enemy.pos
                    || chain.path.size() < 2
                    || Dungeon.level.pit[chain.path.get(1)])
                return false;
            else {
                int newPos = -1;
                for (int i : chain.subPath(1, chain.dist)){
                    if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                        newPos = i;
                        break;
                    }
                }

                if (newPos == -1){
                    return false;
                } else {
                    final int newPosFinal = newPos;
                    this.target = newPos;

                    if (sprite.visible || enemy.sprite.visible) {
                        new Item().throwSound();
                        Sample.INSTANCE.play(Assets.Sounds.CHAINS);
                        sprite.parent.add(new Chains(sprite.center(),
                                enemy.sprite.destinationCenter(),
                                Effects.Type.CHAIN,
                                new Callback() {
                                    public void call() {
                                        Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback() {
                                            public void call() {
                                                pullEnemy(enemy, newPosFinal);
                                            }
                                        }), -1);
                                        next();
                                    }
                                }));
                    } else {
                        pullEnemy(enemy, newPos);
                    }
                }
            }
            return true;
        }
        private void pullEnemy( Char enemy, int pullPos ){
            enemy.pos = pullPos;
            enemy.sprite.place(pullPos);
            Dungeon.level.occupyCell(enemy);
            if (enemy == Dungeon.hero) {
                Dungeon.hero.interrupt();
                Dungeon.observe();
                GameScene.updateFog();
            }
        }
        public static class ControlSprite extends StatueSprite {

            public ControlSprite(){
                super();
                tint(0, 0, 1, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0, 0, 1, 0.3f);
            }
        }

    }
    public static class Guard extends WeaponGuardian{
        {
            spriteClass = GuardSprite.class;
            properties.add(Property.LARGE);
            HP = HT = 36 + Dungeon.depth * 12;
        }

        @Override
        protected boolean canAttack(Char enemy) {
            if (Dungeon.level.distance( pos, enemy.pos ) > 2){
                return false;
            } else {
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                for (Char ch : Actor.chars()) {
                    //our own tile is always passable
                    passable[ch.pos] = ch == this ;
                }
                PathFinder.buildDistanceMap(enemy.pos, passable, 2);

                return PathFinder.distance[pos] <= 2;
            }
        }

        public static class GuardSprite extends StatueSprite {

            public GuardSprite(){
                super();
                tint(1f, 1, 0f, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(1f, 1, 0f,0.3f);
            }
        }
    }
    public static class Bomb extends WeaponGuardian{
        {
            spriteClass=BombSprite.class;
            HP = HT = 12 + Dungeon.depth * 4;
        }

        @Override
        protected boolean act() {
            if (    state==HUNTING
                    && !isCharmedBy( enemy )
                    && canAttack( enemy )
                    &&  isAlive()
            ){
               damage(HT,this);
               return  true;
            }else {
                return super.act();
            }

        }

        @Override
        public void die(Object cause) {

            explode();
            super.die(cause);
        }
        public void explode(){
            Sample.INSTANCE.play( Assets.Sounds.BLAST );

            ArrayList<Char> affected = new ArrayList<>();

            if (Dungeon.level.heroFOV[pos]) {
                CellEmitter.center(pos).burst(BlastParticle.FACTORY, 30);
            }

            boolean terrainAffected = false;
            for (int n : PathFinder.NEIGHBOURS25) {
                int c = pos + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                        terrainAffected = true;
                    }

                    //destroys items / triggers bombs caught in the blast.
                    Heap heap = Dungeon.level.heaps.get(c);
                    if (heap != null)
                        heap.explode();

                    Char ch = Actor.findChar(c);
                    if (ch != null) {
                        affected.add(ch);
                    }
                }
            }

            for (Char ch : affected){

                //if they have already been killed by another bomb
                if(!ch.isAlive()){
                    continue;
                }

                int dmg = damageRoll()+damageRoll()+damageRoll();

                dmg -= ch.drRoll();

                if (dmg > 0) {
                    ch.damage(dmg, this);
                }

                if (ch == Dungeon.hero && !ch.isAlive()) {
                    Dungeon.fail(WeaponGuardian.class);
                }
            }

            if (terrainAffected) {
                Dungeon.observe();
            }
        }

        public static class BombSprite extends StatueSprite {

            public BombSprite(){
                super();
                tint(0f, 0f, 0f, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0f, 0f, 0f,0.3f);
            }
        }
    }
    public static class Fury extends WeaponGuardian{
        {
            spriteClass=FurySprite.class;
        }

        @Override
        public int damageRoll() {
            return Math.round(super.damageRoll()*1.5f);
        }

        @Override
        protected void activate() {
            Buff.affect(this, Amok.class, 999);
            super.activate();
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(Dungeon.depth/2, Dungeon.depth*3/2 );
        }
        public static class FurySprite extends StatueSprite {

            public FurySprite(){
                super();
                tint(0.9f, 0f, 0f, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0.9f, 0f, 0f,0.3f);
            }
        }
    }
    public static class Fear extends WeaponGuardian{
        {
            spriteClass=FearSprite.class;
            HP = HT = 12 + Dungeon.depth * 4;
            WANDERING = new Wandering();
            FLEEING = new Fleeing();
        }

        @Override
        public void damage( int dmg, Object src ) {
            Buff.prolong(this, Haste.class, 1f);
            super.damage(dmg,src);
        }

        @Override
        protected void activate() {
            state = FLEEING;
            Buff.affect(this, Haste.class, 2f);
            if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
                enemy = Dungeon.hero;
                enemySeen = true;
                GameScene.flash( 0x80FFFFFF );
                Buff.prolong(enemy, Blindness.class, Blindness.DURATION);
                int count = 32;
                int newPos;
                do {
                    newPos = Dungeon.level.randomRespawnCell( Fear.this );
                    if (count-- <= 0) {
                        break;
                    }
                } while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

                if (newPos != -1) {

                    if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);
                    pos = newPos;
                    sprite.place( pos );
                    sprite.visible = Dungeon.level.heroFOV[pos];
                    if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

                }

                GLog.n( Messages.get(Fear.class, "escapes"));
                state = WANDERING;
                Dungeon.observe();
            }
        }

        private class Wandering extends Mob.Wandering {
            @Override
            public boolean act(boolean enemyInFOV, boolean justAlerted) {
                super.act(enemyInFOV, justAlerted);
                if (state == HUNTING ){
                    state = FLEEING;
                }

                return true;
            }
        }
        private class Fleeing extends Mob.Fleeing{
            @Override
            protected void nowhereToRun() {

                if (buff( Terror.class ) == null
                        && buffs( AllyBuff.class ).isEmpty()
                        && buff( Dread.class ) == null) {
                    if (enemySeen) {
                        sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
                        state = HUNTING;
                    } else  {
                        state = WANDERING;
                    }
                } else {
                    super.nowhereToRun();
                }
            }
        }
        public static class FearSprite extends StatueSprite {

            public FearSprite(){
                super();
                tint(0.8f, 0.8f, 0.8f, 0.3f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                tint(0.8f, 0.8f, 0.8f,0.3f);
            }
        }
    }




}



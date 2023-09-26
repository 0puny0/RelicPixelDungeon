package com.shatteredpixel.shatteredpixeldungeon.items.testItems;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WeaponGuardian;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Summon extends Item {
    {
        image = ItemSpriteSheet.CEREMONIAL_DAGGER;
        stackable = true;
        defaultAction = AC_SUMMON;
    }

    public static final String AC_SUMMON	= "SUMMON";
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SUMMON);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_SUMMON)) {
            onCast(hero);
        }
    }
    private   void onSUMMON(int cell ){
        WeaponGuardian text=WeaponGuardian.random();
        text.pos = cell;
        GameScene.add(text);
        text.beckon(Dungeon.hero.pos);
    }
    protected  void onCast(Hero hero ){
        GameScene.selectCell(targeter);
    }
    private CellSelector.Listener targeter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.PROJECTILE);
                int cell = shot.collisionPos;

                curUser.sprite.zap(cell);
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                curUser.busy();

                fx(shot, new Callback() {
                    public void call() {
                        affectTarget(shot, curUser);
                        Invisibility.dispel();
                        updateQuickslot();
                        curUser.spendAndNext( 1f );
                    }
                });
            }
        }
        @Override
        public String prompt() {
            return Messages.get(TargetedSpell.class, "prompt");
        }
    };

    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.BEACON,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
    }

    protected void affectTarget(Ballistica bolt, Hero hero) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch==null){
            onSUMMON(bolt.collisionPos);
        }

    }
    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 15 * quantity;
    }

    @Override
    public int energyVal() {
        return 3 * quantity;
    }

}

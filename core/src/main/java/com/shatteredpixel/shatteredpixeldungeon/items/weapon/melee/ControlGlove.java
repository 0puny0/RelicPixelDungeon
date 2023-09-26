package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ControlGlove extends MeleeWeapon{

    {
        image = ItemSpriteSheet.CONTROL_GLOVE;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch=1f;
        DMG=0.5f;
        hasSkill=true;
        defaultAction=AC_WEAPONSKILL;
    }
    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_WEAPONSKILL)) {
            if (!isEquipped(hero)){
                GLog.i( Messages.get(MeleeWeapon.class, "need_to_equip") );
                return;
            }
            if (hero.buff(MagicImmune.class)!=null){
                GLog.i( Messages.get(MeleeWeapon.class, "magic_immune") );
                return;
            }
            onCast(hero);
        }
    }
    public float durabilityMultiplier(){
        return (float)(Math.pow(1.2, buffedLvl()+1));
    }
    @Override
    public String statsInfo() {
        if(isIdentified()){
            return  Messages.get(this, "stats_desc",1+(int) ((Math.sqrt(8 * buffedLvl() + 1) - 1)/2),new DecimalFormat("#.##").format(100f * (Math.pow(1.2, buffedLvl()+1) - 1f)));
        }else {
            return  Messages.get(this, "typical_stats_desc",1,20);
        }

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

        //special logic for DK when he is on his throne
        if (ch == null && bolt.path.size() > bolt.dist+1){
            ch = Actor.findChar(bolt.path.get(bolt.dist+1));
            if (!(ch instanceof DwarfKing && Dungeon.level.solid[ch.pos])){
                ch = null;
            }
        }

        if (ch != null && ch.buff(PinCushion.class) != null){
            for (int i=0;ch.buff(PinCushion.class) != null&&(i<1+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2);i++) {
                Item item = ch.buff(PinCushion.class).grabOne();

                if (item.doPickUp(hero, ch.pos)) {
                    hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
                    GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );

                     } else {
                    GLog.w(Messages.get(this, "cant_grab"));
                    Dungeon.level.drop(item, ch.pos).sprite.drop();
                    return;
                     }

            }

        } else if (Dungeon.level.heaps.get(bolt.collisionPos) != null)
        {

            Heap h = Dungeon.level.heaps.get(bolt.collisionPos);

            if (h.type != Heap.Type.HEAP){
                GLog.w(Messages.get(this, "cant_grab"));
                h.sprite.drop();
                return;
            }

            for (int i=0;i<1+(int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2&&!h.isEmpty();i++) {
                Item item = h.peek();
                if (item.doPickUp(hero, h.pos)) {
                    h.pickUp();
                    hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
                    GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );

                } else {
                    GLog.w(Messages.get(this, "cant_grab"));
                    h.sprite.drop();
                    return;
                }
            }

        } else {
            GLog.w(Messages.get(this, "no_target"));
        }

    }
}

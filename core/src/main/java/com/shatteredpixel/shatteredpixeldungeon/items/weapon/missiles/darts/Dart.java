/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Dart extends MissileWeapon {

	{
		image = ItemSpriteSheet.DART;
		hitSound = Assets.Sounds.HIT_ARROW;
		hitSoundPitch = 1.3f;
		
		tier = 1;
		
		//infinite, even with penalties
		baseUses = 1000;
	}
	
	protected static final String AC_TIP = "TIP";
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_TIP );
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_TIP)){
			GameScene.selectItem(itemSelector);
		}
	}
	
	@Override
	public int min(int lvl) {
		if (bow != null){
			return bow.getDartMin() + lvl; //+1 per level or bow level
		} else {
			return  1 +     //1 base, down from 2
					lvl;    //scaling unchanged
		}
	}

	@Override
	public int max(int lvl) {
		if (bow != null){
			return  bow.getDartMax() + 2*lvl; //+3 per bow level, +2 per level (default scaling +2)
		} else {
			return  2 +     //2 base, down from 5
					2*lvl;  //scaling unchanged
		}
	}

	@Override
	public int damageRoll(Char owner) {
		if(bow != null){
			int damage=Random.NormalIntRange( min(), max() );
			if (owner instanceof Hero&&owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
				damage = Math.round(damage * (1f + 0.15f * ((Hero) owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
			}
			return damage;
		}else {
			return super.damageRoll(owner);
		}
	}

	private static Crossbow bow;
	
	private void updateCrossbow(){
		if (Dungeon.hero.belongings.weapon() instanceof Crossbow){
			bow = (Crossbow) Dungeon.hero.belongings.weapon();
		} else {
			bow = null;
		}
	}

	public boolean crossbowHasEnchant( Char owner ){
		return bow != null && bow.enchantment != null && owner.buff(MagicImmune.class) == null;
	}
	
	@Override
	public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
		if (bow != null && bow.hasEnchant(type, owner)){
			return true;
		} else {
			return super.hasEnchant(type, owner);
		}
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (bow != null){
			damage = bow.proc(attacker, defender, damage);
		}

		return super.proc(attacker, defender, damage);
	}

	@Override
	public int throwPos(Hero user, int dst) {
		updateCrossbow();
		if(bow!=null&&bow.form==Form.FORM1){
			return new Ballistica( user.pos, dst, Ballistica.PENETRATE_BOLT ).collisionPos;
		}
		return super.throwPos(user, dst);
	}


	@Override
	protected void onThrow(int cell) {
		updateCrossbow();
		if(bow!=null&&bow.form==Form.FORM1){
			final Ballistica shot = new Ballistica( curUser.pos, cell, Ballistica.PENETRATE_BOLT);
			//寻找路劲上的敌人
			ArrayList<Char> chars = new ArrayList<>();
			for (int c : shot.subPath(1, shot.dist)) {
				Char ch;
				if ((ch = Actor.findChar( c )) != null) {
					if (ch instanceof Mob && ((Mob) ch).state == ((Mob) ch).PASSIVE
							&& !(Dungeon.level.mapped[c] || Dungeon.level.visited[c])){
					} else {
						chars.add(ch);
					}
				}
			}
			for(Char enemy:chars){
				if (curUser.shoot( enemy, this )){
					decrementDurability();
				}
			}
			parent = null;
			if (durability <= 0){
				//attempt to stick the dart to the enemy, just drop it if we can't.
				Dart d = new Dart();
				Dungeon.level.drop( d, cell).sprite.drop();
			}else {
				Dungeon.level.drop( this, cell).sprite.drop();
			}
			if (curUser.hasTalent(Talent.SEER_SHOT)
					&& curUser.heroClass != HeroClass.HUNTRESS
					&& curUser.buff(Talent.SeerShotCooldown.class) == null){
				if (Actor.findChar(cell) == null) {
					RevealedArea a = Buff.affect(curUser, RevealedArea.class, 5 * curUser.pointsInTalent(Talent.SEER_SHOT));
					a.depth = Dungeon.depth;
					a.pos = cell;
					Buff.affect(curUser, Talent.SeerShotCooldown.class, 20f);
				}
			}
		}else if(bow!=null&&bow.form==Form.FORM2){
			if (Dungeon.level.pit[cell]){
				super.onThrow(cell);
				return;
			}
			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser){
				decrementDurability();
				if (curUser.hasTalent(Talent.SEER_SHOT)
						&& curUser.heroClass != HeroClass.HUNTRESS
						&& curUser.buff(Talent.SeerShotCooldown.class) == null){
					if (Actor.findChar(cell) == null) {
						RevealedArea a = Buff.affect(curUser, RevealedArea.class, 5 * curUser.pointsInTalent(Talent.SEER_SHOT));
						a.depth = Dungeon.depth;
						a.pos = cell;
						Buff.affect(curUser, Talent.SeerShotCooldown.class, 20f);
					}
				}
				//need to spawn a dart
				if (durability <= 0){
					//attempt to stick the dart to the enemy, just drop it if we can't.
					Dart d = new Dart();
					Dungeon.level.drop( d, cell).sprite.drop();
				}
			}else {
				super.onThrow(cell);
			}
			ArrayList<Char> targets = new ArrayList<>();
			if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
			}
			for (Char target:targets){
				if(target!=Dungeon.hero) proc(curUser,target,0);
			}
			WandOfBlastWave.BlastWave.blast(cell);
		}else {
			super.onThrow(cell);
		}
	}

	@Override
	public void throwSound() {
		updateCrossbow();
		if (bow != null) {
			Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW, 1, Random.Float(0.87f, 1.15f));
		} else {
			super.throwSound();
		}
	}
	
	@Override
	public String info() {
		updateCrossbow();
		if (bow != null && !bow.isIdentified()){
			int level = bow.level();
			//temporarily sets the level of the bow to 0 for IDing purposes
			bow.level(0);
			String info = super.info();
			bow.level(level);
			return info;
		} else {
			return super.info();
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public int value() {
		return super.value()/2; //half normal value
	}
	
	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(Dart.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return VelvetPouch.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Plant.Seed;
		}

		@Override
		public void onSelect(final Item item) {
			
			if (item == null) return;
			
			final int maxToTip = Math.min(curItem.quantity(), item.quantity()*2);
			final int maxSeedsToUse = (maxToTip+1)/2;
			
			final int singleSeedDarts;
			
			final String[] options;
			
			if (curItem.quantity() == 1){
				singleSeedDarts = 1;
				options = new String[]{
						Messages.get(Dart.class, "tip_one"),
						Messages.get(Dart.class, "tip_cancel")};
			} else {
				singleSeedDarts = 2;
				if (maxToTip <= 2){
					options = new String[]{
							Messages.get(Dart.class, "tip_two"),
							Messages.get(Dart.class, "tip_cancel")};
				} else {
					options = new String[]{
							Messages.get(Dart.class, "tip_all", maxToTip, maxSeedsToUse),
							Messages.get(Dart.class, "tip_two"),
							Messages.get(Dart.class, "tip_cancel")};
				}
			}
			
			TippedDart tipResult = TippedDart.getTipped((Plant.Seed) item, 1);
			
			GameScene.show(new WndOptions( new ItemSprite(item),
					Messages.titleCase(item.name()),
					Messages.get(Dart.class, "tip_desc", tipResult.name()) + "\n\n" + tipResult.desc(),
					options){
				
				@Override
				protected void onSelect(int index) {
					super.onSelect(index);
					
					if (index == 0 && options.length == 3){
						if (item.quantity() <= maxSeedsToUse){
							item.detachAll( curUser.belongings.backpack );
						} else {
							item.quantity(item.quantity() - maxSeedsToUse);
						}
						
						if (maxToTip < curItem.quantity()){
							curItem.quantity(curItem.quantity() - maxToTip);
						} else {
							curItem.detachAll(curUser.belongings.backpack);
						}
						
						TippedDart newDart = TippedDart.getTipped((Plant.Seed) item, maxToTip);
						if (!newDart.collect()) Dungeon.level.drop(newDart, curUser.pos).sprite.drop();
						
						curUser.spend( 1f );
						curUser.busy();
						curUser.sprite.operate(curUser.pos);
						
					} else if ((index == 1 && options.length == 3) || (index == 0 && options.length == 2)){
						item.detach( curUser.belongings.backpack );
						
						if (curItem.quantity() <= singleSeedDarts){
							curItem.detachAll( curUser.belongings.backpack );
						} else {
							curItem.quantity(curItem.quantity() - singleSeedDarts);
						}
						
						TippedDart newDart = TippedDart.getTipped((Plant.Seed) item, singleSeedDarts);
						if (!newDart.collect()) Dungeon.level.drop(newDart, curUser.pos).sprite.drop();
						
						curUser.spend( 1f );
						curUser.busy();
						curUser.sprite.operate(curUser.pos);
					}
				}
			});
			
		}
		
	};


}

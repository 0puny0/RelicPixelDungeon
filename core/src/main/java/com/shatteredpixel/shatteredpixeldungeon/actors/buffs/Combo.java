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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndCombo;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Combo extends Buff implements ActionIndicator.Action {

	{
		type = buffType.POSITIVE;
	}
	
	private int count = 0;//连击数
	private float comboTime = 0f;//消退时间

	@Override
	public int icon() {
		return BuffIndicator.COMBO;
	}
	//buff图标颜色
	@Override
	public void tintIcon(Image icon) {
		if (count >= 10)    icon.hardlight(1f, 0f, 0f);
		else if (count >= 8)icon.hardlight(1f, 0.8f, 0f);
		else if (count >= 6)icon.hardlight(1f, 1f, 0f);
		else if (count >= 4)icon.hardlight(0.8f, 1f, 0f);
		else if (count >= 2)icon.hardlight(0f, 1f, 0f);
		else                icon.resetColor();
	}

	@Override
	public float iconFadePercent() {
		float time=4f;
		if(Dungeon.hero.hasTalent(Talent.KEEP_VIGILANCE)){
			int point=Dungeon.hero.pointsInTalent(Talent.KEEP_VIGILANCE);
			switch (point){
				default: case 1:
					time++;
					break;
				case 2:
					time+=10;
					break;
				case 3:
					time=999;
					break;
			}
		}
		return Math.max(0, (time- comboTime)/time);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)comboTime);
	}
	
	public void hit( Char enemy ) {

		count++;
		comboTime = 4f;
		if(Dungeon.hero.hasTalent(Talent.KEEP_VIGILANCE)){
			int point=Dungeon.hero.pointsInTalent(Talent.KEEP_VIGILANCE);
			switch (point){
				default: case 1:
					comboTime++;
					break;
				case 2:
					comboTime+=10;
					break;
				case 3:
					comboTime=999;
					break;
			}
		}else {
			comboTime = 4f;
		}
		if (count >= 2) {

			ActionIndicator.setAction( this );
			Badges.validateMasteryCombo( count );

			GLog.p( Messages.get(this, "combo", count) );
			
		}

		BuffIndicator.refreshHero(); //击中时刷新可视buff。 refresh the buff visually on-hit

	}

	public void addTime( float time ){
		comboTime += time;
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public boolean act() {
		comboTime-=TICK;
		spend(TICK);
		if (comboTime <= 0) {
			detach();
		}
		return true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", count, dispTurns(comboTime));
	}

	private static final String COUNT = "count";
	private static final String TIME  = "combotime";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(TIME, comboTime);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = bundle.getInt( COUNT );
		comboTime = bundle.getFloat( TIME );


		if (count >= 2) ActionIndicator.setAction(this);
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action_name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.COMBO;
	}
	@Override
	public Visual secondaryVisual() {
		BitmapText txt = new BitmapText(PixelScene.pixelFont);
		txt.text( Integer.toString(count) );
		txt.hardlight(CharSprite.POSITIVE);
		txt.measure();
		return txt;
	}

	@Override
	public int indicatorColor() {

		if (count >= 10)    return 0xFF0000;
		else if (count >= 8)return 0xFFCC00;
		else if (count >= 6)return 0xFFFF00;
		else if (count >= 4)return 0xCCFF00;
		else if (count >= 2)return 0x00FF00;
		else return 0xDFDFDF;
	}

	@Override
	public void doAction() {
		GameScene.show(new WndCombo(this));
	}

	public enum ComboMove {
		CRUSH ,SLAM ,VOLLEY ,DEMISE,CALM;
		public String desc(int count){
			switch (this){
				default:
					return Messages.get(this, name()+"_desc");
				case SLAM:
					return Messages.get(this, name()+"_desc", count*10);
				case CRUSH:
					return Messages.get(this, name()+"_desc", count*25);
				case VOLLEY:
					return Messages.get(this, name()+"_desc", count);
				case DEMISE:
					return Messages.get(this, name()+"_desc", count*10);
				case CALM:
					String desc=Messages.get(this, name()+"_desc", count);
					int point=Dungeon.hero.pointsInTalent(Talent.VENT_NOPLACE);
					if(point>=2){
						desc+=Messages.get(this, name()+"_else", count*(point-1)*0.25f,(point-1)*0.25f);
					}
					return desc+"。";
			}
		}
	}

	public int getComboCount(){
		return count;
	}
	public void useMove(ComboMove move){

			moveBeingUsed = move;
			GameScene.selectCell(listener);

	}

	private static ComboMove moveBeingUsed;



	private void doCalm(){
		Hero hero = (Hero) target;
		target.HP = Math.min(target.HT, target.HP + Math.round(count*0.01f* (target.HT-target.HP)));
		Buff.affect(hero, ArtifactRecharge.class).set(count*(Dungeon.hero.pointsInTalent(Talent.VENT_NOPLACE)-1)*0.25f);
		//Post-attack behaviour

		detach();
		ActionIndicator.clearAction(Combo.this);
		hero.spendAndNext(Actor.TICK);
		if (hero.hasTalent(Talent.LETHAL_DEFENSE) && hero.buff(BrokenSeal.WarriorShield.class) != null) {
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			shield.recoverShield(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3f));
		}
	}
	private void doCrush(final Char enemy){
		Hero hero = (Hero) target;
		float dmgMulti = 0.25f * count;
		int dmgBonus=0;
		if (hero.attack(enemy, dmgMulti, dmgBonus, Char.INFINITE_ACCURACY)){
			//special on-hit effects
			WandOfBlastWave.BlastWave.blast(enemy.pos);
			PathFinder.buildDistanceMap(target.pos, BArray.not(Dungeon.level.solid, null), 3);
			for (Char ch : Actor.chars()) {
				if (ch != enemy && ch.alignment == Char.Alignment.ENEMY
						&& PathFinder.distance[ch.pos] < Integer.MAX_VALUE) {
					int aoeHit = Math.round(target.damageRoll() * 0.25f * count);
					aoeHit /= 2;
					aoeHit -= ch.drRoll();
					if (ch.buff(Vulnerable.class) != null) aoeHit *= 1.33f;
					ch.damage(aoeHit, target);
					ch.sprite.bloodBurstA(target.sprite.center(), aoeHit);
					ch.sprite.flash();
				}
			}

			}
		Invisibility.dispel();
		//Post-attack behaviour
		detach();
		ActionIndicator.clearAction(Combo.this);
		hero.spendAndNext(hero.attackDelay());

		if (hero.hasTalent(Talent.LETHAL_DEFENSE) && hero.buff(BrokenSeal.WarriorShield.class) != null) {
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			shield.recoverShield(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3f));
		}
	}
	private void doDemise(final Char enemy){
		Hero hero = (Hero) target;
		float dmgMulti =0.1f*count;
		int dmgBonus = 0;
		int hp=enemy.HP;
		if(target.attack(enemy, dmgMulti, dmgBonus, Char.INFINITE_ACCURACY)){
			if(enemy.isAlive()){
				Buff.affect( enemy, Bleeding.class ).
						set(hp-enemy.HP);
			}
		}
		Invisibility.dispel();
		//Post-attack behaviour
		detach();
		ActionIndicator.clearAction(Combo.this);
		hero.spendAndNext(hero.attackDelay());

		if (hero.hasTalent(Talent.LETHAL_DEFENSE) && hero.buff(BrokenSeal.WarriorShield.class) != null) {
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			shield.recoverShield(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3f));
		}
	}
	private void doSlam(final Char enemy,int dis){
		boolean wasAlly = enemy.alignment == target.alignment;
		Hero hero = (Hero) target;
		float dmgMulti = 1f;
		int dmgBonus = Math.round(target.drRoll() * count / 5f);
		if(dis!=1){
			dmgBonus*=(1+(dis-1)*0.5f);
		}
		switch (dis){
			default:break;
			case 1:dmgBonus*=1.5;
		}
		target.attack(enemy, dmgMulti, dmgBonus, Char.INFINITE_ACCURACY);
		Invisibility.dispel();
		//Post-attack behaviour
		detach();
		ActionIndicator.clearAction(Combo.this);
		hero.spendAndNext(hero.attackDelay());

		if (hero.hasTalent(Talent.LETHAL_DEFENSE) && hero.buff(BrokenSeal.WarriorShield.class) != null) {
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			shield.recoverShield(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3f));
		}
	}
	private void doVolley(final Char enemy){

		Hero hero = (Hero) target;
		ArrayList<MissileWeapon> missiles = hero.belongings.getAllItems(MissileWeapon.class);
		ArrayList<MissileWeapon> list = new ArrayList<>(missiles);
		for(MissileWeapon m :list){
			for (int i=1;i<m.quantity();i++){
				missiles.add(m);
			}
		}
		hero.busy();
		Random.shuffle(missiles);
		Buff.affect(hero, VolleyTracker.class, 0f);
		castMissile(missiles,hero,enemy);
		if (hero.hasTalent(Talent.LETHAL_DEFENSE) && hero.buff(BrokenSeal.WarriorShield.class) != null) {
			BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
			shield.recoverShield(Math.round(shield.maxShield() * hero.pointsInTalent(Talent.LETHAL_DEFENSE) / 3f));
		}
	}
	public static class VolleyTracker extends FlavourBuff{};
	Actor volleyActor = null;
	private void castMissile( ArrayList<MissileWeapon> missiles, Hero hero, Char enemy){
		MissileWeapon cur = missiles.remove(0);
		hero.sprite.zap(enemy.pos);
		hero.busy();
		float startTime = Game.timeTotal;
		((MissileSprite) hero.sprite.parent.recycle(MissileSprite.class)).
				reset(hero.sprite,
						enemy.sprite,
						cur,
						new Callback() {
							@Override
							public void call() {
								cur.throwSound();
								Item i = cur.detach(hero.belongings.backpack);
								boolean wasAlly = enemy.alignment == target.alignment;
								if (i != null) i.Throw(hero,enemy.pos);
								if (Game.timeTotal - startTime < 0.33f){
									hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
										@Override
										protected void onComplete() {
											afterCast( missiles, hero, enemy,wasAlly);
										}
									});
								} else {
									afterCast(missiles, hero, enemy,wasAlly);
								}
							}
						});
	}
	private void afterCast(  ArrayList<MissileWeapon> missiles, Hero hero, Char enemy,boolean wasAlly){
		if (volleyActor != null){
			volleyActor.next();
			volleyActor = null;
		}
		count--;
		boolean enemyDie=!enemy.isAlive() || (!wasAlly && enemy.alignment == target.alignment);
		if (!missiles.isEmpty() &&!enemyDie&&hero.isAlive()&&count!=0) {
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO-1;
				}

				@Override
				protected boolean act() {
					volleyActor = this;
					castMissile(missiles, hero, enemy);
					Actor.remove(this);
					return false;
				}
			});
			hero.next();
		} else {
			if (hero.buff(WildMagic.WildMagicTracker.class) != null) {
				hero.buff(WildMagic.WildMagicTracker.class).detach();
			}
			detach();
			ActionIndicator.clearAction(Combo.this);
			hero.spendAndNext(1f/ RingOfFuror.attackSpeedMultiplier(hero));
			Item.updateQuickslot();
		}
	}
	private CellSelector.Listener listener = new CellSelector.Listener() {

		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			boolean couldAttak=(enemy != null
					&& enemy != target
					&&Dungeon.level.heroFOV[cell]
					&&!target.isCharmedBy( enemy ));
			switch (moveBeingUsed){
				case DEMISE:
					if(couldAttak &&((Hero)target).canAttack(enemy)){
					Dungeon.hero.busy();
					target.sprite.attack(cell, new Callback() {
						@Override
						public void call() {
							doDemise(enemy);
						}
					});
				}else {
					GLog.w(Messages.get(Combo.class, "bad_target"));
				}
					break;
					case CRUSH:
						if(couldAttak&&((Hero)target).canAttack(enemy)){
							Dungeon.hero.busy();
							target.sprite.attack(cell, new Callback() {
								@Override
								public void call() {
									doCrush(enemy);
								}
							});
						}else {
							GLog.w(Messages.get(Combo.class, "bad_target"));
						}
						break;
					case SLAM:
						if(couldAttak&&Dungeon.level.distance(target.pos, enemy.pos)<=4){
							Ballistica c = new Ballistica(target.pos, enemy.pos, Ballistica.PROJECTILE);
							if (c.collisionPos == enemy.pos){
								final int leapPos = c.path.get(c.dist-1);
								if (!Dungeon.level.passable[leapPos]){
									GLog.w(Messages.get(Combo.class, "bad_target"));
								} else {
									Dungeon.hero.busy();
									target.sprite.jump(target.pos, leapPos, new Callback() {
										@Override
										public void call() {
											target.move(leapPos);
											Dungeon.level.occupyCell(target);
											Dungeon.observe();
											GameScene.updateFog();
											target.sprite.attack(cell, new Callback() {
												@Override
												public void call() {
													doSlam(enemy,Dungeon.level.distance(target.pos, enemy.pos));
												}
											});
										}
									});
								}
							} else {
								GLog.w(Messages.get(Combo.class, "bad_target"));
							}
						}else {
							GLog.w(Messages.get(Combo.class, "bad_target"));
						}
						break;
				case VOLLEY:
					if(enemy != null && enemy != target){
						Dungeon.hero.busy();
						target.sprite.attack(cell, new Callback() {
							@Override
							public void call() {
								doVolley(enemy);
							}
						});
					}else {
						GLog.w(Messages.get(Combo.class, "bad_target"));
					}
					break;
				case CALM:
					if(enemy==target){
						Dungeon.hero.busy();
						target.sprite.operate(cell);
						doCalm();
					}else {
						GLog.w(Messages.get(Combo.class, "bad_target"));
					}
					break;
			}
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "prompt");
		}
	};
}

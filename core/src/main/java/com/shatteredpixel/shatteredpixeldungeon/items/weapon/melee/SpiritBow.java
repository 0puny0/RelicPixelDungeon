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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WarFever;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class SpiritBow extends MeleeWeapon {

	
	{image = ItemSpriteSheet.SPIRIT_BOW;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch=1f;
        DMG=0.75f;
		ACC=0.75f;
		hasSkill=true;
        defaultAction = AC_WEAPONSKILL;
		usesTargeting = true;
		unique = true;
		bones = false;
	}

	public static final String AC_SWITCH	= "SWITCH";
	public boolean sniperSpecial = false;
	public float sniperSpecialBonusDamage = 0f;
	public int pursuitType=0;


	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if(hero.subClass == HeroSubClass.SNIPER){
			actions.add(AC_SWITCH);
		}
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		
		super.execute(hero, action);
		
		if (action.equals(AC_WEAPONSKILL)) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( shooter );
		}
		if(action.equals(AC_SWITCH)){
			onSwitch(hero);
		}
	}
	private void onSwitch(Hero hero){
		GLog.p(Messages.get(this,"switch"));
		hero.sprite.operate( hero.pos );
		if(pursuitType==2){
			pursuitType=0;
		}else {
			pursuitType++;
		}
	}
	private static Class[] harmfulPlants = new Class[]{
			Blindweed.class, Firebloom.class, Icecap.class, Sorrowmoss.class,  Stormvine.class
	};

	@Override
	public int proc(Char attacker, Char defender, int damage) {

		if (attacker.buff(NaturesPower.naturesPowerTracker.class) != null && !sniperSpecial){

			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {

					if (Random.Int(12) < ((Hero)attacker).pointsInTalent(Talent.NATURES_WRATH)){
						Plant plant = (Plant) Reflection.newInstance(Random.element(harmfulPlants));
						plant.pos = defender.pos;
						plant.activate( defender.isAlive() ? defender : null );
					}

					if (!defender.isAlive()){
						NaturesPower.naturesPowerTracker tracker = attacker.buff(NaturesPower.naturesPowerTracker.class);
						if (tracker != null){
							tracker.extend(((Hero) attacker).pointsInTalent(Talent.WILD_MOMENTUM));
						}
					}

					Actor.remove(this);
					return true;
				}
			});

		}

		return super.proc(attacker, defender, damage);
	}

	@Override
	public int STRReq(int lvl) {
		return STRReq(1, lvl); //tier 1
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockArrow().targetingPos(user, dst);
	}


	private int targetPos;



	@Override
	public String statsInfo() {
		SpiritArrow arrow=new SpiritArrow(isEquipped(Dungeon.hero));
		arrow.updateControGlove();
		String info=Messages.get(this, "stats_desc",arrow.min(),arrow.max());
		if(Dungeon.hero.subClass == HeroSubClass.SNIPER){
			if(pursuitType==0){
				info+="\n\n"+Messages.get(this,"rapid_fire");
			}else if (pursuitType==1){
				info+="\n\n"+Messages.get(this,"continuous_fire");
			}else {
				info+="\n\n"+Messages.get(this,"strong_fire");
			}
		}
		return  info;

	}
	@Override
	protected float speedMultiplier(Char owner) {
		float speed = super.speedMultiplier(owner);
		if (owner.buff(NaturesPower.naturesPowerTracker.class) != null){
			// +33% speed to +50% speed, depending on talent points
			speed += ((8 + ((Hero)owner).pointsInTalent(Talent.GROWING_POWER)) / 24f);
		}
		return speed;
	}

	@Override
	public int level() {
		int level = Dungeon.hero == null ? 0 : Dungeon.hero.lvl/5;
		if (curseInfusionBonus) level += 1 + level/6;
		return level;
	}

	@Override
	public int buffedLvl() {
		int lvl=level();
		if (Dungeon.hero.buff(WarFever.class) != null&&this ==Dungeon.hero.belongings.weapon()){
			lvl+=Dungeon.hero.buff(WarFever.class).level;
		}
		//level isn't affected by buffs/debuffs
		return lvl;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public SpiritArrow knockArrow(){
		return new SpiritArrow(isEquipped(Dungeon.hero));
	}
	private static final String PURSUITTYPE = "pursuittype";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( PURSUITTYPE, pursuitType );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pursuitType=bundle.getInt(PURSUITTYPE);
	}

	public class SpiritArrow extends MissileWeapon {
		
		{
			image = ItemSpriteSheet.SPIRIT_ARROW;

			hitSound = Assets.Sounds.HIT_ARROW;
		}
		public boolean isHand;
		public SpiritArrow(boolean isHand){
			this.isHand=isHand;
		}
		@Override
		public Emitter emitter() {
			if (Dungeon.hero.buff(NaturesPower.naturesPowerTracker.class) != null && !sniperSpecial){
				Emitter e = new Emitter();
				e.pos(5, 5);
				e.fillTarget = false;
				e.pour(LeafParticle.GENERAL, 0.01f);
				return e;
			} else {
				return super.emitter();
			}
		}
		@Override
		protected float baseDelay(Char owner) {
			 float delay=super.baseDelay(owner);
			if (sniperSpecial){
				switch (pursuitType){
					case 0: default:
						return 0f;
					case 1:
						return 1f;
					case 2:
						return 2f;
				}
			}
			return delay;
		}

		@Override
		public int min() {
			int dmg;
			Hero hero=Dungeon.hero;
			dmg = 1 + hero.lvl/4
					+ RingOfSharpshooting.levelDamageBonus(hero)
					+ (curseInfusionBonus ? 1 + hero.lvl/30 : 0);
			if (hero.buff(WarFever.class) != null&&isHand){
				dmg+=Dungeon.hero.buff(WarFever.class).level;
			}
			if (sniperSpecial){
				dmg = Math.round(dmg* (1f + sniperSpecialBonusDamage));
				switch (pursuitType){
					case 0:default:
						dmg = Math.round(dmg * 0.75f);
						break;
					case 1:
						dmg = Math.round(dmg * 0.667f);
						break;
					case 2:
						//as distance increases so does damage, capping at 2.5x:
						//1.20x|1.35x|1.52x|1.71x|1.92x|2.16x|2.43x|2.74x|3.00x
						int distance = Dungeon.level.distance(curUser.pos, targetPos) - 1;
						float multiplier = Math.min(3f,  (float)Math.pow(1.125f, distance));
						dmg= Math.round(dmg * multiplier);
						break;
				}
			}else {
				switch (SpiritBow.this.form){
					case FORM0:default:
						break;
					case FORM1:
						dmg=Math.round(dmg * 0.75f);
						break;
					case FORM2:
						dmg=Math.round(dmg*1.5f);
				}
			}
			return Math.max(0, dmg);
		}

		@Override
		public int max() {
			Hero hero=Dungeon.hero;
			float dmg =6 + (int)(hero.lvl/2f)
					+ 2*RingOfSharpshooting.levelDamageBonus(hero)
					+ (curseInfusionBonus ? 2 + hero.lvl/15 : 0);
			if (hero.buff(WarFever.class) != null&&isHand){
				dmg+=2*Dungeon.hero.buff(WarFever.class).level;
			}
			if (sniperSpecial){
				dmg = Math.round(dmg* (1f + sniperSpecialBonusDamage));


				switch (pursuitType){
					case 0:default:
						dmg = Math.round(dmg * 0.75f);
						break;
					case 1:
						dmg = Math.round(dmg * 0.6f);
						break;
					case 2:
						//as distance increases so does damage, capping at 3x:
						//1.20x|1.35x|1.52x|1.71x|1.92x|2.16x|2.43x|2.74x|3.00x
						int distance = Dungeon.level.distance(curUser.pos, targetPos) - 1;
						float multiplier = Math.min(3f, (float)Math.pow(1.125f, distance));
						dmg= Math.round(dmg * multiplier);
						break;
				}
			}else {
				switch (SpiritBow.this.form){
					case FORM0:default:
						break;
					case FORM1:
						dmg=Math.round(dmg * 0.75f);
						break;
					case FORM2:
						dmg=Math.round(dmg*1.5f);
				}
			}

			if(hero.hasTalent(Talent.CONCENTRATE_SHOOT) &&isHand){
				dmg*=1.5f;
			}
			return Math.max(0, (int)dmg);
		}


		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return SpiritBow.this.hasEnchant(type, owner);
		}
		
		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return SpiritBow.this.proc(attacker, defender, damage);
		}
		private void blink(int cell) {
			if (!ScrollOfTeleportation.teleportToLocation(curUser, cell)){
				Dungeon.level.drop(this, cell).sprite.drop();
			}
		}
		@Override
		public float accuracyFactor(Char owner, Char target) {
			if (sniperSpecial && SpiritBow.this.pursuitType ==2){
				return Float.POSITIVE_INFINITY;
			} else {
				return super.accuracyFactor(owner, target);
			}
		}
		
		@Override
		public int STRReq(int lvl) {
			return SpiritBow.this.STRReq(lvl);
		}

		@Override
		protected void onThrow( int cell ) {

			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser) {
				if ( Dungeon.level.map[cell] == Terrain.DOOR){
					Level.set(cell, Terrain.OPEN_DOOR);
					GameScene.updateMap(cell);
				}
				parent = null;
				Splash.at( cell, 0xCC99FFFF, 1 );
			} else {
				if (!curUser.shoot( enemy, this )) {
					Splash.at(cell, 0xCC99FFFF, 1);
				}else {
					if (SpiritBow.this.form==Form.FORM2){
						ArrayList<Integer> blinkPoints = new ArrayList<>();
						for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
							int p = cell + PathFinder.NEIGHBOURS8[i];
							if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
								blinkPoints.add( p );
							}
						}
						if(blinkPoints.size()<=0){;
							return;
						}
						Random.shuffle(blinkPoints);
						blink(blinkPoints.get(0));
					}
				}

				if (sniperSpecial && SpiritBow.this.pursuitType != 1) sniperSpecial = false;
			}
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
		}

		int flurryCount = -1;
		Actor flurryActor = null;

		@Override
		public void cast(final Hero user, final int dst) {

			if(user.pointsInTalent(Talent.CONCENTRATE_SHOOT)==2&&isHand){
				Buff.affect(user, Talent.ConcentrateShootTracker.class, 0f);
			}
			final int cell = throwPos( user, dst );
			SpiritBow.this.targetPos = cell;
			if (sniperSpecial && SpiritBow.this.pursuitType==1){
				if (flurryCount == -1) flurryCount = 3;
				
				final Char enemy = Actor.findChar( cell );
				
				if (enemy == null){
					user.spendAndNext(castDelay(user, dst));
					sniperSpecial = false;
					flurryCount = -1;

					if (flurryActor != null){
						flurryActor.next();
						flurryActor = null;
					}
					return;
				}
				QuickSlotButton.target(enemy);
				
				final boolean last = flurryCount == 1;
				
				user.busy();
				
				throwSound();
				
				((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
						reset(user.sprite,
								cell,
								this,
								new Callback() {
									@Override
									public void call() {
										if (enemy.isAlive()) {
											curUser = user;
											onThrow(cell);
										}
										
										if (last) {
											user.spendAndNext(castDelay(user, dst));
											sniperSpecial = false;
											flurryCount = -1;
										}

										if (flurryActor != null){
											flurryActor.next();
											flurryActor = null;
										}
									}
								});
				
				user.sprite.zap(cell, new Callback() {
					@Override
					public void call() {
						flurryCount--;
						if (flurryCount > 0){
							Actor.add(new Actor() {

								{
									actPriority = VFX_PRIO-1;
								}

								@Override
								protected boolean act() {
									flurryActor = this;
									int target = QuickSlotButton.autoAim(enemy, SpiritArrow.this);
									if (target == -1) target = cell;
									cast(user, target);
									Actor.remove(this);
									return false;
								}
							});
							curUser.next();
						}
					}
				});
				
			} else {

				if (user.hasTalent(Talent.SEER_SHOT)
						&& user.buff(Talent.SeerShotCooldown.class) == null){
					int shotPos = throwPos(user, dst);
					if (Actor.findChar(shotPos) == null) {
						RevealedArea a = Buff.affect(user, RevealedArea.class, 5 * user.pointsInTalent(Talent.SEER_SHOT));
						a.depth = Dungeon.depth;
						a.pos = shotPos;
						Buff.affect(user, Talent.SeerShotCooldown.class, 20f);
					}
				}

				super.cast(user, dst);
			}
		}
	}
	
	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				knockArrow().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(SpiritBow.class, "prompt");
		}
	};
}

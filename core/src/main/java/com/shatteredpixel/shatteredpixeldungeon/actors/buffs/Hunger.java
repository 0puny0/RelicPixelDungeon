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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.KnifeFork;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Hunger extends Buff implements Hero.Doom {

	private static final float STEP	= 10f;
	public static final int WELLFED	=450;
	public static final int JUST 		=300;
	public static final int HUNGRY	= 100;
	public static final int STARVING	= 0;

	private float level=300f;
	private float partialDamage;

	private static final String LEVEL			= "level";
	private static final String PARTIALDAMAGE 	= "partialDamage";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}

	@Override
	public boolean act() {
		float stepValue=Dungeon.isChallenged(Challenges.NO_FOOD)?STEP*1.5f:STEP;
		float stepTime=STEP;
		if (Dungeon.level.locked
				|| target.buff(WellFed.class) != null
				|| SPDSettings.intro()
				|| target.buff(ScrollOfChallenge.ChallengeArena.class) != null){
			spend(stepTime);
			return true;
		}
		if (target.isAlive() && target instanceof Hero) {

			Hero hero = (Hero)target;
			if (isStarving()) {

				partialDamage += stepValue * target.HT/1000f;

				if (partialDamage > 1){
					target.damage( (int)partialDamage, this);
					partialDamage -= (int)partialDamage;
				}
				
			} else {
				float newLevel = level - stepValue;
				if (newLevel <= STARVING) {

					GLog.n( Messages.get(this, "onstarving") );
					hero.resting = false;
					hero.damage( 1, this );

					hero.interrupt();

				} else if (newLevel <= HUNGRY && level > HUNGRY) {

					GLog.w( Messages.get(this, "onhungry") );

					if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_FOOD)){
						GLog.p(Messages.get(Guidebook.class, "hint"));
						GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_FOOD);
					}

				}else if(newLevel<=WELLFED&&level>WELLFED){
					GLog.p( Messages.get(this, "onwellfed") );
				}
				level = newLevel;

			}
			//结算频率
			if(isStuffed()){
				stepTime*=0.67f;
			}
			if(target.buff( Shadows.class )!=null){
				stepTime*=1.5f;
			}
			if(target.buff(Terraforming.RelaxVigilance.class)!=null&&Dungeon.hero.pointsInTalent(Talent.TEMPORARY_REST)>1){
				stepTime*=1.25f;
			}
			spend(stepTime );

		} else {

			diactivate();

		}

		return true;
	}

	public void satisfy( float foodVal ) {
		Hero hero=(Hero)target;
		float energy = foodVal;
		Artifact.ArtifactBuff buff = hero.buff( HornOfPlenty.hornRecharge.class );
		if (buff != null && buff.isCursed()){
			energy *= 0.67f;
			GLog.n( Messages.get(Hunger.class, "cursedhorn") );
		}
		KindOfWeapon w=hero.belongings.weapon();
		if(w!=null&&w instanceof KnifeFork){
			float val=energy;
			energy*=((KnifeFork) w).satisfyMultiplier();
			if(energy<val){
				hero.HP = Math.min(hero.HT, hero.HP +Math.round(val-energy)/(Dungeon.isChallenged(Challenges.NO_FOOD)?6:4) );
			}
		}
		affectHunger( energy, true );
	}

	public void affectHunger(float energy ){
		affectHunger( energy, false);
	}

	public void affectHunger(float energy, boolean overrideLimits ) {

		if (energy < 0 && target.buff(WellFed.class) != null){
			target.buff(WellFed.class).left -= energy;
			BuffIndicator.refreshHero();
			return;
		}

		level += energy;
		if (level > 450f && !overrideLimits) {
			level = 450f;
		} else if (level < STARVING) {
			float excess = STARVING-level;
			level = STARVING;
			partialDamage += excess * (target.HT/1000f);
		}

		BuffIndicator.refreshHero();
	}

	public boolean isStarving() {
		return level <= STARVING;
	}
	public boolean isHungry(){
		return level <= HUNGRY&&level>STARVING;
	}
	public boolean isJust(){
		return level<=JUST&&level>HUNGRY;
	}
	public boolean isWellFed(){
		return level<=WELLFED&&level>JUST;
	}
	public boolean isStuffed(){
		return level>WELLFED;
	}

	public int hunger() {
		return (int)Math.ceil(level);
	}

	@Override
	public int icon() {
		if (isJust()) {
			return BuffIndicator.NONE;
		} else if (isHungry()) {
			return BuffIndicator.HUNGER;
		} else if (isStarving()){
			return BuffIndicator.STARVATION;
		}else if(isWellFed()){
			return BuffIndicator.WELL_FED;
		}else {
			return BuffIndicator.STUFFED;
		}
	}

	@Override
	public String name() {
		 if (isHungry()) {
			return Messages.get(this, "hungry");
		} else if (isStarving()){
			return Messages.get(this, "starving");
		}else if(isWellFed()){
			return Messages.get(this, "wellfed");
		}else {
			return Messages.get(this, "stuffed");
		}
	}

	@Override
	public String desc() {
		String result;
		if (isHungry()) {
			result = Messages.get(this, "desc_intro_hungry");
		} else if (isStarving()){
			result = Messages.get(this, "desc_intro_starving");
		}else if(isWellFed()){
			result = Messages.get(this, "desc_intro_wellfed");
		}else {
			result = Messages.get(this, "desc_intro_stuffed");
		}

		return result;
	}

	@Override
	public void onDeath() {

		Badges.validateDeathFromHunger();

		Dungeon.fail( getClass() );
		GLog.n( Messages.get(this, "ondeath") );
	}
}

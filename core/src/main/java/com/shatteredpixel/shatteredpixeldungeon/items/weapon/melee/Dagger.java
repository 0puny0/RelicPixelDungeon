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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Dagger extends MeleeWeapon {
	
	{	image = ItemSpriteSheet.DAGGER;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch=1.1f;
        DMG=0.75f;
	}
	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.enemy();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				float dmgeMulti;
				switch (form){
					case FORM0: default:
						dmgeMulti=2f;
						break;
					case FORM1:
						dmgeMulti=1.5f;
						break;
					case FORM2:
						dmgeMulti=3f;
						break;
				}
				int damage = Math.round(Random.IntRange(min() , max())*dmgeMulti);
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage +=  (int)(exStr * RingOfForce.extraStrengthBonus(hero ));
				}
				return damage;
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if(form==Form.FORM1&&defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)){
			Buff.affect(defender, SilentProc.class);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public void switchForm(Form toForm) {
		super.switchForm(toForm);
		modeSwitch();
	}
	public void modeSwitch() {
		switch (form){
			case FORM0:
				DMG=0.75f;
				break;
			case FORM1:
				DMG=0.75f;
				break;
			case FORM2:
				DMG=0.5f;
				break;
		}
	}
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		modeSwitch();
	}
	public static class SilentProc extends FlavourBuff {
		{
			actPriority =MOB_PRIO+1;
		}
		@Override
		public boolean act() {
			if(target instanceof Hero){
				Buff.affect(target, Invisibility.class,3f);
			}
			detach();
			return true;
		}
	}
}

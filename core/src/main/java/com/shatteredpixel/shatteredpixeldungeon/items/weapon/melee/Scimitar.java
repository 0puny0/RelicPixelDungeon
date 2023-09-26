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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Scimitar extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SCIMITAR;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch=1.1f;
        DMG=0.75f;
		DLY=0.75f;
		ACC=1.5f;
    }
	int enemiesSlain=0;
	public void modeSwitch() {
		switch (form){
			case FORM0:
				DLY=0.75f;
				ACC=1.5f;
				break;
			case FORM1:
				DLY=1f;
				ACC=1.5f;
				break;
			case FORM2:
				DLY=1f;
				ACC=1f;
				break;
		}
	}

	@Override
	public int min() {
		int	min=super.min();
		if(form==Form.FORM1){
			min+=enemiesSlain;
			min =Math.min(min,max());
		}
		return min;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (form==Form.FORM1&&defender.HP <= damage ){
			Buff.affect(defender, GrowProc.class).SetScimitar(this);
		}
		if(form==Form.FORM2){
			Buff.prolong(attacker, Momentum.class, Dungeon.hero.attackDelay()).addTier();
		}
		return super.proc(attacker, defender, damage);
	}
	@Override
	public float delayFactor( Char owner ) {
		float delay=super.delayFactor(owner);
		if(Dungeon.hero.buff(Momentum.class)!=null){
			delay*=(1f-0.15f*Dungeon.hero.buff(Momentum.class).tier);
		}
		return delay;
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		float accuracy=super.accuracyFactor(owner, target);
		if(Dungeon.hero.buff(Momentum.class)!=null){
			accuracy*=(1f+0.2f*Dungeon.hero.buff(Momentum.class).tier);
		}
		return accuracy;
	}

	@Override
	public void switchForm(Form toForm) {
		super.switchForm(toForm);
		modeSwitch();
	}

	private static final String ENEMIESSLAIN	= "enemiesslain";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( ENEMIESSLAIN,enemiesSlain );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		enemiesSlain=bundle.getInt(ENEMIESSLAIN);
		modeSwitch();
	}
	public static class GrowProc extends Buff {
		Scimitar s;
		@Override
		public boolean act() {
			detach();
			return true;
		}
		public void SetScimitar(Scimitar scimitar){
			s=scimitar;
		}
		public void Grow(){
			if(s!=null){
				s.enemiesSlain++;
			}
		}
	}
	public static class Momentum extends FlavourBuff {
		int tier;
		@Override
		public int icon() {
			return BuffIndicator.SCIMITAR;
		}
		@Override
		public void tintIcon(Image icon) {
			switch (tier){
				case 1:
					icon.hardlight(0f, 1f, 0f);
					break;
				case 2:
					icon.hardlight(1f, 1f, 0f);
					break;
				case 3:
				default:
					icon.hardlight(1f, 0f, 0f);
					break;
			}

		}
		@Override
		public float iconFadePercent() {
			return 0;
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc",tier*20,tier*15);
		}

		public void addTier(){
			tier++;
			if (tier>3)tier=3;
		}
		public int getTier(){
			return tier;
		}
		private static final String TIER	    = "tier";
		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( TIER, tier );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			tier = bundle.getInt( TIER);
		}
	}
}

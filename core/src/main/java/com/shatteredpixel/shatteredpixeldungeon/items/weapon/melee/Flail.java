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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class Flail extends MeleeWeapon {

	{
		image = ItemSpriteSheet.FLAIL;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch=1f;
        DMG=1.4f;
		ACC=0.5f;
		hasSkill=true;
		defaultAction=AC_WEAPONSKILL;
    }

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if(action.equals(AC_WEAPONSKILL)){
			if (!isEquipped(hero)){
				GLog.i( Messages.get(MeleeWeapon.class, "need_to_equip") );
				return;
			}
			if(hero.buff(BattleStance.class)==null){
				onWAVE(hero);
			}else {
				onSTOP(hero);
			}

		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		onSTOP(hero);
		return super.doUnequip(hero, collect, single);
	}

	protected  void onWAVE(Hero hero ){
		Buff.affect(hero,BattleStance.class);
		hero.sprite.operate( hero.pos );
		hero.spendAndNext( 1f );
	}
	protected void onSTOP(Hero hero){
		if(hero.buff(BattleStance.class)!=null){
			hero.buff(BattleStance.class).detach();
			hero.sprite.operate( hero.pos );
		}
	}
	@Override
	public float accuracyFactor(Char owner, Char target) {
		float accuracy=super.accuracyFactor(owner, target);
		if(owner.buff(BattleStance.class)!=null){
			accuracy*=1.5f;
		}
		return accuracy;
	}

	public static class BattleStance extends Buff{
		private Hunger targetHunger;
		@Override
		public boolean act() {
			if(targetHunger==null){
				targetHunger=target.buff(Hunger.class);
			}
			targetHunger.affectHunger(-4);
			spend(TICK);
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.BATTLE_STANCE;
		}

	}
}

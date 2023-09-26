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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SwordShield extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SWORDSHIELD;
		hitSound = Assets.Sounds.HIT_SLASH;
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
			onBLOCK(hero);
		}
	}
	public void onBLOCK(Hero hero){

		Buff.affect(hero,Block.class,Actor.TICK).SetDef(0,(tier+1)+(tier+1)*buffedLvl()/2);
		Invisibility.dispel();
		curUser.spendAndNext( 1f );
	}
	public static class Block extends FlavourBuff{
		int minDef;
		int maxDef;
		public int Def;
		public void SetDef(int minDef,int maxDef){
			this.minDef=minDef;
			this.maxDef=maxDef;
			Def= Random.IntRange(minDef, maxDef);
		}
	}

	public static class RiposteTracker extends Buff {
		{ actPriority = VFX_PRIO;}

		public Char enemy;

		@Override
		public boolean act() {
			target.sprite.attack(enemy.pos, new Callback() {
				@Override
				public void call() {
					GLog.p(Messages.get(SwordShield.class, "back"));
					target.attack(enemy,0.5f,0,100000);
					next();
				}
			});
			detach();
			return false;
		}
	}
	@Override
	public String statsInfo() {
		if (isIdentified()){
			return  Messages.get(this, "stats_desc",0,(tier+1)+(tier+1)*buffedLvl()/2);
		}else {
			return  Messages.get(this, "typical_stats_desc",0,2);
		}
	}
}

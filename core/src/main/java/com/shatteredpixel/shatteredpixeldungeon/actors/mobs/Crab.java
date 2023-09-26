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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Crab extends Mob {

	{
		spriteClass = CrabSprite.class;
		
		HP = HT = 13;
		defenseSkill = 5;
		baseSpeed = 2f;
		
		EXP = 4;
		maxLvl = 9;
		
		loot = new MysteryMeat();
		lootChance = 0.167f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 6 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(1, 5);
	}
	@Override
	public void rollToDropLoot() {
		Hero hero= Dungeon.hero;
		if(hero.hasTalent(Talent.ANATOMY_EXPERT)){
			int meatAvailable = 1 + 2*hero.pointsInTalent(Talent.ANATOMY_EXPERT);
			Talent.AutopsyMeatDropped dropped = Buff.affect(hero, Talent.AutopsyMeatDropped.class);
			meatAvailable -= dropped.count();
			if(meatAvailable>0){
				dropped.countUp(1);
				Dungeon.level.drop(new MysteryMeat(), pos).sprite.drop();
				GLog.i(Integer.toString(meatAvailable));
			}
		}
		super.rollToDropLoot();
	}
}

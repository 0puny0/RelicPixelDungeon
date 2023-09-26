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

import static com.shatteredpixel.shatteredpixeldungeon.actors.Char.INFINITE_ACCURACY;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Aim;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class WarHammer extends MeleeWeapon {

	{
		image = ItemSpriteSheet.WAR_HAMMER;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch=0.8f;
        DMG=1.4f;
		ACC=0.75f;
		DLY=1.5f;
	}

	@Override
	public int damageRoll(Char owner) {
		if(owner instanceof Hero&&owner.buff(Aim.class)!=null){
			int diff = max() - min();
			int damage = Random.NormalIntRange(min()+ Math.round(diff*0.5f) , max());
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage +=   (int)(exStr * RingOfForce.extraStrengthBonus((Hero)owner ));
			}
			return damage;
		}else {
			return super.damageRoll(owner);
		}
	}
	@Override
	public float accuracyFactor(Char owner, Char target) {
		float accuracy=super.accuracyFactor(owner, target);
		if(owner.buff(Aim.class)!=null){
			accuracy=INFINITE_ACCURACY;
		}
		return accuracy;
	}

}

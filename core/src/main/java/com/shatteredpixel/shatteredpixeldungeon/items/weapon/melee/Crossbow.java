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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Crossbow extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.CROSSBOW;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch=1.1f;
        DMG=0.75f;
	}

	public int getDartMax(){
		if(form==Form.FORM2||form==Form.FORM2){
			return 2*(1+tier)+tier*buffedLvl()*2/3;
		}
		return 3*(1+tier)+tier*buffedLvl();
	}
	public int getDartMin(){
		return 1+tier+buffedLvl();
	}
	@Override
	public String statsInfo() {
		if(isIdentified()){
			return  Messages.get(this, "stats_desc",getDartMin(),getDartMax());
		}else {
			return  Messages.get(this, "typical_stats_desc",1+tier,3*(1+tier));
		}

	}
}

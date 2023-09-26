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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LongSword extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.LONG_SWORD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch=1f;

    }
	public boolean isCastGold(){
		return form==Form.FORM1&&Dungeon.gold>=Dungeon.depth/5*10;
	}
	public int discount(){
		return 85-LevelAppreciation()*15;
	}
	public float valueMulti(){
		return LevelAppreciation()*1.5f;
	}
	@Override
	public int damageRoll(Char owner) {
		int	dmg=super.damageRoll(owner);
		if(isCastGold()){
			dmg=(int) Math.floor(dmg*1.25f);
		}
		return dmg;
	}
	@Override
	public float accuracyFactor(Char owner, Char target) {
		float accuracy=super.accuracyFactor(owner, target);
		if(isCastGold()){
			accuracy*=1.25f;
		}
		return accuracy;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if(isCastGold()){
			CellEmitter.center( defender.pos ).burst( Speck.factory( Speck.COIN ), 4 );
			Dungeon.gold -= Dungeon.depth/5*10;
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String statsInfo() {
		if(isIdentified()){
			return  Messages.get(this, "stats_desc",discount());
		}else {
			return  Messages.get(this, "typical_stats_desc",85);
		}

	}
}

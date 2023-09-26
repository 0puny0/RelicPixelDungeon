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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class GreatAxe extends MeleeWeapon {

	{
		image = ItemSpriteSheet.GREAT_AXE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch=0.85f;
        DMG=1.4f;
        defaultAction = AC_EQUIP;
	}

	@Override
	public int min(int lvl) {
		return max(lvl)/3;
	}

	@Override
	public boolean doEquip(Hero hero) {
		GLog.w(Messages.get(GreatAxe.class, "equip") );
		defaultAction=AC_UNEQUIP;
		hero.sprite.operate( hero.pos );
		return super.doEquip(hero);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		GLog.p(Messages.get(GreatAxe.class, "unequip") );
		defaultAction=AC_EQUIP;
		hero.sprite.operate( hero.pos );
		return super.doUnequip(hero, collect, single);
	}
}

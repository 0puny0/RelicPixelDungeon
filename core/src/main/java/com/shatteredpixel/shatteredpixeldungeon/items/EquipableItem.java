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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlessingPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class EquipableItem extends Item {

	public static final String AC_EQUIP		= "EQUIP";
	public static final String AC_UNEQUIP	= "UNEQUIP";

	protected static final String AC_DETACH       = "DETACH";
	{
		bones = true;
	}
	public enum Inlay {
		noThing,
		brokenSeal,
		blessedPeal,
		blessingPeal,
	}

	public Inlay inlay = Inlay.noThing;
	public BlessedPearl pearl;
	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EQUIP );
		if (inlay != Inlay.noThing&&inlay!=Inlay.blessingPeal) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (super.doPickUp(hero, pos)){
			if (!isIdentified() && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_IDING)){
				GLog.p(Messages.get(Guidebook.class, "hint"));
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_IDING);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EQUIP )) {
			//In addition to equipping itself, item reassigns itself to the quickslot
			//This is a special case as the item is being removed from inventory, but is staying with the hero.
			int slot = Dungeon.quickslot.getSlot( this );
			doEquip(hero);
			if (slot != -1) {
				Dungeon.quickslot.setSlot( slot, this );
				updateQuickslot();
			}
		} else if (action.equals( AC_UNEQUIP )) {
			doUnequip( hero, true );
		}
		if(action.equals(AC_DETACH)&&inlay==Inlay.blessedPeal){

			GameScene.show(new WndOptions(new ItemSprite(new  BlessedPearl()),
					Messages.get(BlessedPearl.class, "name"),
					Messages.get(BlessedPearl.class, "detach_intro"),
					Messages.get(BlessedPearl.class,"confirm_detach"),
					Messages.get(BlessedPearl.class,"cancel")){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						GLog.n( Messages.get(EquipableItem.class, "detach_pearl") );
						detachPearl(hero);
						hero.sprite.operate(hero.pos);
						Sample.INSTANCE.play( Assets.Sounds.UNLOCK);
					}
					updateQuickslot();
				}

				@Override
				public void onBackPressed() {
					//do nothing
				}
			});

		}
	}
	public   void  detachPearl(Hero hero){
		if(isEquipped(hero)){
			doUnequip(hero,false);
		}else {
			detach( hero.belongings.backpack );
			onDetach();
			Dungeon.quickslot.clearItem(this);
			updateQuickslot();
			hero.spend(1f);
		}
		inlay=Inlay.noThing;
		if(this instanceof MeleeWeapon){
			BlessingPower.setBlessWeapon(null);
		}
		BlessedPearl newPearl=pearl;
		pearl=null;
		if (!newPearl.collect()){
			Dungeon.level.drop(newPearl, hero.pos);
		}
		BlessingPower.setBlessedEquip(false);
	}
	public void affixPearl(BlessedPearl pearl){
		this.pearl = pearl;
		inlay=Inlay.brokenSeal;
		level(trueLevel()+(Dungeon.hero.hasTalent(Talent.UPGRADE_TRANSFER)?pearl.level():1));

		inlay= EquipableItem.Inlay.blessedPeal;
		Badges.validateItemLevelAquired(this);
		pearl.detach(Dungeon.hero.belongings.backpack);
		if (isEquipped(Dungeon.hero)){
			BlessingPower.setBlessedEquip(true);
		}
		if(this instanceof MeleeWeapon){
			BlessingPower.setBlessWeapon((MeleeWeapon) this);
		}
	}


	@Override
	public boolean isUpgradable() {
		if(inlay==Inlay.blessedPeal&&pearl!=null){
			return pearl.isUpgradable();
		}else {
			return super.isUpgradable();
		}
	}

	@Override
	public Item upgrade() {
		if(inlay==Inlay.blessedPeal&&pearl!=null&&pearl.isUpgradable()){
			pearl.upgrade();
		}
		return super.upgrade();
	}
	@Override
	public Emitter emitter() {
		if (inlay==Inlay.blessedPeal&&pearl!=null) {
			Emitter emitter = new Emitter();
			emitter.pos(ItemSpriteSheet.film.width(image)/2f, ItemSpriteSheet.film.height(image)/2f);
			emitter.fillTarget = false;
			emitter.pour(Speck.factory( Speck.BLESSED_LIGHT ), 0.8f);
			return emitter;
		}else if(inlay==Inlay.blessingPeal){
			Emitter emitter = new Emitter();
			emitter.fillTarget = true;
			emitter.pour(Speck.factory( Speck.YELLOW_LIGHT ), 0.4f);
			return emitter;
		}
		return super.emitter();
	}

	@Override
	public void doDrop( Hero hero ) {
		if (!isEquipped( hero ) || doUnequip( hero, false, false )) {
			super.doDrop( hero );
		}
	}
	@Override
	public void cast( final Hero user, int dst ) {

		if (isEquipped( user )) {
			if (quantity == 1 && !this.doUnequip( user, false, false )) {
				return;
			}
		}

		super.cast( user, dst );
	}

	public static void equipCursed( Hero hero ) {
		hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
		Sample.INSTANCE.play( Assets.Sounds.CURSED );
	}

	protected float time2equip( Hero hero ) {
		return 1;
	}

	public abstract boolean doEquip( Hero hero );

	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {

		if (cursed && hero.buff(MagicImmune.class) == null) {
			GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
			return false;
		}

		if (single) {
			hero.spendAndNext( time2equip( hero ) );
		} else {
			hero.spend( time2equip( hero ) );
		}

		//temporarily keep this item so it can be collected
		boolean wasKept = keptThoughLostInvent;
		keptThoughLostInvent = true;
		if (!collect || !collect( hero.belongings.backpack )) {
			onDetach();
			Dungeon.quickslot.clearItem(this);
			updateQuickslot();
			if (collect) Dungeon.level.drop( this, hero.pos );
		}
		keptThoughLostInvent = wasKept;
		return true;
	}

	final public boolean doUnequip( Hero hero, boolean collect ) {
		return doUnequip( hero, collect, true );
	}

	public void activate( Char ch ){}
	private static final String INLAY = "inlay";
	private static final String PEARL="pearl";
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(INLAY, inlay);
		bundle.put(PEARL,pearl);
	}
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		inlay= bundle.getEnum(INLAY, Inlay.class);
		pearl=(BlessedPearl) bundle.get(PEARL);
		if(this instanceof MeleeWeapon){
			if(inlay==Inlay.blessedPeal){
				BlessingPower.setBlessWeapon((MeleeWeapon) this);
			}
			if(inlay==Inlay.blessingPeal){
				BlessingPower.setHolyWeapon((MeleeWeapon) this);
			}
		}
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlessingPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WarFever;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MeleeWeapon extends Weapon {
	{

	}
	public  boolean hasSkill=false;
	public static final String AC_BREAKTHROW		="BREAKTHROW";
	public static final String AC_WEAPONSKILL		="WEAPONSKILL";
	public static final String AC_SWITCHWEAPON		="SWITCHWEAPON";
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);

		if(hasSkill){
			actions.add(AC_WEAPONSKILL);
			defaultAction=AC_WEAPONSKILL;
		}else {
			defaultAction=null;
		}

		BlessingPower bp=Dungeon.hero.buff(BlessingPower.class);
		if (bp!=null&&bp.twinTakeEffect() &&Dungeon.hero.pointsInTalent(Talent.TWIN_SACRED_OBJECTS)==3
		&&(inlay==Inlay.blessingPeal||inlay==Inlay.blessedPeal)){
			actions.add(AC_SWITCHWEAPON);
			defaultAction=AC_SWITCHWEAPON;
			if (isEquipped(hero)){
				if(hasSkill){
					defaultAction=AC_WEAPONSKILL;
				}else {
					defaultAction="";
				}
			}
		}

//		if (RingOfForce.breakMeleeDamageBonus(Dungeon.hero)>0) {
//			actions.add(AC_BREAKTHROW);
//		}else if(RingOfForce.breakMeleeDamageBonus(Dungeon.hero)<0&&actions.contains(AC_BREAKTHROW)) {
//			actions.remove(AC_BREAKTHROW);
//		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);
		if(action.equals(AC_SWITCHWEAPON)){
			onSwitchWeapon(hero);
			Dungeon.hero.sprite.operate(Dungeon.hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
		}
		if (action.equals(AC_BREAKTHROW)) {
			if (Dungeon.hero.belongings.weapon()==this){
				GLog.i( Messages.get(MeleeWeapon.class, "break_equip") );
				return;
			}
			onBreakThrow(hero);
		}
	}
	int LevelAppreciation(){
		return (int)(Math.sqrt(8 * buffedLvl() + 1) - 1)/2;
	}
	void  onSwitchWeapon(Hero
								 hero){
		KindOfWeapon temp = hero.belongings.weapon;
		if(temp==null||(temp.inlay!=Inlay.blessedPeal&&temp.inlay!=Inlay.blessingPeal)){
			GLog.w(Messages.get(MeleeWeapon.class, "need_twin"));
			return ;
		}
		if(this==temp){
			return;
		}
		hero.belongings.weapon =this;
		for (Item item : hero.belongings.backpack.items) {
			if (item == this) {
				hero.belongings.backpack.items.remove(this);
				updateQuickslot();
				break;
			}
		}
		temp.collect(hero.belongings.backpack );
		if(this instanceof MeleeWeapon){
			if(inlay==Inlay.blessedPeal){
				BlessingPower.setBlessedEquip(true);
				BlessingPower.setBlessingEquip(false);
			}
			if(inlay==Inlay.blessingPeal){
				BlessingPower.setBlessingEquip(true);
				BlessingPower.setBlessedEquip(false);
			}
		}
		this.actions(hero);
		temp.actions(hero);
		activate( hero );
		ActionIndicator.refresh();

	}
	protected  void onBreakThrow (Hero hero){
		GameScene.selectCell(breakThrower);
	}
	//执行扔入操作逻辑
	protected void onBreakThrow( int cell ) {
		Char enemy = Actor.findChar( cell );
		//如果对空格或对英雄扔出，将物品扔到目标格
		if (enemy == null || enemy == curUser) {
			Sample.INSTANCE.play(Assets.Sounds.SHATTER , 1, 0.8f);
		} else {
			curUser.shoot( enemy, this );
		}
	}
	protected static CellSelector.Listener breakThrower = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				Char enemy = Actor.findChar( target );
				if(enemy==null|| enemy == curUser){
					GLog.i( Messages.get(MeleeWeapon.class, "break_to_null"));
					return;
				}
				((MeleeWeapon)curItem).breakCast( curUser, target );
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};
	public void breakCast( final Hero user, final int dst ){

		final int cell = throwPos( user, dst );
		user.sprite.zap( cell );
		user.busy();

		throwSound();

		Char enemy = Actor.findChar( cell );
		QuickSlotButton.target(enemy);

		if (enemy != null) {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							enemy.sprite,
							this,
							new Callback() {
								@Override
								public void call() {
									curUser = user;
									((MeleeWeapon)(MeleeWeapon.this.detach(user.belongings.backpack))).onBreakThrow(cell);
									user.spendAndNext(1);
								}
							});
		} else {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							cell,
							this,
							new Callback() {
								@Override
								public void call() {
									curUser = user;
									((MeleeWeapon)(MeleeWeapon.this.detach(user.belongings.backpack))).onBreakThrow(cell);
									user.spendAndNext(1);
								}
							});
		}
	}

	@Override
	public int min(int lvl) {
		if(DMG==0.5){
			return (int)( (tier+lvl)*(1+0.1f*tier));
		}
		return tier+lvl;
	}

	@Override
	public int max(int lvl) {
		if(DMG>1){
			return  (int) Math.floor((tier+1)*(6+lvl)*DMG);
		}else if(DMG<1){
			return  (int) Math.ceil((tier+1)*(6+lvl)*DMG);
		} else {
			return  (int) Math.round((tier+1)*(6+lvl)*DMG);
		}
	}

	@Override
	public int level() {
		int lvl=0;
		if(Dungeon.hero.pointsInTalent(Talent.TWIN_SACRED_OBJECTS)>1
				&&BlessingPower.holyWeapon!=null
				&&inlay==Inlay.blessedPeal&&pearl!=null){
			lvl=BlessingPower.holyWeapon.level()+pearl.level();
		}
			return super.level()>lvl?super.level():lvl;
	}

	@Override
	public int buffedLvl() {
		int lvl =super.buffedLvl();
		if (Dungeon.hero.buff(WarFever.class) != null&&this ==Dungeon.hero.belongings.weapon()){
			lvl+=Dungeon.hero.buff(WarFever.class).level;
		}
		if(Dungeon.hero.buff(BlessingPower.Blessing.class)!=null&&inlay==Inlay.blessedPeal){
			lvl+=Dungeon.hero.buff(BlessingPower.Blessing.class).extraLevel();
		}
		return lvl;
	}

	@Override
	public Item random() {
		tier=Random.IntRange(2,5);
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);
		//25% chance to be cursed
		//10% chance to be enchanted
		float effectRoll = Random.Float();
		if (effectRoll < 0.25f) {
			getCurse(true);
		}  else if (effectRoll >= 0.9f){
			enchant();
		}
		return super.random();
	}
	@Override
	public void getCurse(boolean extraEffect){
		if(extraEffect==true){
			if(enchantment!=null){
				enchant(Enchantment.randomCurse(enchantment.getClass()));
			}else {
				enchant(Enchantment.randomCurse());
			}
		}
		super.getCurse( extraEffect);
	}
	@Override
	public boolean removeCurse(boolean extraEffect){
		curseInfusionBonus=false;
		if(hasCurseEnchant()&&extraEffect==true){
			enchant(null);
		}
		return super.removeCurse(extraEffect);
	}

	@Override
	public boolean haveGain() {
		if(hasGoodEnchant()){
			return true;
		}else {
			return super.haveGain();
		}
	}
	@Override
	public boolean haveLose(){
		if(hasCurseEnchant()){
			return  true;
		}else {
			return super.haveLose();
		}
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = Random.NormalIntRange( min(), max() );
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += DLY<=0.5f?(int)(exStr*RingOfForce.halfExStrBonus(owner)):(int)(exStr * RingOfForce.extraStrengthBonus(owner ));
			}
		}
		return damage;
	}
	public String attribute(boolean isManual){
		String atrribute="";
		String dmg,acc,aspd,rch=String.valueOf(RCH);
		if(DMG>=1.4f){
			dmg=Messages.get(MeleeWeapon.class,"highest");
		}else if (DMG==0.75f){
			dmg=Messages.get(MeleeWeapon.class,"lower");
		}else if (DMG<=0.5f){
			dmg=Messages.get(MeleeWeapon.class,"lowest");
		}else {
			dmg=Messages.get(MeleeWeapon.class,"common");
		}
		if (ACC>=1.5f){
			acc=Messages.get(MeleeWeapon.class,"highest");
		}else if (ACC==0.75f){
			acc=Messages.get(MeleeWeapon.class,"lower");
		}else if (ACC<=0.5f){
			acc=Messages.get(MeleeWeapon.class,"lowest");
		}else {
			acc=Messages.get(MeleeWeapon.class,"common");
		}
		if(DLY>=1.5f){
			aspd=Messages.get(MeleeWeapon.class,"lowest");
		}else if (DLY==0.75f){
			aspd=Messages.get(MeleeWeapon.class,"higher");
		}else if (DLY<=0.5f){
			aspd=Messages.get(MeleeWeapon.class,"highest");
		}else {
			aspd=Messages.get(MeleeWeapon.class,"common");
		}
		if(isManual){
			atrribute+=Messages.get(MeleeWeapon.class,"manual_attr",dmg,acc,aspd,rch) ;
		}else {
			atrribute+=Messages.get(MeleeWeapon.class,"attr",dmg,acc,aspd,rch) ;
		}
		return atrribute;
	}
	public String Manual(){
		String info = desc();

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" +statsInfo;
		info += "\n\n" +attribute(true);
		info += "\n\n" +Messages.get(MeleeWeapon.class,"form")+"\n"+Messages.get(this,"form_intro") ;
		return  info;
	}

	@Override
	public String info() {

		String info = desc();
		//武器伤害和力量
		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, min(), max(),STRReq());
			int diffStr=Dungeon.hero.STR()-STRReq();
			if (diffStr<0) {
				info +=Messages.get(Weapon.class, "too_heavy");
			} else if (diffStr>0){
				info += Messages.get(MeleeWeapon.class, "excess_str", DLY<=0.5f?(int)(diffStr*RingOfForce.halfExStrBonus(Dungeon.hero)):
						(int)(diffStr * RingOfForce.extraStrengthBonus(Dungeon.hero )));
			}else if(diffStr==0){
				info +=Messages.get(MeleeWeapon.class, "just_right");
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
			if (STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}
		//武器能力描述
		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" +statsInfo;
		//武器属性
		info += "\n\n" +attribute(false) ;
		//武器特化形态
		switch (form) {
			case FORM1:
				info += "\n\n" +Messages.get(this, "form")+Messages.get(this, "form1");
				break;
			case FORM2:
				info += "\n\n"+Messages.get(this, "form") +Messages.get(this, "form2");
				break;
			case FORM0:
		}
		//武器诅咒附魔情况

//		if(cursed&&cursedKnown ){
//			info += "\n\n" +Messages.get(MeleeWeapon.class, "cursed");
//		}
//		if (enchantment != null && (cursedKnown || !enchantment.curse())){
//			info += "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", enchantment.name()));
//			info += " " + enchantment.desc();
//		}
//
		if(inlay==Inlay.blessingPeal){
			info += "\n\n" + Messages.get(MeleeWeapon.class, "blessing_pearl_attached");
		}
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");

		} else if (!isIdentified() && cursedKnown){
			if (enchantment != null && enchantment.curse()) {
				info += "\n\n" + Messages.get(Weapon.class, "weak_cursed");
			} else {
				info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
			}
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}

	@Override
	public boolean collect() {
		if(!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_MELEE)){
			GLog.p(Messages.get(Guidebook.class, "hint"));
			GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_MELEE);
		}
		return super.collect();
	}

	@Override
	public int value() {
		int price = 20 * tier;
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	private static final String TIER	        = "tier";
	private static final String DEFAULTACTION ="defaultaction";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TIER, tier );
		bundle.put( DEFAULTACTION, defaultAction);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		tier = bundle.getInt(TIER);
		defaultAction = bundle.getString(DEFAULTACTION);
		if(defaultAction==""){
			defaultAction=null;
		}
	}

}

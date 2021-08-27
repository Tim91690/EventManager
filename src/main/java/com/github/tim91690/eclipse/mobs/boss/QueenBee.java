package com.github.tim91690.eclipse.mobs.boss;

import com.github.tim91690.EventManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;


public class QueenBee extends Boss {
    int task;
    int colony;

    public QueenBee(Location loc) {
        super(loc.getWorld().spawnEntity(loc, EntityType.BEE),250, ChatColor.BLUE+""+ChatColor.BOLD+"Queen Bee", BarColor.BLUE);
        this.colony = 0;

        this.entity.setCustomName(this.name);
        this.entity.setCustomNameVisible(true);
        this.entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,2000000,0));

        this.entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);
        this.entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(15);
        this.entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2);
        this.entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);

        summonBee();
        summonBee();
        summonBee();
        summonBee();

        this.task = Bukkit.getScheduler().runTaskTimer(EventManager.getPlugin(),() -> {
            if (this.entity.getHealth() < this.getMaxHealth()) {
                ((Bee) this.entity).setAnger(1000);
                ((Bee) this.entity).setCannotEnterHiveTicks(1000);
                ((Bee) this.entity).setHasStung(false);
                this.entity.getWorld().spawnParticle(Particle.FALLING_HONEY,this.entity.getLocation(),800,10,10,10,0,null,true);
            }
        },0,40).getTaskId();


    }

    @Override
    public void death() {
        bossList.remove(this);
        this.bossbar.removeAll();
        Bukkit.getScheduler().cancelTask(this.task);
    }

    @Override
    public void attack(List<Player> proxPlayer) {
        double distance =10000;
        Player closest = null;
        for (Player p : proxPlayer) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,60,0));
            if (this.entity.getLocation().distance(p.getLocation())<distance) distance = this.entity.getLocation().distance(p.getLocation());
            closest = p;
        }
        if (this.colony < 30) {
            this.colony = this.colony +2;
            if (closest != null) ((Bee)this.entity).setTarget(closest);
            summonBee();
            summonBee();
        }
    }

    private void summonBee() {
        Bee bee = (Bee)this.entity.getWorld().spawnEntity(this.entity.getLocation(),EntityType.BEE);
        bee.setCannotEnterHiveTicks(100000);
        bee.setBaby();
        bee.setAge(-100000);

        int task = Bukkit.getScheduler().runTaskTimer(EventManager.getPlugin(),() -> {
            if (bee.isDead()) this.colony = this.colony -1;
        },0,400).getTaskId();

        Bukkit.getScheduler().runTaskLater(EventManager.getPlugin(),() -> {
            Bukkit.getScheduler().cancelTask(task);
            bee.remove();
            this.colony = this.colony -1;
        },1200);
    }
}

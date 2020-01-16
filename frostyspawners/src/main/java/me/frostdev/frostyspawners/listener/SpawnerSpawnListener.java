package me.frostdev.frostyspawners.listener;

import me.frostdev.frostyspawners.Frostyspawners;
import me.frostdev.frostyspawners.spawners.Spawner;
import me.frostdev.frostyspawners.util.Util;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SpawnerSpawnListener implements Listener {
    private Frostyspawners main;
    private String typename;

    public SpawnerSpawnListener(Frostyspawners as) {
        this.main = as;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onSpawnerSpawner(SpawnerSpawnEvent e) throws IOException {
        if(e.getSpawner().getBlock().getType() != main.items.spawner(1).getType()){
            return;
        }
        Block b = e.getSpawner().getBlock();
        Spawner spawner = this.main.getData().getSpawner(b);
        UUID playerUUID = spawner.getOwner().getUniqueId();
        if (!spawner.isEnabled()) {
            spawner.setDelay(spawner.getDefaultDelay());
            e.getEntity().remove();
        }
        if (!spawner.hasOwner()){
            return;
        }

        typename = Util.toUserFriendlyString(e.getEntityType());
        typename = ChatColor.GOLD + typename;
        e.getEntity().setCustomName(typename + " x" + 1);
        e.getEntity().setCustomNameVisible(true);
        EntityType type = e.getEntityType();
        Location location = b.getLocation();
        if (!spawner.getWorld().getEnvironment().equals(World.Environment.NETHER)){
            if (type == EntityType.WITHER_SKELETON || type == EntityType.BLAZE || type == EntityType.PIG_ZOMBIE || type == EntityType.MAGMA_CUBE){
                e.getEntity().setCustomName(typename + "Drops Null. Nether Restricted.");
                e.getEntity().remove();
            }
        }
        e.getEntity().setMetadata("frosty_count", new FixedMetadataValue(this.main, 1));;
        e.getEntity().setMetadata("frosty_identUUID", new FixedMetadataValue(this.main, playerUUID.toString()));
        e.getEntity().setMetadata("frosty_spawnerloc", new FixedMetadataValue(this.main, location.toString()));
        e.getEntity().setMetadata("frosty_ident", new FixedMetadataValue(this.main, true));
        List<Entity> near = location.getWorld().getEntities();
        new BukkitRunnable() {
            int count = 0;
            double radius = 10;
            int temp = 0;
            int mcheck = 0;
            @Override
            public void run() {
                for (
                        Entity x : near) {
                    if (x.hasMetadata("frosty_ident")) {
                        if (x.getType().equals(type)) {
                            if (x.getLocation().distance(location) <= radius) {
                                if (x.isCustomNameVisible() && x.fromMobSpawner() && x.getMetadata("frosty_identUUID").get(0).asString().equals(playerUUID.toString()) && x.getMetadata("frosty_spawnerloc").get(0).asString().equals(location.toString())) {
                                    if (x.getUniqueId() != e.getEntity().getUniqueId()) {
                                        if (x.getMetadata("frosty_count").get(0).asInt() >= 64) {
                                            e.getEntity().remove();
                                            return;
                                        }
                                        if (x.getMetadata("frosty_spawnerloc").get(0).asString().equals(location.toString())) {
                                            mcheck++;
                                        }
                                        if (mcheck > 1) {
                                            e.getEntity().remove();
                                            return;
                                        }
                                        e.getEntity().remove();
                                        count++;
                                        temp = x.getMetadata("frosty_count").get(0).asInt() + count;
                                        x.setCustomName(typename + " x" + temp);
                                        x.setCustomNameVisible(true);
                                        x.setMetadata("frosty_count", new FixedMetadataValue(main, temp));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(main);




        me.frostdev.frostyspawners.api.event.SpawnerSpawnEvent event = new me.frostdev.frostyspawners.api.event.SpawnerSpawnEvent(spawner, e.getEntity(), e.getLocation());
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.main.getServer().getScheduler().scheduleSyncDelayedTask(this.main, new SpawnerSpawnListener.schedule(spawner), 0L);
        }
    }

    class schedule implements Runnable {
        private Spawner spawner;

        public schedule(Spawner s) {
            this.spawner = s;
        }

        public void run() {
            Random r1 = new Random();
            int defSecDel = this.spawner.getDefaultDelayInSeconds();
            if (defSecDel == 0) {
                defSecDel = 20;
            }

            int a = r1.nextInt(200) / defSecDel;
            boolean b = r1.nextBoolean();
            int defaultDelay = this.spawner.getDefaultDelay();
            if (b) {
                defaultDelay -= a;
            } else {
                defaultDelay += a;
            }

            this.spawner.setDelay(defaultDelay);
            this.spawner.update();
        }
    }
}

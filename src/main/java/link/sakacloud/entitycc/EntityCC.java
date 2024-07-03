package link.sakacloud.entitycc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

public class EntityCC extends JavaPlugin {

    private int maxSilverfish;
    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxSilverfish = getConfig().getInt("max-silverfish");
        logger.info("EntityCC plugin enabled.");

        new BukkitRunnable() {
            @Override
            public void run() {
                checkAndRemoveExcessSilverfish();
            }
        }.runTaskTimer(this, 0L, 600L);
    }

    private void checkAndRemoveExcessSilverfish() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int currentSilverfishCount = getSilverfishCount();
                if (currentSilverfishCount > maxSilverfish) {
                    int excessSilverfish = currentSilverfishCount - maxSilverfish;
                    logger.info("Removing " + excessSilverfish + " excess silverfish.");
                    removeExcessSilverfish(excessSilverfish);
                }
            }
        }.runTask(this);
    }

    private int getSilverfishCount() {
        int count = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.SILVERFISH) {
                    count++;
                }
            }
        }
        return count;
    }

    private void removeExcessSilverfish(int excessSilverfish) {
        List<Entity> silverfishList = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.SILVERFISH) {
                    silverfishList.add(entity);
                }
            }
        }

        silverfishList.sort((e1, e2) -> Integer.compare(e2.getTicksLived(), e1.getTicksLived()));

        for (int i = 0; i < excessSilverfish && i < silverfishList.size(); i++) {
            silverfishList.get(i).remove();
        }

        logger.info("Removed " + Math.min(excessSilverfish, silverfishList.size()) + " silverfish.");
    }
}

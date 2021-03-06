package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.system.protection.ProtectionUtils;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.war.WarSystem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action eventAction = event.getAction();
        Player p = event.getPlayer();
        
        if (eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.LEFT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            
            if (RegionUtils.isIn(clicked.getLocation())) {
                Region region = RegionUtils.getAt(clicked.getLocation());
                Block heart = region.getCenter().getBlock().getRelative(BlockFace.DOWN);
                
                if (clicked.equals(heart)) {
                    Guild g = region.getGuild();
                    
                    if (SecuritySystem.getSecurity().checkPlayer(p, g)) {
                        return;
                    }

                    if (eventAction == Action.LEFT_CLICK_BLOCK) {
                        WarSystem.getInstance().attack(p, g);
                        
                        event.setCancelled(true);
                        return;
                    }
                    
                    else if (eventAction == Action.RIGHT_CLICK_BLOCK) {
                        PluginConfig config = Settings.getConfig();
                        
                        if(config.informationMessageCooldowns.cooldown(p, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                            return;
                        }

                        new ExcInfo().execute(p, new String[]{g.getTag()});
                        
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        
        if (ProtectionUtils.action(eventAction, event.getClickedBlock())) {
            event.setCancelled(false);
        }
    }

}

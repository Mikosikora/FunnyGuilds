package net.dzikoysk.funnyguilds.util.element;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class IndividualPrefix {

    private final User user;

    public IndividualPrefix(User user) {
        this.user = user;
        this.initialize();
        user.setIndividualPrefix(this);
    }

    protected void addPlayer(String player) {
        if (player == null) {
            return;
        }
        
        User user = User.get(player);
        if (!user.hasGuild()) {
            return;
        }
        
        Scoreboard scoreboard = this.getUser().getScoreboard();
        Team team = scoreboard.getEntryTeam(player);
        
        if (team != null) {
            team.removeEntry(player);
        }
        
        team = scoreboard.getTeam(user.getGuild().getTag());
        if (team == null) {
            addGuild(user.getGuild());
            team = scoreboard.getTeam(user.getGuild().getTag());
        }
        
        if (this.getUser().hasGuild()) {
            if (this.getUser().equals(user) || this.getUser().getGuild().getMembers().contains(user)) {
                team.setPrefix(replace(Settings.getConfig().prefixOur, "{TAG}", user.getGuild().getTag()));
            }
        }
        
        team.addEntry(player);
    }

    public void addGuild(Guild to) {
        if (to == null) {
            return;
        }
        
        Scoreboard scoreboard = getUser().getScoreboard();
        Guild guild = getUser().getGuild();
        if (guild != null) {
            if (guild.equals(to)) {
                initialize();
                return;
            }
            
            Team team = scoreboard.getTeam(to.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }
            
            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }
            
            String prefix = Settings.getConfig().prefixOther;
            if (guild.getAllies().contains(to)) {
                prefix = Settings.getConfig().prefixAllies;
            }
            
            if (guild.getEnemies().contains(to)) {
                prefix = Settings.getConfig().prefixEnemies;
            }
            
            team.setPrefix(replace(prefix, "{TAG}", to.getTag()));
        } else {
            Team team = scoreboard.getTeam(to.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }
            
            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }
            
            team.setPrefix(replace(Settings.getConfig().prefixOther, "{TAG}", to.getTag()));
        }
    }

    protected void removePlayer(String player) {
        if (player == null) {
            return;
        }
        
        Team team = getUser().getScoreboard().getEntryTeam(player);
        if (team != null) {
            team.removeEntry(player);
            if (team.getName() != null) {
                team.setPrefix(replace(Settings.getConfig().prefixOther, "{TAG}", team.getName()));
            }
        }
    }

    protected void removeGuild(Guild guild) {
        if (guild == null || guild.getTag() == null || guild.getTag().isEmpty()) {
            return;
        }
        
        Team team = getUser().getScoreboard().getTeam(guild.getTag());
        if (team != null) {
            team.unregister();
        }
    }

    private void initialize() {
        if (getUser() == null) {
            return;
        }
        
        List<Guild> guilds = GuildUtils.getGuilds();
        Scoreboard scoreboard = getUser().getScoreboard();
        Guild guild = getUser().getGuild();
        
        if (guild != null) {
            guilds.remove(guild);
            
            PluginConfig config = Settings.getConfig();
            String our = config.prefixOur;
            String ally = config.prefixAllies;
            String enemy = config.prefixEnemies;
            String other = config.prefixOther;
            Team team = scoreboard.getTeam(guild.getTag());
            
            if (team == null) {
                team = scoreboard.registerNewTeam(guild.getTag());
            }
            
            for (User u : guild.getMembers()) {
                if (u.getName() == null) {
                    continue;
                }
                
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }
            
            team.setPrefix(replace(our, "{TAG}", guild.getTag()));
            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }
                
                team = scoreboard.getTeam(one.getTag());
                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }
                
                for (User u : one.getMembers()) {
                    if (u.getName() == null) {
                        continue;
                    }
                    
                    if (!team.hasEntry(u.getName())) {
                        team.addEntry(u.getName());
                    }
                }
                
                if (guild.getAllies().contains(one)) {
                    team.setPrefix(replace(ally, "{TAG}", one.getTag()));
                } else if (guild.getEnemies().contains(one)) {
                    team.setPrefix(replace(enemy, "{TAG}", one.getTag()));
                } else {
                    team.setPrefix(replace(other, "{TAG}", one.getTag()));
                }
            }
        } else {
            String other = Settings.getConfig().prefixOther;
            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }
                
                Team team = scoreboard.getTeam(one.getTag());
                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }
                
                for (User u : one.getMembers()) {
                    if (u.getName() == null) {
                        continue;
                    }
                    
                    if (!team.hasEntry(u.getName())) {
                        team.addEntry(u.getName());
                    }
                }
                
                team.setPrefix(replace(other, "{TAG}", one.getTag()));
            }
        }
    }

    private String replace(String f, String r, String t) {
        String s = f.replace(r, t);
        
        if (s.length() > 16) {
            s = s.substring(0, 16);
        }
        
        return s;
    }

    public User getUser() {
        return this.user;
    }

    public Scoreboard getScoreboard() {
        return this.user.getScoreboard();
    }
}

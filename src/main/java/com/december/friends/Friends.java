package com.december.friends;

import com.december.friends.commands.FriendCommand;
import com.december.friends.database.MysqlManager;
import com.december.friends.user.User;
import com.december.friends.user.UserManager;
import com.december.friends.user.requests.RequestManager;
import com.december.friends.utils.ConsoleUtils;
import com.december.friends.utils.DataFile;
import com.gitlab.avelyn.core.components.ComponentPlugin;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import java.util.UUID;

import static com.gitlab.avelyn.core.base.Events.listen;

@Getter
public class Friends extends ComponentPlugin {

    public static Gson GSON = new Gson();
    public static Friends instance;
    private DataFile configFile;
    public UserManager userManager;
    private MysqlManager databaseManager;
    public RequestManager requestManager;

    {
        this.onEnable(() -> {
            this.instance = this;
            this.configFile = new DataFile(this, "config", true);
            this.databaseManager = new MysqlManager(this);
            this.userManager = new UserManager(this);
            this.requestManager = new RequestManager(this);
            ConsoleUtils.log("&aFriends Enabled.");
            Objects.requireNonNull(getCommand("friend")).setExecutor(new FriendCommand(this));
        });

        this.onDisable(() -> {
            ConsoleUtils.log("&cFriends Disabled.");
            this.userManager.saveUsers(false);
            this.databaseManager.close();
        });

        /**
         * Check if a player has a user
         * if not create one
         */
        child(listen((AsyncPlayerPreLoginEvent event) -> {
            UUID uuid = event.getUniqueId();
            if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
                if (userManager.getUsers().containsKey(uuid)) return;
                new User(uuid);
            }
        }));

        /**
         * Load the players data
         * (load when join, incase they are coming from another server on the network)
         */
        child(listen((PlayerLoginEvent event) -> {
            val data = userManager.getUser(event.getPlayer());
            if (data == null) return;
            userManager.loadUser(event.getPlayer().getUniqueId());
            if (data.getUsername() == null || !data.getUsername().equalsIgnoreCase(event.getPlayer().getName())) {
                data.setUsername(event.getPlayer().getName());
            }

        }));

        /**
         * Save the players data on quit.
         */
        child(listen((PlayerQuitEvent event) -> {
            Player player = event.getPlayer();
            userManager.saveUser(userManager.getUser(player.getUniqueId()), true);
        }));



    }

    public static Friends getInstance() {
        return instance;
    }
}

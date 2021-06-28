package com.december.friends.user;

import com.december.friends.Friends;
import com.december.friends.user.requests.Request;
import com.december.friends.utils.ConsoleUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

@Getter
public class UserManager {

    private Type FRIEND = new TypeToken<Friend>() {}.getType();
    private Type REQUEST = new TypeToken<Request>() {}.getType();
    private Type SENT = new TypeToken<Request>() {}.getType();

    private final Friends plugin;
    private HashMap<UUID, User> users;

    public UserManager(Friends plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
    }


    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public User getUser(UUID uuid) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            return createNewUser(uuid);
        }
    }


    public void loadUser(UUID uuid) {

        ExecutorService service = Executors.newSingleThreadExecutor();

        service.submit(() -> {
            try {
                CachedRowSet set = plugin.getDatabaseManager().performQuery("SELECT * FROM users WHERE `uuid` = ?;", uuid.toString());
                if (set.next()) {

                    User user = users.get(uuid);
                    user.setUsername(set.getString("username"));
                    user.setFriends(Friends.GSON.fromJson(set.getString("friends"), FRIEND));
                    user.setFriendRequestsReceived(Friends.GSON.fromJson(set.getString("received"), REQUEST));
                    user.setFriendRequestsSent(Friends.GSON.fromJson(set.getString("sent"), REQUEST));
                    return;
                } else {
                    ConsoleUtils.log("&cNo profile found. Creating new user...");
                    createNewUser(uuid);
                }
            } catch (SQLException e) {
                service.shutdown();
                System.out.println("Error performing SQL query: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                e.printStackTrace();
            }
            try {
                service.awaitTermination(25, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Error terminating query async pool: " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
            }
        });
    }

    public void saveUsers() {
        saveUsers(true);
    }

    public void saveUsers(boolean async) {
        for (User user : users.values()) {
            saveUser(user, async);
        }
    }

    public void saveUser(User user, boolean async) {
        String query = "UPDATE users" +
                " SET `username` = ?, `friends` = ?, `received` = ?, `sent` = ? WHERE uuid = ?;";
        if (async) {
            plugin.getDatabaseManager().performAsyncUpdate(query,
                    user.getUsername(),
                    Friends.GSON.toJson(user.getFriends(), FRIEND),
                    Friends.GSON.toJson(user.getFriendRequestsReceived(), REQUEST),
                    Friends.GSON.toJson(user.getFriendRequestsSent(), FRIEND),
                    user.getUuid());
        } else {
            plugin.getDatabaseManager().performUpdate(query,
                    user.getUsername(),
                    Friends.GSON.toJson(user.getFriends(), FRIEND),
                    Friends.GSON.toJson(user.getFriendRequestsReceived(), REQUEST),
                    Friends.GSON.toJson(user.getFriendRequestsSent(), FRIEND),
                    user.getUuid());
        }
    }

    public User createNewUser(UUID uuid) {
        User user = users.get(uuid);

        plugin.getDatabaseManager().performAsyncUpdate("INSERT INTO `users`(`uuid`,`username`)" +
                        "VALUES(?,?);",
                user.getUuid(),
                user.getUsername());
        return user;
    }

    public HashMap<UUID, User> getUsers() {
        return users;
    }

    public Set<UUID> getUserIds() {
        return getUsers().keySet();
    }


}

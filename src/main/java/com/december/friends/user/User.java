package com.december.friends.user;

import com.december.friends.Friends;
import com.december.friends.user.requests.Request;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class User {

    private String uuid;
    private String username;
    public List<Friend> friends;
    public List<Request> friendRequestsReceived;
    public List<Request> friendRequestsSent;

    public User(UUID uuid) {
        this.uuid = String.valueOf(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            this.username = player.getName();
        this.friends = new ArrayList<>();
        this.friendRequestsReceived = new ArrayList<>();
        this.friendRequestsSent = new ArrayList<>();
        Friends.getInstance().getUserManager().getUsers().put(uuid, this);
    }

    public boolean isFriend(Player player) {
        List<UUID> tempList = new ArrayList<>();
        if (friends == null) return false;
        friends.stream().forEach(friend -> {
            tempList.add(friend.getUuid());
        });
        if (tempList.contains(player.getUniqueId())) {
            return true;
        } else {
            return false;
        }
    }



    public boolean hasFriendRequest(Player player) {
        List<UUID> tempList = new ArrayList<>();
        if (friendRequestsReceived == null) return false;
        friendRequestsReceived.stream().forEach(request -> {
            tempList.add(request.getTargetUUID());
        });
        if (tempList.contains(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    public boolean hasSentRequest(Player player) {
        List<UUID> tempList = new ArrayList<>();
        if (friendRequestsSent == null) return false;
        friendRequestsSent.stream().forEach(request -> {
            tempList.add(request.getTargetUUID());
        });
        if (tempList.contains(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    public boolean removeReceivedRequest(Player player) {
        friendRequestsReceived.stream().forEach(request -> {
            if (request.getRequestedUUID().equals(player.getUniqueId())) {
                friendRequestsReceived.remove(request);
            }
        });
        return false;
    }

    public boolean removeSentRequest(Player player) {
        friendRequestsSent.stream().forEach(request -> {
            if (request.getTargetUUID().equals(player.getUniqueId())) {
                friendRequestsSent.remove(request);
            }
        });
        return false;
    }
}

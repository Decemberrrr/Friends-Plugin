package com.december.friends.user.requests;

import com.december.friends.Friends;
import lombok.val;
import org.bukkit.entity.Player;

public class RequestManager {
    private final Friends plugin;

    public RequestManager(Friends plugin) {
        this.plugin = plugin;
    }

    public void createNewInvite(Player player, Player target) {
        Request request = new Request(player.getUniqueId(), target.getUniqueId());
        val senderData = plugin.getUserManager().getUser(player);
        val targetData = plugin.getUserManager().getUser(target);
        senderData.getFriendRequestsReceived().add(request);
        targetData.getFriendRequestsSent().add(request);
    }


}

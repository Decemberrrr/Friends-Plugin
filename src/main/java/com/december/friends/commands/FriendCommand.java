package com.december.friends.commands;


import com.december.friends.Friends;
import com.december.friends.user.Friend;
import com.december.friends.user.User;
import com.december.friends.user.requests.Request;
import com.december.friends.utils.Color;
import com.december.friends.utils.TimeUtil;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * I had to use the old method of creating commands
 * due to me needing to update my engine as I forgot to implement
 * multiple arguments properly
 */
public class FriendCommand implements CommandExecutor {

    private final Friends plugin;

    public FriendCommand(Friends plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {

            return true;
        }
        Player player = (Player) sender;

        val data = plugin.getUserManager().getUser(player);
        User targetData;
        Player target;
        if (args.length < 1) {
            player.sendMessage("Please use /friend <add/remove/list> <user>");
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add":
                if (args.length < 2) {
                    sender.sendMessage("&cPlease use /friend add <user>");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("This player is not online currently.");
                    return false;
                }
                targetData = plugin.getUserManager().getUser(target);
                System.out.println(targetData + " Friend command data");
                if (data.isFriend(target)) {
                    player.sendMessage("This player is already your friend.");
                    return false;
                }

                if (data.hasSentRequest(target)) {
                    player.sendMessage("You have already sent a friend request to this player.");
                    return false;
                }

                if (data.hasFriendRequest(target)) {
                    Friend senderFriend = new Friend(player.getUniqueId(), player.getName(), System.currentTimeMillis());
                    Friend targetFriend = new Friend(target.getUniqueId(), target.getName(), System.currentTimeMillis());
                    player.sendMessage("You have accepted " + targetFriend.getUserName() + "'s friend request.");
                    target.sendMessage(player.getName() + " has accepted your friend request.");
                    data.removeReceivedRequest(target);
                    targetData.removeSentRequest(player);
                    data.getFriends().add(targetFriend);
                    targetData.getFriends().add(senderFriend);
                    return true;
                }

                System.out.println(target.getName());
                System.out.println(player.getName());

                Request request = new Request(player.getUniqueId(), target.getUniqueId());
                System.out.println(data.getFriendRequestsReceived());
                List<Request> tempList = new ArrayList<>();
                tempList.add(request);
                Friends.getInstance().getUserManager().getUser(player).getFriendRequestsReceived().add(request);
                targetData.getFriendRequestsReceived().add(request);
                player.sendMessage("You have sent a friend request.");
                target.sendMessage("You have received a friend request.");

                //  Friend friend = new Friend(target.getUniqueId(), target.getName(), System.currentTimeMillis());
                break;
            case "list":
                if (data.getFriends().isEmpty()) {
                    player.sendMessage(Color.translate("&cYou have no friends."));
                    return false;
                }
                player.sendMessage(Color.translate("&e&m-----------------------"));
                player.sendMessage(Color.translate("&e&lFriends:"));
                data.getFriends().stream().forEach(friend -> {
                    player.sendMessage(Color.translate(" &8> &f" + friend.getUserName() + " 8| &7Friends for " + TimeUtil.getDurationBreakdown(System.currentTimeMillis() - friend.getAddedAt())));
                });
                player.sendMessage(Color.translate("&eRequests: &7(&6&l" + data.getFriendRequestsReceived().size() + "&7)"));
                player.sendMessage(Color.translate("&e&m-----------------------"));
                break;
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage("&cPlease use /friend remove <user>");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("This player is not online currently.");
                    return false;
                }
                targetData = plugin.getUserManager().getUser(target);
                if (!data.isFriend(target)) {
                    player.sendMessage("This player is not your friend.");
                    return false;
                }
                data.removeReceivedRequest(target);
                targetData.removeSentRequest(player);
                player.sendMessage(Color.translate("&cYou have deleted " + target.getName() + " as a friend."));
                player.sendMessage(Color.translate("&c" + player.getName() + "has deleted you as a friend."));
                break;
            default:
                sender.sendMessage("/friend <add/list/remove> <player>");
                break;
        }

        return true;
    }



}

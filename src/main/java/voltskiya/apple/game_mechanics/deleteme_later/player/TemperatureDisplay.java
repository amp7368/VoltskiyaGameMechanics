package voltskiya.apple.game_mechanics.deleteme_later.player;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class TemperatureDisplay {
    public static void display(Player player, double temp) {
        TextComponent message = new TextComponent();
        message.setText(String.format("Temperature: %.0f", temp));
        player.sendActionBar(message);
    }
}

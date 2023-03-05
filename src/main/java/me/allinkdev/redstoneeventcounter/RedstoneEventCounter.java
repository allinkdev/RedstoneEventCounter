package me.allinkdev.redstoneeventcounter;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;

public final class RedstoneEventCounter extends JavaPlugin implements Listener {
    private static final String FORMAT_STRING = "BlockRedstoneEvent was executed (approximately %d ticks since execution, %d executions this tick)";
    private Instant lastRedstoneEvent = Instant.MIN;
    private int executionsPerTick = 0;

    @Override
    public void onEnable() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        lastRedstoneEvent = Instant.MIN;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onTickEnd(final ServerTickEndEvent event) {
        executionsPerTick = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBlockRedstone(final BlockRedstoneEvent event) {
        executionsPerTick++;

        final Instant now = Instant.now();

        if (this.lastRedstoneEvent == Instant.MIN) {
            this.lastRedstoneEvent = now;
            return;
        }

        final Duration duration = Duration.between(lastRedstoneEvent, now);
        final long tickDuration = calculateTickDifference(duration);
        final String formatted = String.format(FORMAT_STRING, tickDuration, executionsPerTick);

        broadcastMessage(formatted);
        this.lastRedstoneEvent = now;
    }

    private long calculateTickDifference(final Duration duration) {
        final long ms = duration.toMillis();

        return ms / 50L;
    }

    private void broadcastMessage(final String message) {
        final Component component = Component.text("[RedstoneEventCounter] ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(message, NamedTextColor.LIGHT_PURPLE));

        Bukkit.broadcast(component);
    }
}

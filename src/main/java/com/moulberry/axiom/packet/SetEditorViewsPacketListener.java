package com.moulberry.axiom.packet;

import com.moulberry.axiom.AxiomConstants;
import com.moulberry.axiom.View;
import com.moulberry.axiom.persistence.UUIDDataType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SetEditorViewsPacketListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!player.hasPermission("axiom.*")) {
            return;
        }

        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(message));
        UUID uuid = friendlyByteBuf.readUUID();
        List<View> views = friendlyByteBuf.readList(View::read);

        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(AxiomConstants.ACTIVE_VIEW, UUIDDataType.INSTANCE, uuid);

        PersistentDataContainer[] containerArray = new PersistentDataContainer[views.size()];
        for (int i = 0; i < views.size(); i++) {
            PersistentDataContainer viewContainer = container.getAdapterContext().newPersistentDataContainer();
            views.get(i).save(viewContainer);
            containerArray[i] = viewContainer;
        }
        container.set(AxiomConstants.VIEWS, PersistentDataType.TAG_CONTAINER_ARRAY, containerArray);
    }

}

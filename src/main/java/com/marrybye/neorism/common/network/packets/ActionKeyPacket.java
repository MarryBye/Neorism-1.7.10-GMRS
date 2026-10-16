package com.marrybye.neorism.common.network.packets;

import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Sent from client to server when an action hotkey is pressed.
 */
public class ActionKeyPacket implements IMessage {

    private int slot; // 0 = primary, 1 = secondary, -1 = custom
    private String customActionId;

    public ActionKeyPacket() {
    }

    public ActionKeyPacket(int slot) {
        this.slot = slot;
        this.customActionId = "";
    }

    public ActionKeyPacket(String customActionId) {
        this.slot = -1;
        this.customActionId = customActionId != null ? customActionId : "";
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeUTF8String(buf, customActionId != null ? customActionId : "");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slot = buf.readInt();
        this.customActionId = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<ActionKeyPacket, IMessage> {
        @Override
        public IMessage onMessage(ActionKeyPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            ActionHandler ah = ActionHandler.get(player);
            SkillHandler sh = SkillHandler.get(player);
            if (ah == null || sh == null) return null;

            String actionId = message.customActionId;
            if (message.slot == 0) {
                actionId = sh.getPrimaryAction();
            } else if (message.slot == 1) {
                actionId = sh.getSecondaryAction();
            }

            if (actionId != null && !actionId.isEmpty()) {
                ah.executeAction(actionId);
            }

            return null;
        }
    }
}

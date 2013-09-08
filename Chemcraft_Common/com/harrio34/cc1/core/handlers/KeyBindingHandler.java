package com.harrio34.cc1.core.handlers;

import java.util.EnumSet;

import com.harrio34.cc1.lib.Reference;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class KeyBindingHandler extends KeyHandler 
{

	 public KeyBindingHandler() {

	        super(KeyBindingUtil.gatherKeyBindings(), KeyBindingUtil.gatherIsRepeating());
	    }

	    @Override
	    public String getLabel() {

	        return Reference.MOD_NAME + ": " + this.getClass().getSimpleName();
	    }

	    @Override
	    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {

	        // Only operate at the end of the tick
	        if (tickEnd) {
	            // If we are not in a GUI of any kind, continue execution
	            if (FMLClientHandler.instance().getClient().inGameHasFocus) {
	                EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
	                if (player != null) {
	                    ItemStack currentItem = FMLClientHandler.instance().getClient().thePlayer.getCurrentEquippedItem();

	                    if (currentItem != null) {
	                        if (currentItem.getItem() instanceof IKeyBound) {
	                            if (!KeyBindingUtil.isClientSided(kb.keyDescription)) {
	                                PacketDispatcher.sendPacketToServer(PacketTypeHandler.populatePacket(new PacketKeyPressed(kb.keyDescription)));
	                            }
	                            else {
	                                ((IKeyBound) currentItem.getItem()).doKeyBindingAction(player, currentItem, kb.keyDescription);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }

	    @Override
	    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {

	    }

	    @Override
	    public EnumSet<TickType> ticks() {

	        return EnumSet.of(TickType.CLIENT);
	    }
	}
package com.harrio34.cc1.core.proxy;

import com.harrio34.cc1.core.handlers.KeyBindingHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
	    @Override
	    public void registerKeyBindingHandler() {

	        KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler());
	    }

	    @Override
	    public void registerRenderTickHandler() {

	        TickRegistry.registerTickHandler(new TransmutationTargetOverlayHandler(), Side.CLIENT);
	    }

	    @Override
	    public void registerDrawBlockHighlightHandler() {

	        MinecraftForge.EVENT_BUS.register(new DrawBlockHighlightHandler());
	    }

	    @Override
	    public void setKeyBinding(String name, int value) {

	        KeyBindingUtil.addKeyBinding(name, value);
	        KeyBindingUtil.addIsRepeating(false);
	    }

	    @Override
	    public void registerSoundHandler() {

	        MinecraftForge.EVENT_BUS.register(new SoundHandler());
	    }

	    @Override
	    public void initRenderingAndTextures() {

	        RenderIds.calcinatorRender = RenderingRegistry.getNextAvailableRenderId();
	        RenderIds.aludelRender = RenderingRegistry.getNextAvailableRenderId();
	        RenderIds.alchemicalChestRender = RenderingRegistry.getNextAvailableRenderId();
	        RenderIds.glassBell = RenderingRegistry.getNextAvailableRenderId();
	        RenderIds.alchemyTable = RenderingRegistry.getNextAvailableRenderId();
	        RenderIds.renderingTank = RenderingRegistry.getNextAvailableRenderId();

	        MinecraftForgeClient.registerItemRenderer(BlockIds.CALCINATOR, new ItemCalcinatorRenderer());
	        MinecraftForgeClient.registerItemRenderer(BlockIds.ALUDEL_BASE, new ItemAludelRenderer());
	        MinecraftForgeClient.registerItemRenderer(BlockIds.ALCHEMICAL_CHEST, new ItemAlchemicalChestRenderer());
	        MinecraftForgeClient.registerItemRenderer(BlockIds.GLASS_BELL, new ItemGlassBellRenderer());
	        // TODO Alchemy Table item renderer
	        MinecraftForgeClient.registerItemRenderer(BlockIds.RENDERING_TANK, new ItemRenderingTankRenderer());
	    }

	    @Override
	    public void registerTileEntities() {

	        super.registerTileEntities();

	        ClientRegistry.bindTileEntitySpecialRenderer(TileCalcinator.class, new TileEntityCalcinatorRenderer());
	        ClientRegistry.bindTileEntitySpecialRenderer(TileAludel.class, new TileEntityAludelRenderer());
	        ClientRegistry.bindTileEntitySpecialRenderer(TileAlchemicalChest.class, new TileEntityAlchemicalChestRenderer());
	        ClientRegistry.bindTileEntitySpecialRenderer(TileGlassBell.class, new TileEntityGlassBellRenderer());
	        // TODO Alchemy Table TESR
	        ClientRegistry.bindTileEntitySpecialRenderer(TileRenderingTank.class, new TileEntityRenderingTankRenderer());
	    }

	    @Override
	    public void sendRequestEventPacket(byte eventType, int originX, int originY, int originZ, byte sideHit, byte rangeX, byte rangeY, byte rangeZ, String data) {

	        PacketDispatcher.sendPacketToServer(PacketTypeHandler.populatePacket(new PacketRequestEvent(eventType, originX, originY, originZ, sideHit, rangeX, rangeY, rangeZ, data)));
	    }

	    @Override
	    public void handleTileEntityPacket(int x, int y, int z, ForgeDirection orientation, byte state, String customName) {

	        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getBlockTileEntity(x, y, z);

	        if (tileEntity != null) {
	            if (tileEntity instanceof TileEE) {
	                ((TileEE) tileEntity).setOrientation(orientation);
	                ((TileEE) tileEntity).setState(state);
	                ((TileEE) tileEntity).setCustomName(customName);
	            }
	        }
	    }

	    @Override
	    public void handleTileWithItemPacket(int x, int y, int z, ForgeDirection orientation, byte state, String customName, int itemID, int metaData, int stackSize, int color) {

	        World world = FMLClientHandler.instance().getClient().theWorld;
	        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

	        this.handleTileEntityPacket(x, y, z, orientation, state, customName);

	        if (tileEntity != null) {
	            if (tileEntity instanceof TileGlassBell) {

	                ItemStack itemStack = new ItemStack(itemID, stackSize, metaData);
	                if (color != Integer.parseInt(Colours.PURE_WHITE, 16)) {
	                    ItemUtil.setColor(itemStack, color);
	                }

	                ((TileGlassBell) tileEntity).setInventorySlotContents(TileGlassBell.DISPLAY_SLOT_INVENTORY_INDEX, itemStack);
	                world.updateAllLightTypes(x, y, z);
	            }
	            else if (tileEntity instanceof TileAludel) {

	                ItemStack itemStack = new ItemStack(itemID, stackSize, metaData);
	                if (color != Integer.parseInt(Colours.PURE_WHITE, 16)) {
	                    ItemUtil.setColor(itemStack, color);
	                }

	                ((TileAludel) tileEntity).setInventorySlotContents(TileAludel.INPUT_INVENTORY_INDEX, itemStack);
	                world.updateAllLightTypes(x, y, z);
	            }
	        }
	    }

	    @Override
	    public void transmuteBlock(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int sideHit) {

	        if (TransmutationHelper.targetBlockStack != null) {
	            if (itemStack != null) {
	                int pnX = 1;
	                int pnY = 1;
	                int pnZ = 1;
	                if (itemStack.getItem() instanceof IChargeable) {
	                    int charge = ((IChargeable) itemStack.getItem()).getCharge(itemStack) * 2;
	                    switch (ForgeDirection.getOrientation(sideHit)) {
	                        case UP: {
	                            pnX = 1 + charge;
	                            pnZ = 1 + charge;
	                            break;
	                        }
	                        case DOWN: {
	                            pnX = 1 + charge;
	                            pnZ = 1 + charge;
	                            break;
	                        }
	                        case NORTH: {
	                            pnX = 1 + charge;
	                            pnY = 1 + charge;
	                            break;
	                        }
	                        case SOUTH: {
	                            pnX = 1 + charge;
	                            pnY = 1 + charge;
	                            break;
	                        }
	                        case EAST: {
	                            pnY = 1 + charge;
	                            pnZ = 1 + charge;
	                            break;
	                        }
	                        case WEST: {
	                            pnY = 1 + charge;
	                            pnZ = 1 + charge;
	                            break;
	                        }
	                        case UNKNOWN: {
	                            pnX = 0;
	                            pnY = 0;
	                            pnZ = 0;
	                            break;
	                        }
	                        default:
	                            break;
	                    }
	                }

	                EquivalentExchange3.proxy.sendRequestEventPacket(ActionTypes.TRANSMUTATION, x, y, z, (byte) sideHit, (byte) pnX, (byte) pnY, (byte) pnZ, TransmutationHelper.formatTargetBlockInfo(TransmutationHelper.targetBlockStack));
	            }
	        }
	    }

	}
}

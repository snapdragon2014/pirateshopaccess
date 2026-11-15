package com.example.piratetoken;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestHandler {

    private static final ResourceLocation BANNER =
            new ResourceLocation("piratetoken", "textures/gui/pirate_shop_access.png");
    private static final int BW = 96, BH = 16;

    private long startTime = 0;
    private int mode = 0;            // 0 yok, 1 acilis, 2 kapanis
    private boolean chestWasOpen = false;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest) {
            mode = 1;
            startTime = System.currentTimeMillis();
            chestWasOpen = true;
        } else if (event.gui == null && chestWasOpen) {
            mode = 2;
            startTime = System.currentTimeMillis();
            chestWasOpen = false;
        } else {
            chestWasOpen = false;
        }
    }

    // sandik ekrani aciktayken
    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiChest) draw();
    }

    // sandik kapaninca oyun ekraninda
    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Minecraft.getMinecraft().currentScreen == null) draw();
    }

    private void draw() {
        if (mode == 0) return;

        float total = (mode == 1) ? 1800f : 900f;
        float elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > total) { mode = 0; return; }
        float t = elapsed / total;

        float scale, alpha;
        if (mode == 1) { // acilis: buyuyerek patlar, sonra solar
            if (t < 0.15f)      scale = (t / 0.15f) * 1.25f;
            else if (t < 0.30f) scale = 1.25f - 0.25f * ((t - 0.15f) / 0.15f);
            else                scale = 1f;
            alpha = (t > 0.75f) ? 1f - (t - 0.75f) / 0.25f : 1f;
        } else {         // kapanis: kuculup kaybolur
            scale = 1f - 0.5f * t;
            alpha = 1f - t;
        }
        if (scale <= 0f || alpha <= 0f) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        GlStateManager.pushMatrix();
        GlStateManager.translate(sr.getScaledWidth() / 2f, 30f, 0f);
        GlStateManager.scale(2f * scale, 2f * scale, 1f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1f, 1f, 1f, alpha);
        mc.getTextureManager().bindTexture(BANNER);
        Gui.drawModalRectWithCustomSizedTexture(-BW / 2, -BH / 2, 0, 0, BW, BH, BW, BH);
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();
    }
}

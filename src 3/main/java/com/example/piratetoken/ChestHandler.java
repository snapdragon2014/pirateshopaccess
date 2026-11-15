package com.example.piratetoken;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
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
    private static final ResourceLocation SHOP_SOUND = new ResourceLocation("piratetoken:shop");
    private static final int BW = 96, BH = 16;

    private static final float OPEN_MS = 3600f;   // acilis animasyonu suresi
    private static final float CLOSE_MS = 700f;   // kapanis animasyonu suresi

    private long startTime = 0;
    private int mode = 0;            // 0 yok, 1 acilis, 2 kapanis
    private boolean chestWasOpen = false;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest) {
            mode = 1;
            startTime = System.currentTimeMillis();
            chestWasOpen = true;
            try {
                Minecraft.getMinecraft().getSoundHandler()
                        .playSound(PositionedSoundRecord.create(SHOP_SOUND, 1.0F));
            } catch (Exception ignored) { }
        } else if (event.gui == null && chestWasOpen) {
            mode = 2;
            startTime = System.currentTimeMillis();
            chestWasOpen = false;
        } else {
            chestWasOpen = false;
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.gui instanceof GuiChest) draw();
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Minecraft.getMinecraft().currentScreen == null) draw();
    }

    private static float easeOut(float u) { float k = 1f - u; return 1f - k * k * k; }
    private static float easeIn(float u)  { return u * u * u; }

    private void draw() {
        if (mode == 0) return;

        float total = (mode == 1) ? OPEN_MS : CLOSE_MS;
        float elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > total) { mode = 0; return; }
        float t = elapsed / total;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        float dist = sr.getScaledWidth() / 2f + BW;   // ekranin disina cikmak icin mesafe

        float x, alpha = 1f;
        if (mode == 1) {
            if (t < 0.15f)      x = -dist * (1f - easeOut(t / 0.15f));   // soldan kayarak gelir
            else if (t < 0.45f) x = 0f;                                  // ortada durur
            else if (t < 0.62f) x = dist * easeIn((t - 0.45f) / 0.17f);  // saga dogru gecip gider
            else if (t < 0.79f) x = dist * (1f - easeOut((t - 0.62f) / 0.17f)); // sagdan geri gelir
            else                x = 0f;                                  // ortada durur
            if (t > 0.92f) alpha = 1f - (t - 0.92f) / 0.08f;             // sonra solar
        } else {
            x = dist * easeIn(t);                                        // kapaninca saga kayip gider
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(sr.getScaledWidth() / 2f + x, 30f, 0f);
        GlStateManager.scale(2f, 2f, 1f);
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

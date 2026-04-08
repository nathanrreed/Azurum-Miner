package com.nred.azurum_miner.compat.recipe_viewers.jei;

import com.nred.azurum_miner.screen.SidebarScreen;
import com.nred.azurum_miner.widget.side_bar.CollapsableWidget;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collection;

import static com.nred.azurum_miner.util.Helpers.azLoc;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return azLoc("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new IGlobalGuiHandler() {
            @Override
            public Collection<Rect2i> getGuiExtraAreas() {
                if (Minecraft.getInstance().screen instanceof SidebarScreen<?, ?> screen) {
                    ArrayList<Rect2i> rects = new ArrayList<>();
                    screen.renderables.forEach(renderable -> {
                        if (renderable instanceof CollapsableWidget el) {
                            rects.add(new Rect2i(el.getX(), el.getY(), el.getWidth(), el.getHeight()));
                        }
                    });

                    return rects;
                }
                return IGlobalGuiHandler.super.getGuiExtraAreas();
            }
        });
    }
}
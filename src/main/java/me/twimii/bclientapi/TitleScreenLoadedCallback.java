package me.twimii.bclientapi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface TitleScreenLoadedCallback {

    Event<TitleScreenLoadedCallback> EVENT = EventFactory.createArrayBacked(TitleScreenLoadedCallback.class,
            (listeners) -> () -> {
                for (TitleScreenLoadedCallback listener : listeners) {
                    ActionResult result = listener.interact();

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact();
}

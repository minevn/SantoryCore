package mk.plugin.santory.listener;

import mk.plugin.santory.skin.gui.SkinNPCGUI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCClick(NPCRightClickEvent e) {
        SkinNPCGUI.onNPCInteract(e);
    }
}

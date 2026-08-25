package qouteall.imm_ptl.core.ducks;

import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.TicketStorage;
import java.util.List;
import java.util.concurrent.Executor;

public interface IEDistanceManager {
    List<Ticket> portal_getTickets(long chunkPos);
    TicketStorage portal_getTicketStorage();
    Executor ip_getMainThreadExecutor();
}

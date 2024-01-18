package at.nanopenguin.mtcg.application;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BattleHandler {
    private static BattleHandler INSTANCE;
    private Lock lock = new ReentrantLock();
    private Battle waiting = null;
    private BattleHandler() {

    }

    public static BattleHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BattleHandler();
        }

        return INSTANCE;
    }

    public String[] startBattle(UUID userUuid) throws SQLException {
        Battle battle;

        this.lock.lock();
        if ((battle = this.waiting) == null) {
            battle = (this.waiting = new Battle(new Combatant(userUuid)));
        }

        synchronized (battle) {
            this.lock.unlock();

            if (battle.isWaiting()) {
                try {
                    battle.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                return battle.getLog().toArray(new String[0]);
            }

            battle.addCombatant(new Combatant(userUuid));
            battle.start();
        }

        battle.notify();
        return battle.getLog().toArray(new String[0]);
    }
}

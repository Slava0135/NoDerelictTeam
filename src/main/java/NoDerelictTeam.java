import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

import java.util.*;

public class NoDerelictTeam extends Plugin {

    @Override
    public void init() {
        Timer.schedule(() -> {

            HashMap<Team, Integer> amount = new HashMap<>();
            for (Teams.TeamData team : Vars.state.teams.active) {
                if (team.team == Team.derelict) continue;
                amount.put(team.team, 0);
            }

            ArrayList<Player> players = new ArrayList<>();
            Groups.player.each(player -> {
                if (player.team() == Team.derelict) {
                    players.add(player);
                } else {
                    int count = amount.get(player.team());
                    amount.put(player.team(), count + 1);
                }
            });

            if (players.size() > 0) {

                List<Map.Entry<Team, Integer>> list = new ArrayList<>(amount.entrySet());
                list.sort(Map.Entry.comparingByValue());

                for (Player player : players) {

                    Map.Entry<Team, Integer> lastEntry = list.get(0);
                    int index = 0;
                    for (Map.Entry<Team, Integer> entry : list) {
                        if (lastEntry.getValue() < entry.getValue()) {
                            break;
                        }
                        lastEntry = entry;
                        index++;
                    }

                    Map.Entry<Team, Integer> newEntry = new AbstractMap.SimpleEntry<>(lastEntry.getKey(), lastEntry.getValue() + 1);
                    list.set(index,  newEntry);
                    player.team(lastEntry.getKey());
                }
            }
        }, 0, 1);
    }
}

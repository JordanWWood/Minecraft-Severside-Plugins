package network.marble.minigamecore.managers;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import lombok.Getter;
import lombok.Setter;
import network.marble.dataaccesslayer.models.GameMode;
import network.marble.minigamecore.entities.team.Team;
import network.marble.minigamecore.entities.team.TeamSetup;
import network.marble.minigamecore.entities.player.MiniGamePlayer;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

public class TeamManager {
    private static TeamManager instance;
    private static ArrayList<TeamSetup> teamConfigs;
    private static ArrayList<Team> teams;
    private static boolean dynamicTeams = false;
    @Getter @Setter
    private static boolean teamChat = false;

    public TeamManager(){
        teams = new ArrayList<>();
        teamConfigs = new ArrayList<>();
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void registerTeamsWithPlayers() {
        for (Team team : teams) {
            WrapperPlayServerScoreboardTeam st = team.getScoreboardTeamPacket();
            st.setMode(0);
            st.setPlayers(team.getPlayers().stream().map(p -> p.id.toString()).collect(Collectors.toList()));
            Bukkit.getOnlinePlayers().forEach(st::sendPacket);
        }
    }

    public void sortPlayerIntoTeam(MiniGamePlayer player) {
        boolean teamFound = false;
        WrapperPlayServerScoreboardTeam st;
        for (Team team : teams) {
            if (team.getPlayers().size() >= team.getMaximumNumberOfPlayers()) continue;
            team.getPlayers().add(player);
            teamFound = true;
            st = team.getScoreboardTeamPacket();
            st.setMode(3);
            st.setPlayers(Collections.singletonList(player.getPlayer().getUniqueId().toString()));
            Bukkit.getOnlinePlayers().forEach(st::sendPacket);
            break;
        }

        if (teamFound) return;
        TeamSetup newSetup = teamConfigs.size() > 0 ? teamConfigs.get(dynamicTeams ? 0 : teams.size()) : new TeamSetup();
        Team newTeam = new Team(newSetup);
        newTeam.getPlayers().add(player);
        teams.add(newTeam);
        //MiniGameCore.simpleScoreboardManager.registerTeamWithScoreboard(newTeam);
        st = newTeam.getScoreboardTeamPacket();
        st.setMode(0);
        st.setPlayers(Collections.singletonList(player.id.toString()));
        Bukkit.getOnlinePlayers().forEach(st::sendPacket);
    }

    public void removePlayerFromTeams(UUID playerID, boolean removeTeamIfEmpty) {
        Team team = getPlayersTeam(playerID);
        MiniGamePlayer player = PlayerManager.getPlayer(playerID);
        if (player != null && team != null) {
            team.getPlayers().remove(player);
            WrapperPlayServerScoreboardTeam st = team.getScoreboardTeamPacket();
            st.setMode(4);
            st.setPlayers(Collections.singletonList(player.id.toString()));
            Bukkit.getOnlinePlayers().forEach(st::sendPacket);
        }
        if (removeTeamIfEmpty && team != null && team.getPlayers().size() <= 0)
        {
            WrapperPlayServerScoreboardTeam st = team.getScoreboardTeamPacket();
            st.setMode(1);
            teams.remove(team);
        }
    }

    public void sortAllPlayersIntoTeams() {
        PlayerManager.getPlayers().forEach( this::sortPlayerIntoTeam );
    }

    public Team getPlayersTeam(UUID playerID){
        for (Team team : teams) for (MiniGamePlayer player : team.getPlayers()) if (player.id == playerID) return team;
        return null;
    }

    public List<MiniGamePlayer> getTeamMates(UUID playerID){
        Team team = getPlayersTeam(playerID);
        return team == null ? new ArrayList<>() : team.getPlayers().stream().filter(p -> p.id != playerID).collect(Collectors.toList());
    }

    public void configTeamSetup(ArrayList<TeamSetup> teamConfigs) {
        TeamManager.teamConfigs = teamConfigs;
    }

    public void dynamicTeams(boolean dynamicTeams) {
        TeamManager.dynamicTeams = dynamicTeams;
    }

    public static int calculateMinimumOfPlayers() {
        GameMode gm = GameManager.getGameMode();
        if (dynamicTeams) {
            int teamCount = gm != null ? gm.getMinTeamCount() : 1;
            int minPlayers = teamConfigs.size() > 0 ? teamConfigs.get(0).minimumNumberOfPlayers : 1;
            return teamCount * minPlayers;
        } else {
            return teamConfigs.stream().mapToInt(tc-> tc.minimumNumberOfPlayers).sum();
        }
    }

    public static int calculateMaximumOfPlayers() {
        GameMode gm = GameManager.getGameMode();
        if (dynamicTeams) {
            int teamCount = gm != null ? gm.getMaxTeamCount() : Integer.MAX_VALUE;
            int maxPlayers = teamConfigs.size() > 0 ? teamConfigs.get(0).maximumNumberOfPlayers : 1;
            return teamCount * maxPlayers;
        } else {
            return teamConfigs.stream().mapToInt(tc-> tc.maximumNumberOfPlayers).sum();
        }
    }


    public static TeamManager getInstance() {
        if (instance == null) instance = new TeamManager();
        return instance;
    }
}

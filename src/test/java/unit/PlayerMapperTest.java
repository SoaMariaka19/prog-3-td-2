package unit;

import app.foot.model.Player;
import app.foot.model.PlayerScorer;
import app.foot.repository.MatchRepository;
import app.foot.repository.PlayerRepository;
import app.foot.repository.TeamRepository;
import app.foot.repository.entity.MatchEntity;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.TeamEntity;
import app.foot.repository.mapper.PlayerMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static utils.TestUtils.*;

public class PlayerMapperTest {
    public static final int MATCH_ID = 1;
    MatchRepository matchRepositoryMock = mock(MatchRepository.class);
    PlayerRepository playerRepositoryMock = mock(PlayerRepository.class);
    TeamRepository teamRepositoryMock = mock(TeamRepository.class);
    PlayerMapper subject = new PlayerMapper(matchRepositoryMock, playerRepositoryMock, teamRepositoryMock);

    private static PlayerEntity entityRakoto() {
        return playerEntityRakoto(teamBarea());
    }

    private static PlayerScorer rakotoScorer() {
        return rakotoModelScorer(
                playerModelRakoto(entityRakoto()),
                scorerRakoto(playerEntityRakoto(teamBarea())));
    }

    @Test
    void player_to_domain_ok() {
        PlayerEntity entity = entityRakoto();
        Player expected = Player.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isGuardian(entity.isGuardian())
                .teamName(entity.getTeam().getName())
                .build();

        Player actual = subject.toDomain(entity);

        assertEquals(expected, actual);
    }

    @Test
    void player_scorer_to_domain_ok() {
        PlayerScorer actual = subject.toDomain(PlayerScoreEntity.builder()
                .id(1)
                .player(entityRakoto())
                .minute(10)
                .ownGoal(false)
                .build());

        assertEquals(rakotoScorer(), actual);
    }

    @Test
    void player_scorer_to_entity_ok() {
        Instant now = Instant.now();
        MatchEntity matchEntity1 = MatchEntity.builder()
                .id(1)
                .teamA(teamBarea())
                .teamB(teamGhana())
                .scorers(List.of())
                .datetime(now)
                .stadium("Mahamasina")
                .build();
        when(playerRepositoryMock.findById(1))
                .thenReturn(Optional.of(playerEntityRakoto(teamBarea())));
        when(matchRepositoryMock.findById(1))
                .thenReturn(Optional.of(matchEntity1));

        PlayerScoreEntity actual = subject.toEntity(MATCH_ID, PlayerScorer.builder()
                .isOwnGoal(false)
                .minute(10)
                .player(Player.builder()
                        .id(1)
                        .name("Rakoto")
                        .isGuardian(false)
                        .teamName("Barea")
                        .build())
                .build());


        assertEquals(PlayerScoreEntity.builder()
                .player(playerEntityRakoto(teamBarea()))
                .minute(10)
                .ownGoal(false)
                .match(matchEntity1)
                .build(), actual);
    }


    @Test
    void player_scorer_to_entity_ko() {
        Instant now = Instant.now();
        MatchEntity matchEntity1 = MatchEntity.builder()
                .id(1)
                .teamA(null)
                .teamB(null)
                .scorers(List.of())
                .datetime(now)
                .stadium("Mahamasina")
                .build();
        when(playerRepositoryMock.findById(1))
                .thenReturn(Optional.of(playerEntityRakoto(teamBarea())));
        when(matchRepositoryMock.findById(1))
                .thenReturn(Optional.of(matchEntity1));
        PlayerScorer actual = PlayerScorer.builder()
                .player(Player.builder()
                        .isGuardian(true)
                        .name("jean")
                        .build())
                .minute(10)
                .isOwnGoal(false)
                .build();

        assertThrows(NoSuchElementException.class , () -> subject.toEntity(matchEntity1.getId(),actual));
    }

    @Test
    void player_to_entity_ok(){

        Player player = Player.builder()
                .isGuardian(false)
                .name("Soa")
                .teamName("Team")
                .id(1)
                .build();

        TeamEntity team = TeamEntity.builder()
                .name("Team")
                .id(1)
                .build();

        PlayerEntity expected = PlayerEntity.builder()
                .team(team)
                .guardian(player.getIsGuardian())
                .name(player.getName())
                .id(player.getId())
                .build();


    when(teamRepositoryMock.findByName(player.getTeamName()))
            .thenReturn(team);

        PlayerEntity actual = subject.toEntity(player);

        assertEquals(expected,actual);
    }

    @Test
    void player_to_entity_ko(){

        Player player = Player.builder()
                .isGuardian(false)
                .name("Soa")
                .teamName("Team")
                .id(1)
                .build();

        Player player1 = Player.builder()
                .isGuardian(false)
                .name("Soa")
                .teamName(null)
                .id(1)
                .build();


        TeamEntity team = TeamEntity.builder()
                .name("Team")
                .id(1)
                .build();

        PlayerEntity expected = PlayerEntity.builder()
                .team(team)
                .guardian(player.getIsGuardian())
                .name(player.getName())
                .id(player.getId())
                .build();


        when(teamRepositoryMock.findByName(player.getTeamName()))
                .thenReturn(null);


        PlayerEntity actual = subject.toEntity(player);

        assertNotEquals(expected, actual);
    }

}

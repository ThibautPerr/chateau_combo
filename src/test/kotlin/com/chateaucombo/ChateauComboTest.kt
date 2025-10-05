package com.chateaucombo

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import com.chateaucombo.deck.repository.DeckRepository
import com.chateaucombo.joueur.model.Joueur
import com.chateaucombo.joueur.repository.JoueurRepository
import com.chateaucombo.tableau.repository.TableauRepository
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists

class ChateauComboTest {
    private val joueurRepository = spyk(JoueurRepository(TableauRepository(), DeckRepository()))

    private val deckRepository = DeckRepository()

    private val app = ChateauCombo(joueurRepository, deckRepository)

    private fun play(joueurs: List<Joueur>) {
        val path = pathAvecToutesLesCartes()
        app.play(joueurs = joueurs, pathCartes = path)
    }

    private fun pathAvecToutesLesCartes() = Path("src/main/resources/cartes")

    private lateinit var fileAppender: FileAppender<ILoggingEvent>

    private lateinit var rootLogger: Logger

    @BeforeEach
    fun setUp() {
        prepareLogger()
    }

    private fun prepareLogger(): Pair<FileAppender<ILoggingEvent>, Logger> {
        Path("target/test-logs/test.log").deleteIfExists()
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        fileAppender = FileAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = "TEST_FILE"
            file = "target/test-logs/test.log"
            encoder = PatternLayoutEncoder().apply {
                pattern = "%msg%n"
                context = loggerContext
                start()
            }
            start()
        }

        rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        rootLogger.addAppender(fileAppender)
        return Pair(fileAppender, rootLogger)
    }

    @AfterEach
    fun tearDown() {
        rootLogger.detachAppender(fileAppender)
        fileAppender.stop()
    }

    @Test
    fun `should play a game with 4 players`() {
        val joueurs = quatreJoueurs()

        play(joueurs)

        joueurs.forEach { joueur ->
            verify(exactly = 9) {
                joueurRepository.choisitUneCarte(joueur, any())
                joueurRepository.choisitUnePosition(joueur)
                joueurRepository.placeUneCarte(joueur, any(), any())
            }
        }
    }

    private fun quatreJoueurs() = List(4) { Joueur(id = it) }

    @Disabled
    @Test
    fun `should play multiple games`() {
        for (i in 1..10) {
            val joueurs = quatreJoueurs()

            play(joueurs)
        }
    }
}

package dev.leon.zimmermann.semanticsearch.integration.data.confluence

import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H1_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.H2_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.PARAGRAPH_TAG
import dev.leon.zimmermann.semanticsearch.integration.data.confluence.ConfluenceDataService.Companion.TITLE_TAG
import dev.leon.zimmermann.semanticsearch.preprocessors.impl.IdentityTextPreprocessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

internal class ConfluenceDataPreprocessorUnitTest {

    @Test
    fun testApplySimple() {
        val textPreprocessor = IdentityTextPreprocessor()
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)
        val input = javaClass.getResource("/test.html").readText(Charset.defaultCharset())
        val result = confluenceDataPreprocessor.apply(input)!!
        assertEquals("test", result[TITLE_TAG])
        assertEquals("test-titel", result[H1_TAG])
        assertEquals("noch ein weiterer test-titel, aber als h2", result[H2_TAG])
        assertEquals("das ist ein erster test-paragraph, welcher extrahiert werden sollte das ist ein weiterer paragraph. dieser paragraph ist unter einem h2-tag zu finden, sollte aber auch erkannt werden", result[PARAGRAPH_TAG])
    }

    @Test
    fun testNoContent() {
        val textPreprocessor = IdentityTextPreprocessor()
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)
        val input = javaClass.getResource("/test-no-content.html").readText(Charset.defaultCharset())
        assertNull(confluenceDataPreprocessor.apply(input))
    }

    @Test
    fun testApplyRealistic() {
        val textPreprocessor = IdentityTextPreprocessor()
        val confluenceDataPreprocessor = ConfluenceDataPreprocessor(textPreprocessor)
        val input = javaClass.getResource("/Auftragsliste_exportieren.html").readText(Charset.defaultCharset())
        val result = confluenceDataPreprocessor.apply(input)!!
        assertEquals("auftragsliste exportieren - dokumentation", result[TITLE_TAG])
        assertEquals("auftragsliste erzeugen benutzeroberfläche des unterbereichs auftragsliste exportieren", result[H1_TAG])
        assertEquals("exportdaten festlegen menüs und buttons der kopf-navigationsleiste linker teil des inhaltsbereichs rechter teil des inhaltsbereichs", result[H2_TAG])
        assertEquals("in unterbereich auftragsliste exportieren des arbeitsbereichs auftrags-import & export können sie eine auftragsliste für regelprüfungen im folgejahr aus den stammdaten der pflegeeinrichtungen exportieren. maximal ein export gleichzeitig es kann seitens der zum export berechtigten nutzer, maximal ein export gleichzeitig ausgeführt werden. das ergebnis des exports ist danach für alle nutzer mit der entsprechenden berechtigung verfügbar. ein neuer export überschreibt das alte ergebnis. wechseln sie in den unterbereich \"auftragsliste exportieren\" wählen sie in der auswahlliste \"kalenderjahr\" das kalenderjahr für das die gültigen pflegeeinrichtungen ermittelt werden sollen. wählen sie in der auswahlliste \"produkttyp\" den produkttyp zu dessen art und pflegeart die zu ermittelnden pflegeeinrichtungen passen müssen. haken sie die checkbox \"pkv-ziehung\" an, wenn beim export eine pkv-ziehung durchgeführt werden soll. haken sie die checkbox \"nur für md-regionen\" an, wenn nur pflegeeinrichtungen verwendet werden sollen, deren standort zur prüfungsbeginn (prüfungsort) in einer region des md liegen. nach angabe aller verarbeitungsdaten können sie durch klick auf den \"export starten\"-button die zusammenstellung der auftragsliste für den export anstoßen. nach erfolgter verarbeitung wird ihnen das verarbeitungsergebnis im rechten teil des inhaltsbereichs angezeigt. mit klick auf den \"export herunterladen\"-button können sie nun die zusammengestellte auftragsliste als csv datei speichern. mit klick auf den \"bericht herunterladen\"-button können sie den bericht der angezeigten meldungen als csv datei speichern. die benutzeroberfläche ist wie folgt aufgebaut: buttons beschreibung \"aufträge importieren\" wechselt in den unterbereich aufträge importieren (excel/csv) \"auftragsliste exportieren\" ist hellblau hinterlegt, wenn der unterbereich ausgewählt ist element beschreibung \"kalenderjahr\" auswahl zur angabe des kalenderjahrs für das die gültigen pflegeeinrichtungen ermittelt werden sollen \"produkttyp\" auswahlfeld zur angabe des produkttyps zu dessen art und pflegeart die zu ermittelnden pflegeinrichtungen passen müssen. \"pkv-ziehung\" checkbox zur angabe, ob beim export eine pkv-ziehung durchgeführt werden soll. \"nur für md-regionen\" checkbox zur angabe, ob pflegeeinrichtungen verwendet werden sollen, deren standort zu prüfungsbeginn (prüfungsort) in einer region des md liegen. \"export starten\"-button stellt die daten entsprechend der auswahlkriterien zusammen und die zu exportierenden dateiliste zum download bereit element beschreibung hinweisbereich anzeigebereich, für hinweise und fehler bei der bereitstellung der exportdatei \"export herunterladen\"-button öffnet einen modaldialog zum speichern der erstellten auftragsliste \"bericht herunterladen\"-button öffnet einen modaldialog zum speichern der angezeigten export meldungen als datei im csv-format", result[PARAGRAPH_TAG])
    }
}

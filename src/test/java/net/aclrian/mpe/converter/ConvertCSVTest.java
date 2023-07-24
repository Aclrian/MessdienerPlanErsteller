package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Dialogs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.*;

import static net.aclrian.mpe.converter.ConvertCSV.parseLineToMessdiener;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

class ConvertCSVTest {
    private List<Messdiener> medis = new ArrayList<>();

    @Test
    void testImport(@TempDir Path tempDir) throws IOException { //NOPMD - suppressed NcssCount - for test purposes
        DateienVerwalter dv = Mockito.mock(DateienVerwalter.class);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Dialogs dialog = Mockito.mock(Dialogs.class);
        Dialogs.setDialogs(dialog);
        Mockito.when(pf.getSettings()).thenReturn(new Einstellungen());
        doAnswer(invocationOnMock -> {
            medis = null;
            return null;
        }).when(dv).reloadMessdiener();
        Mockito.when(dv.getMessdiener()).then((Answer<List<Messdiener>>) invocationOnMock -> getMessdiener(tempDir));

        List<ConvertCSV.Sortierung> columns = Arrays.asList(
                ConvertCSV.Sortierung.VORNAME, ConvertCSV.Sortierung.NACHNAME, ConvertCSV.Sortierung.NICHT_LEITER,
                ConvertCSV.Sortierung.EINTRITT, ConvertCSV.Sortierung.EMAIL
        );
        ConvertCSV.ConvertData convertdata = new ConvertCSV.ConvertData(
                null, columns, Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        Messdiener m = parseLineToMessdiener("a;b;;2000;a@a.de", convertdata);
        assertThat(m).isNotNull();
        assertThat(m.getVorname()).isEqualTo("a");
        assertThat(m.getNachnname()).isEqualTo("b");
        assertThat(m.istLeiter()).isTrue();
        assertThat(m.getEintritt()).isEqualTo(2000);
        assertThat(m.getEmail()).isEqualTo("a@a.de");

        columns = Arrays.asList(
                ConvertCSV.Sortierung.NACHNAME_KOMMA_VORNAME, ConvertCSV.Sortierung.LEITER, ConvertCSV.Sortierung.EINTRITT,
                ConvertCSV.Sortierung.EMAIL, ConvertCSV.Sortierung.IGNORIEREN
        );
        convertdata = new ConvertCSV.ConvertData(
                null, columns, Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener("b, a;;x;non-valid e-mail;empty", convertdata);
        assertThat(m).isNotNull();
        assertThat(m.getVorname()).isEqualTo("a");
        assertThat(m.getNachnname()).isEqualTo("b");
        assertThat(m.istLeiter()).isFalse();
        assertThat(m.getEintritt()).isGreaterThanOrEqualTo(2023);
        assertThat(m.getEmail()).isEmpty();

        convertdata = new ConvertCSV.ConvertData(
                null, List.of(ConvertCSV.Sortierung.NACHNAME), Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener("b;b", convertdata);
        assertThat(m).isNull();

        convertdata = new ConvertCSV.ConvertData(
                null, List.of(ConvertCSV.Sortierung.NACHNAME), Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener("b", convertdata);
        assertThat(m).isNull();

        columns = List.of(ConvertCSV.Sortierung.VORNAME_LEERZEICHEN_NACHNAME, ConvertCSV.Sortierung.EINTRITT, ConvertCSV.Sortierung.LEITER);
        convertdata = new ConvertCSV.ConvertData(
                null, columns, Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener("a b c;2023;x;", convertdata);
        assertThat(m.getVorname()).isEqualTo("a");
        assertThat(m.getNachnname()).isEqualTo("b c");

        convertdata = new ConvertCSV.ConvertData(
                null, columns, Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener("a b;2023;x;", convertdata);
        assertThat(m.getVorname()).isEqualTo("a");
        assertThat(m.getNachnname()).isEqualTo("b");

        convertdata = new ConvertCSV.ConvertData(
                null, columns, Collections.singletonList(new Sonstiges()),
                ";", ",", Charset.defaultCharset(), true, false
        );
        m = parseLineToMessdiener(" ;2023;x;", convertdata);
        assertThat(m).isNull();

        StandardMesse sm1 = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm2 = new StandardMesse(DayOfWeek.TUESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm3 = new StandardMesse(DayOfWeek.WEDNESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm4 = new StandardMesse(DayOfWeek.THURSDAY, 8, "00", "ort", 1, "typ");
        List<StandardMesse> standardMessen = Arrays.asList(sm1, sm2, sm3, sm4);
        convertdata = new ConvertCSV.ConvertData(
                null,
                List.of(
                ConvertCSV.Sortierung.VORNAME,
                ConvertCSV.Sortierung.NACHNAME,
                ConvertCSV.Sortierung.STANDARD_MESSE,
                ConvertCSV.Sortierung.STANDARD_MESSE,
                ConvertCSV.Sortierung.NICHT_STANDARD_MESSE,
                ConvertCSV.Sortierung.NICHT_STANDARD_MESSE
                ),
                standardMessen, ";", ",", Charset.defaultCharset(), true, false);
        Mockito.when(pf.getStandardMessen()).thenReturn(standardMessen);
        m = parseLineToMessdiener("a;b;x;;x;;", convertdata);
        assertThat(m.getDienverhalten().getBestimmtes(sm1)).isTrue();
        assertThat(m.getDienverhalten().getBestimmtes(sm2)).isFalse();
        assertThat(m.getDienverhalten().getBestimmtes(sm3)).isFalse();
        assertThat(m.getDienverhalten().getBestimmtes(sm4)).isFalse();
        assertThat(m.getNachnname()).isEqualTo("b");

        Files.createDirectories(tempDir);
        Path file = Files.createTempFile(tempDir, null, ".csv");
        Mockito.when(dv.getSavePath()).thenReturn(tempDir.toFile());
        Files.writeString(file, "a;b;1,3;2\nc;d;0;\ne;f;;0\ng;h;0;");
        convertdata = new ConvertCSV.ConvertData(
                file.toFile(),
                List.of(
                        ConvertCSV.Sortierung.VORNAME,
                        ConvertCSV.Sortierung.NACHNAME,
                        ConvertCSV.Sortierung.FREUNDE,
                        ConvertCSV.Sortierung.GESCHWISTER
                ),
                standardMessen, ";", ",", Charset.defaultCharset(), true, false);
        ConvertCSV converter = new ConvertCSV(convertdata);
        converter.start();

        ReadFile rf = new ReadFile();
        m = rf.getMessdiener(tempDir.resolve("b, a.xml").toFile());
        assertThat(m.getFreunde()[0]).isEqualTo("d, c");
        assertThat(m.getFreunde()[1]).isEqualTo("h, g");
        assertThat(m.getFreunde()[2]).isEmpty();
        assertThat(m.getFreunde()[3]).isEmpty();
        assertThat(m.getFreunde()[4]).isEmpty();
        assertThat(m.getGeschwister()[0]).isEqualTo("f, e");
        assertThat(m.getGeschwister()[1]).isEmpty();
        assertThat(m.getGeschwister()[2]).isEmpty();
        Mockito.verify(dialog, Mockito.times(1)).info(Mockito.anyString());
    }

    @Test
    void testIO(@TempDir Path tempDir) throws IOException {
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);
        DateienVerwalter dv = Mockito.mock(DateienVerwalter.class);
        DateienVerwalter.setInstance(dv);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        Dialogs dialog = Mockito.mock(Dialogs.class);
        Dialogs.setDialogs(dialog);
        Dialogs.setDialogs(dialog);
        Mockito.when(pf.getSettings()).thenReturn(new Einstellungen());
        medis = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            medis = null;
            return null;
        }).when(dv).reloadMessdiener();
        Mockito.when(dv.getSavePath()).thenReturn(tempDir.toFile());

        StandardMesse sm1 = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm2 = new StandardMesse(DayOfWeek.TUESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm3 = new StandardMesse(DayOfWeek.WEDNESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm4 = new StandardMesse(DayOfWeek.THURSDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm5 = new StandardMesse(DayOfWeek.SATURDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm6 = new StandardMesse(DayOfWeek.SUNDAY, 8, "00", "ort", 1, "typ");
        List<StandardMesse> standardMessen = Arrays.asList(sm1, sm2, sm3, sm4, sm5, sm6);
        Mockito.when(pf.getStandardMessen()).thenReturn(standardMessen);


        Path convertFile = Paths.get("src", "test", "resources", "converter", "converter_data.csv");
        ConvertCSV.ConvertData convertdata = new ConvertCSV.ConvertData(
                convertFile.toFile(),
                List.of(ConvertCSV.Sortierung.VORNAME,
                        ConvertCSV.Sortierung.NACHNAME,
                        ConvertCSV.Sortierung.EINTRITT,
                        ConvertCSV.Sortierung.EMAIL,
                        ConvertCSV.Sortierung.FREUNDE,
                        ConvertCSV.Sortierung.GESCHWISTER,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.STANDARD_MESSE,
                        ConvertCSV.Sortierung.IGNORIEREN
                ), standardMessen, ";", ",", Charset.defaultCharset(), true, false);
        assertThat(convertFile.toFile()).exists();
        new ConvertCSV(convertdata).start();
        String tomString = "Schmidt, Tom.xml";
        Path tom = Paths.get(tempDir.toString(), tomString);
        String timString = "Schmidt, Tim.xml";
        Path tim = Paths.get(tempDir.toString(), timString);
        String annaString = "Kiel, Anna.xml";
        Path anna = Paths.get(tempDir.toString(), annaString);
        assertThat(tom).exists().hasSameContentAs(Paths.get(convertFile.getParent().toString(), tomString));
        assertThat(tim).exists().hasSameContentAs(Paths.get(convertFile.getParent().toString(), timString));
        assertThat(anna).exists().hasSameContentAs(Paths.get(convertFile.getParent().toString(), annaString));
        Mockito.verify(dialog, Mockito.times(1)).info(Mockito.anyString());
        Locale.setDefault(locale);
    }

    private List<Messdiener> getMessdiener(Path tempDir) {
        if (medis != null) {
            return medis;
        }
        medis = new ArrayList<>();
        ReadFile rf = new ReadFile();
        medis.add(rf.getMessdiener(tempDir.resolve("b, a.xml").toFile()));
        medis.add(rf.getMessdiener(tempDir.resolve("d, c.xml").toFile()));
        medis.add(rf.getMessdiener(tempDir.resolve("f, e.xml").toFile()));
        medis.add(rf.getMessdiener(tempDir.resolve("h, g.xml").toFile()));
        medis.forEach(Messdiener::setNewMessdatenDaten);
        return medis;
    }
}

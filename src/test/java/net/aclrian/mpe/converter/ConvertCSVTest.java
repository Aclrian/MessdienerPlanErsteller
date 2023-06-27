package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Messdaten;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.ReadFile;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.messe.StandardMesse;
import net.aclrian.mpe.pfarrei.Einstellungen;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.util.*;

import static net.aclrian.mpe.converter.ConvertCSV.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;

class ConvertCSVTest {
    private List<Messdiener> medis = new ArrayList<>();

    @Test
    void testImport(@TempDir Path tempDir) throws IOException {
        DateienVerwalter dv = Mockito.mock(DateienVerwalter.class);
        Pfarrei pf = Mockito.mock(Pfarrei.class);
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getPfarrei()).thenReturn(pf);
        ConvertData convertdata = new ConvertData(null, Arrays.asList(Sortierung.VORNAME, Sortierung.NACHNAME, Sortierung.NICHT_LEITER, Sortierung.EINTRITT, Sortierung.EMAIL), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        Messdiener m = parseLineToMessdiener("a;b;;2000;a@a.de", convertdata);
        assertThat(m, notNullValue());
        assertThat(m.getVorname(), is("a"));
        assertThat(m.getNachnname(), is("b"));
        assertThat(m.istLeiter(), is(true));
        assertThat(m.getEintritt(), is(2000));
        assertThat(m.getEmail(), is("a@a.de"));

        convertdata = new ConvertData(null, Arrays.asList(Sortierung.NACHNAME_KOMMA_VORNAME, Sortierung.LEITER, Sortierung.EINTRITT, Sortierung.EMAIL, Sortierung.IGNORIEREN), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener("b, a;;x;non-valid e-mail;empty", convertdata);
        assertThat(m, notNullValue());
        assertThat(m.getVorname(), is("a"));
        assertThat(m.getNachnname(), is("b"));
        assertThat(m.istLeiter(), is(false));
        assertTrue(m.getEintritt() >= 2023);
        assertThat(m.getEmail(), is(""));

        convertdata = new ConvertData(null, List.of(Sortierung.NACHNAME), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener("b;b", convertdata);
        assertThat(m, nullValue());

        convertdata = new ConvertData(null, List.of(Sortierung.NACHNAME), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener("b", convertdata);
        assertThat(m, nullValue());

        convertdata = new ConvertData(null, List.of(Sortierung.VORNAME_LEERZEICHEN_NACHNAME, Sortierung.EINTRITT, Sortierung.LEITER), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener("a b c;2023;x;", convertdata);
        assertThat(m.getVorname(), is("a"));
        assertThat(m.getNachnname(), is("b c"));

        convertdata = new ConvertData(null, List.of(Sortierung.VORNAME_LEERZEICHEN_NACHNAME, Sortierung.EINTRITT, Sortierung.LEITER), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener("a b;2023;x;", convertdata);
        assertThat(m.getVorname(), is("a"));
        assertThat(m.getNachnname(), is("b"));

        convertdata = new ConvertData(null, List.of(Sortierung.VORNAME_LEERZEICHEN_NACHNAME, Sortierung.EINTRITT, Sortierung.LEITER), Collections.singletonList(new Sonstiges()), ";", ",", Charset.defaultCharset(), true);
        m = parseLineToMessdiener(" ;2023;x;", convertdata);
        assertThat(m, nullValue());

        StandardMesse sm1 = new StandardMesse(DayOfWeek.MONDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm2 = new StandardMesse(DayOfWeek.TUESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm3 = new StandardMesse(DayOfWeek.WEDNESDAY, 8, "00", "ort", 1, "typ");
        StandardMesse sm4 = new StandardMesse(DayOfWeek.THURSDAY, 8, "00", "ort", 1, "typ");
        List<StandardMesse> standardMessen = Arrays.asList(sm1, sm2, sm3, sm4);
        convertdata = new ConvertData(null, List.of(Sortierung.VORNAME, Sortierung.NACHNAME, Sortierung.STANDARD_MESSE, Sortierung.STANDARD_MESSE, Sortierung.NICHT_STANDARD_MESSE, Sortierung.NICHT_STANDARD_MESSE), standardMessen, ";", ",", Charset.defaultCharset(), true);
        Mockito.when(pf.getStandardMessen()).thenReturn(standardMessen);
        m = parseLineToMessdiener("a;b;x;;x;;", convertdata);
        assertThat(m.getDienverhalten().getBestimmtes(sm1), is(true));
        assertThat(m.getDienverhalten().getBestimmtes(sm2), is(false));
        assertThat(m.getDienverhalten().getBestimmtes(sm3), is(false));
        assertThat(m.getDienverhalten().getBestimmtes(sm4), is(false));
        assertThat(m.getNachnname(), is("b"));

        Files.createDirectories(tempDir);
        Path file = Files.createTempFile(tempDir, null, ".csv");
        Mockito.when(dv.getSavePath()).thenReturn(tempDir.toFile());
        Files.writeString(file, "a;b;1,3;2\nc;d;0;\ne;f;;0\ng;h;0;");
        convertdata = new ConvertData(file.toFile(), List.of(Sortierung.VORNAME, Sortierung.NACHNAME, Sortierung.FREUNDE, Sortierung.GESCHWISTER), standardMessen, ";", ",", Charset.defaultCharset(), true);
        Mockito.when(pf.getSettings()).thenReturn(new Einstellungen());
        doAnswer(invocationOnMock -> {
            medis = null;
            return null;
        }).when(dv).reloadMessdiener();
        Mockito.when(dv.getMessdiener()).then((Answer<List<Messdiener>>) invocationOnMock -> getMessdiener(tempDir));
        ConvertCSV converter = new ConvertCSV(convertdata);
        converter.start();

        ReadFile rf = new ReadFile();
        m = rf.getMessdiener(tempDir.resolve("b, a.xml").toFile());
        assertThat(m.getFreunde()[0], is("d, c"));
        assertThat(m.getFreunde()[1], is("h, g"));
        assertThat(m.getFreunde()[2], is(""));
        assertThat(m.getFreunde()[3], is(""));
        assertThat(m.getFreunde()[4], is(""));
        assertThat(m.getGeschwister()[0], is("f, e"));
        assertThat(m.getGeschwister()[1], is(""));
        assertThat(m.getGeschwister()[2], is(""));
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
        medis.forEach(m -> {
            try {
                m.setNewMessdatenDaten();
            } catch (Messdaten.CouldFindMessdiener e) {
                throw new RuntimeException(e);
            }
        });
        return medis;
    }
}
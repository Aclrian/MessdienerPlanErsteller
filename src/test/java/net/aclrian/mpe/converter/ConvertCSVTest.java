package net.aclrian.mpe.converter;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messe.Sonstiges;
import net.aclrian.mpe.pfarrei.Pfarrei;
import net.aclrian.mpe.utils.DateienVerwalter;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.aclrian.mpe.converter.ConvertCSV.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class ConvertCSVTest {

    @Test
    public void testImport() {
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
    }
}
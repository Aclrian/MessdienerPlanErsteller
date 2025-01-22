package net.aclrian.mpe.einteilung;

import net.aclrian.mpe.algorithms.Generation;
import net.aclrian.mpe.messe.Messe;
import net.aclrian.mpe.messe.StandardMesse;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestGeneration {

    @Test
    public void generiereDefaultMessen() {
        StandardMesse sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", Collections.singletonList(1));
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        List<Messe> list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 2).atStartOfDay());

        sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", Collections.singletonList(2));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 9).atStartOfDay());

        sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", Collections.singletonList(3));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 16).atStartOfDay());

        sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", Collections.singletonList(4));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 23).atStartOfDay());

        sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", Collections.singletonList(5));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 30).atStartOfDay());

        sm = new StandardMesse(DayOfWeek.WEDNESDAY, 12, "00", "o", 1, "t", Collections.singletonList(5));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        sm = new StandardMesse(DayOfWeek.WEDNESDAY, 12, "00", "o", 1, "t", Collections.singletonList(4));
        List<Messe> listDuplicate = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 25).atStartOfDay());
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(listDuplicate.get(0).getDate());

        sm = new StandardMesse(DayOfWeek.MONDAY, 12, "00", "o", 1, "t", List.of(2, 3));
        list = Generation.generiereDefaultMesseFuerStandardmesse(sm, startDate, endDate);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 9).atStartOfDay());
        assertThat(list.get(1).getDate()).isEqualToIgnoringHours(LocalDate.of(2023, 1, 16).atStartOfDay());
    }
}

package net.aclrian.mpe;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.messdiener.WriteFile;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class TestUtil {

    private TestUtil() { }

    public static void mockSaveToXML(Messdiener messdiener) {
        Mockito.doAnswer(getAnswerForMessdienerMock(messdiener)).when(messdiener).makeXML();
    }

    public static Answer<Messdiener> getAnswerForMessdienerMock(Messdiener messdiener) {
        return invocationOnMock -> {
            WriteFile wf = new WriteFile(messdiener);
            try {
                wf.saveToXML();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
    }
}

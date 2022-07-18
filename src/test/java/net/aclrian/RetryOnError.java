package net.aclrian;

import net.aclrian.mpe.utils.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

public class RetryOnError implements TestRule {
    public static final int MAX_ITERATIONS = 5;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RetryOnError.evaluate(base, description, 0);
            }
        };
    }

    private static void evaluate(Statement base, Description description, int currentTry) throws Throwable {
        if (currentTry >= MAX_ITERATIONS) {
            base.evaluate();
        } else {
            try {
                base.evaluate();
            } catch (Throwable t) {
                if (t instanceof NullPointerException &&
                        t.getMessage().contains("Cannot invoke \"String.toString()\" because the return value of \"javafx.scene.control.ComboBox.getValue()\" is null")) {
                    Log.getLogger().warn(description.getDisplayName() + ": Error on " + currentTry + "st/nd/rd/th Try: " + t.getMessage(), t);
                    evaluate(base, description, ++currentTry);
                } else {
                    throw t;
                }
            }
        }
    }
}

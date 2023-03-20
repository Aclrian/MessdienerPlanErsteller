package net.aclrian;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.util.WaitForAsyncUtils;

public class Retrying implements TestRule{
    int retryCount;
    public Retrying(int retryCount){
        this.retryCount = retryCount;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for(int i=0; i<retryCount; i++){
                    try{
                        base.evaluate();
                        WaitForAsyncUtils.waitForFxEvents();
                        WaitForAsyncUtils.checkException();
                        break;
                    } catch (NullPointerException e){
                        if ((i+1)==retryCount){
                            throw e;
                        }
                        System.out.println("Caught Exception:");
                        e.printStackTrace(System.out);
                    }
                }
            }
        };
    }
}

package org.jay.launchstarter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLog;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class TaskDispatcherTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskDispatcher taskDispatcher;

    @Before
    public void setUp() {
        ShadowLog.stream = System.out;
        MockitoAnnotations.initMocks(this);

        TaskDispatcher.init(RuntimeEnvironment.application);
        taskDispatcher = TaskDispatcher.createInstance();
    }

    @Test
    public void testStart() {
        addTask();
        taskDispatcher.start();
    }


    @Test(expected = RuntimeException.class)
    public void testAwaitFail() {
        addNeedWaitTask();
        taskDispatcher.await();
    }

    @Test
    public void testAwait() {
        addNeedWaitTask();
        taskDispatcher.start();
        taskDispatcher.await();
    }

    @Test
    public void testExecuteTask() {
        Task task = getSimpleTask();
        taskDispatcher.executeTask(task);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert task.isFinished();

    }

    private Task getSimpleTask() {
        return new Task() {
            @Override
            public void run() {
                System.out.println("run");
            }
        };
    }

    private void addNeedWaitTask() {
        taskDispatcher.addTask(new Task() {
            @Override
            public void run() {
                System.out.println("run");
            }

            @Override
            public boolean needWait() {
                return true;
            }
        });
    }

    private void addTask() {
        taskDispatcher.addTask(new Task() {
            @Override
            public void run() {
                System.out.println("run");
            }
        });
    }


}

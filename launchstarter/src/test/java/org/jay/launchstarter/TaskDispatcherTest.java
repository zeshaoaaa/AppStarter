package org.jay.launchstarter;

import android.util.Log;
import org.jay.launchstarter.utils.DispatcherLog;
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

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testDependency() {
        final boolean[] orderIsRight = {false};
        final TaskA taskA = new TaskA();
        Task taskB = new TaskB() {
            @Override
            public void run() {
                if (taskA.isFinished()) {
                    orderIsRight[0] = true;
                }
            }
        };
        taskDispatcher.addTask(taskB);
        taskDispatcher.addTask(taskA);
        taskDispatcher.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assert orderIsRight[0];
    }

    class TaskA extends Task {

        @Override
        public void run() {
            DispatcherLog.i("TaskB running");
        }
    }

    class TaskB extends Task {

        @Override
        public List<Class<? extends Task>> dependsOn() {
            List<Class<? extends Task>> list = new ArrayList<>();
            list.add(TaskA.class);
            return list;
        }

        @Override
        public void run() {
            DispatcherLog.i("TaskB running");
        }
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

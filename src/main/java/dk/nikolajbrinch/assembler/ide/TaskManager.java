package dk.nikolajbrinch.assembler.ide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class TaskManager {
  private final ExecutorService executor;
  private final AtomicReference<Future<?>> lastTask;

  public TaskManager() {
    this.executor = Executors.newSingleThreadExecutor();
    this.lastTask = new AtomicReference<>();
  }

  public void submitTask(Runnable task) {
    Future<?> prevTask = lastTask.getAndSet(null);

    if (prevTask != null && !prevTask.isDone()) {
      prevTask.cancel(true);
    }

    Future<?> newTask =
        executor.submit(
            () -> {
              if (!Thread.currentThread().isInterrupted()) {
                task.run();
              }
            });

    lastTask.set(newTask);
  }

  public void shutdown() {
    executor.shutdownNow();
  }
}

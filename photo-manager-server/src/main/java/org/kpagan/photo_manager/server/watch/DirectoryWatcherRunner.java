package org.kpagan.photo_manager.server.watch;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.kpagan.photo_manager.server.service.imaging.PhotoPathService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DirectoryWatcherRunner implements ApplicationRunner {

    private final ApplicationEventPublisher publisher;
    private final PhotoPathService photoPathService;
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    private DirectoryWatcher watcher;

    public DirectoryWatcherRunner(ApplicationEventPublisher publisher, PhotoPathService photoPathService) {
        this.publisher = publisher;
        this.photoPathService = photoPathService;
        simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("watch-executor");
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {

        try {
            watcher = new DirectoryWatcher(publisher, photoPathService);
            watcher.initWatching();
            simpleAsyncTaskExecutor.execute(watcher);
        } catch (Exception e) {
            log.error("Failure initializing DirectoryWatcher", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Closing DirectoryWatcher service");
        try {
            if (watcher != null) {
                watcher.close();
            }
        } catch (Exception e) {
            log.error("Failed to close DirectoryWatcher", e);
        }
    }
}
package org.kpagan.photo_manager.server.watch;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class DirectoryWatcherRunner implements ApplicationRunner {

    private final List<String> watchingDirectories;
    private final ApplicationEventPublisher publisher;
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    private DirectoryWatcher watcher;

    public DirectoryWatcherRunner(@Value("${photo.config.directories}") List<String> watchingDirectories,
                                  ApplicationEventPublisher publisher) {
        this.watchingDirectories = watchingDirectories;
        this.publisher = publisher;
        simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (CollectionUtils.isEmpty(watchingDirectories)) {
            log.warn("Watch directory is not configured. Can't watch when new files are added");
            return;
        }

        try {
            watcher = new DirectoryWatcher(publisher);
            log.info("Watching directory: {}", watchingDirectories);
            watcher.registerPaths(watchingDirectories);
            simpleAsyncTaskExecutor.execute(watcher);
            log.info("Directory Watcher initialized...");
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
package org.kpagan.photo_manager.server.watch;

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

    public DirectoryWatcherRunner(@Value("${photo.config.watchingDirectories}") List<String> watchingDirectories,
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
            DirectoryWatcher watcher = new DirectoryWatcher(publisher);
            log.info("Watching directory: {}", watchingDirectories);
            watcher.registerPaths(watchingDirectories);
            simpleAsyncTaskExecutor.execute(watcher);
            log.info("Directory Watcher initialized...");
        } catch (Exception e) {
            log.error("Failure initializing DirectoryWatcher", e);
        }
    }
}
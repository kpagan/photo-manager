package org.kpagan.photo_manager.runner.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.service.imaging.ImageHandlingService;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "scan", mixinStandardHelpOptions = true)
@RequiredArgsConstructor
@Slf4j
public class ScanCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-d", "--directory"}, description = "Directory to scan", required = true)
    private String directory;

    @CommandLine.Parameters(description = "positional params")
    private List<String> positionals;

    private final ImageHandlingService imageHandlingService;

    @Override
    public Integer call() {
        try {
            imageHandlingService.handleImagesUnder(directory);
            return 0;
        } catch (IOException e) {
            log.error("Error while scanning folder {}", directory, e);
            return -1;
        }
    }
}

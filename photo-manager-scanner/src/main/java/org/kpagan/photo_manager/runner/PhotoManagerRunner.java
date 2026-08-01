package org.kpagan.photo_manager.runner;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.runner.cli.ScanCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@RequiredArgsConstructor
public class PhotoManagerRunner implements CommandLineRunner, ExitCodeGenerator {

    private final ScanCommand scanCommand;

    private final CommandLine.IFactory factory; // auto-configured to inject PicocliSpringFactory

    private int exitCode;

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(scanCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

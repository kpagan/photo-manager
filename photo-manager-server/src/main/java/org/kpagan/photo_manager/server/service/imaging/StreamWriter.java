package org.kpagan.photo_manager.server.service.imaging;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StreamWriter {
    void writeTo(OutputStream outputStream) throws IOException;
}

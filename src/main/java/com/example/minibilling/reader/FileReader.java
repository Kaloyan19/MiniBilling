package com.example.minibilling.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileReader<T> {
    List<T> read(Path path) throws IOException;
}
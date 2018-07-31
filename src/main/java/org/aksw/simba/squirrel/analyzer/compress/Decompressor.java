package org.aksw.simba.squirrel.analyzer.compress;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Decompressor {

    public List<File> decompress(File inputFile) throws IOException;

}

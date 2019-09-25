package org.dice_research.squirrel.analyzer.compress;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Interface for a Decompressor class
 * 
 * @author Geraldo de Souza Junior gsjunior@mail.uni-paderborn.de
 *
 */
public interface Decompressor {

    public List<File> decompress(File inputFile) throws IOException;

}

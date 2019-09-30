package org.dice_research.squirrel.analyzer.compress;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;


/**
 * Interface for a Decompressor class
 * 
 * @author Geraldo de Souza Junior gsjunior@mail.uni-paderborn.de
 *
 */
public interface Decompressor {

    public List<File> decompress(CrawleableUri curi, File inputFile) throws IOException;

}

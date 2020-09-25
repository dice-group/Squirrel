package org.dice_research.squirrel.sink.quadbased;

import org.apache.jena.sparql.core.Quad;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.SinkBase;

/**
 * A sink that can handle quads.
 *
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 */
public interface QuadBasedSink extends SinkBase{
    
    /*
     * Add a quad for the given uri.
     *
     * @param uri    The given uri.
     * @param quad The quad to add.
     */
    void addQuad(CrawleableUri uri, Quad quad);


}

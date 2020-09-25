package org.dice_research.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDFBase;
import org.dice_research.squirrel.analyzer.compress.impl.FileManager;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Class for the Decompression Classes
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */
public class DecompressionTest {

    private FileManager fm = new FileManager();
    private Model model;
    private CrawleableUri testUri;

    private File tarFile;

    private File bzipFile;

    private File gzFile;

    /**
     * 
     * Initiate the environment for the test scenarios.
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    @Before
    public void initiateEnvironmentTest() throws IOException, URISyntaxException {
        tarFile = generateTar(createRdfFiles());
        bzipFile = generateBzip(tarFile);
        gzFile = generateGZ(tarFile);
        testUri = new CrawleableUri(new URI("http://dice-research.org/squirrel/test"));

    }
    
    /**
     * 
     * Create the RDF files for testing
     * 
     * @return a list of created files
     * @throws IOException
     */
    private List<File> createRdfFiles() throws IOException{
        List<File> listFiles = new ArrayList<File>();
        
        File file1 = File.createTempFile("tempModel_" + System.currentTimeMillis(), ".nt");
        FileOutputStream os = new FileOutputStream(file1);

        model = ModelFactory.createDefaultModel();

        model.add(model.createResource("http://www.dice-research.org/Squirrel/Resource1"),
                model.createProperty("http://www.dice-research.org/Squirrel/Property1"),
                model.createLiteral("First statement"));

        model.add(model.createResource("http://www.dice-research.org/Squirrel/Resource2"),
                model.createProperty("http://www.dice-research.org/Squirrel/Property2"),
                model.createResource("http://www.dice-research.org/Squirrel/Resource3"));

        model.add(model.createResource("http://www.dice-research.org/Squirrel/Resource3"),
                model.createProperty("http://www.dice-research.org/Squirrel/Property1"),
                model.createLiteral("Third Statement"));
        

        RDFDataMgr.write(os, model, Lang.NT);
        
        
        listFiles.add(file1);

       
        return listFiles;
        
    }

    private File generateTar(List<File> files) throws IOException {
        File tarFile = File.createTempFile("tempModel_" + System.currentTimeMillis(), ".tar");

        TarArchiveOutputStream taos = new TarArchiveOutputStream(new FileOutputStream(tarFile));
        taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        taos.setAddPaxHeadersForNonAsciiNames(true);

        for (File file : files) {

            String entry = File.separator + file.getName();

            taos.putArchiveEntry(new TarArchiveEntry(file, entry));
            try (FileInputStream in = new FileInputStream(file)) {
                IOUtils.copy(in, taos);
            }
            taos.closeArchiveEntry();
        }
        taos.finish();

        return tarFile;
    }

    private File generateBzip(File file) throws IOException {
        File bzipFile = File.createTempFile("tempModel_" + System.currentTimeMillis(), ".tar.bz2");
        FileOutputStream fos = new FileOutputStream(bzipFile);
        FileInputStream fis = new FileInputStream(file);
        BZip2CompressorOutputStream bzout = new BZip2CompressorOutputStream(fos);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = fis.read(buffer))) {
            bzout.write(buffer, 0, n);
        }
        
        bzout.close();
        fis.close();

        return bzipFile;
    }

    private File generateGZ(File file) throws IOException {
        
        File gzipFile = File.createTempFile("tempModel_" + System.currentTimeMillis(), ".tar.gz");
        FileOutputStream fos = new FileOutputStream(gzipFile);
        FileInputStream fis = new FileInputStream(file);
        GzipCompressorOutputStream gzout = new GzipCompressorOutputStream(fos);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = fis.read(buffer))) {
            gzout.write(buffer, 0, n);
        }
        
        gzout.close();
        fis.close();

        return gzipFile;
    }
    
    private void assertDcompressedFiles(List<File> listFiles) {
        
        StmtIterator iter = model.listStatements();
        List<Triple> originalTriples = new ArrayList<Triple>();
        while(iter.hasNext()) {
            originalTriples.add(iter.next().asTriple());
        }
        
        
        for (File file : listFiles) {
            List<Triple> listTriples = new ArrayList<Triple>();
            FilterSinkRDF sinkRDF = new FilterSinkRDF(listTriples);
            RDFDataMgr.parse(sinkRDF, file.getAbsolutePath(),Lang.NT);
            Assert.assertThat(listTriples, IsIterableContainingInAnyOrder.containsInAnyOrder(originalTriples.toArray()));
       
        }
        
    }
    
    private class FilterSinkRDF extends StreamRDFBase {
        

        private List<Triple> listTriples;
        

        public FilterSinkRDF(List<Triple> listTriples) {
            this.listTriples = listTriples;
        }

        @Override
        public void triple(Triple triple) {
            listTriples .add(triple);
        }


    }

    @Test
    public void bzipDecompressionTest() {
        List<File> listFiles = fm.decompressFile(testUri, bzipFile);
        assertDcompressedFiles(listFiles);
    }
    
    
    @Test
    public void gzDecompressionTest() {
        List<File> listFiles = fm.decompressFile(testUri, gzFile);
        assertDcompressedFiles(listFiles);
    }

    @Test
    public void tarDecompressionTest() {
        List<File> listFiles = fm.decompressFile(testUri, tarFile);
        assertDcompressedFiles(listFiles);
    }
    
    @After
    public void deleteFiles() {
        tarFile.delete();
        gzFile.delete();
        bzipFile.delete();
    }

}

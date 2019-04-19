package org.dice_research.squirrel.analyzer.impl.data_portals;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.*;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.squirrel.analyzer.impl.data_portals.utils.AbstractDataPortalTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class Datago_jpTest extends AbstractDataPortalTest{
    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException, URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005")),
            new File("src/test/resources/html_scraper_analyzer/datago/datago_detail.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("湖沼データ（河北潟）参考資料A3サイズ"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("湖沼データ（河北潟）のうち、湖沼画像データをA3サイズでPDFにしたものです。"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("国土交通省"),null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl(DCTerms.creator.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("応用地理部"),null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("2018-12-03"),null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005"),
                    new PropertyImpl("http://www.w3.org/ns/dcat#downloadURL"),
                    new ResourceImpl("http://www1.gsi.go.jp/geowww/lake/download/kahokugata/kahokugata-2018_A3.pdf")
                )
            )

        });
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI("")),
            new File("src/test/resources/"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20150223_0105")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0005")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0004")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20180907_0054")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_3101")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_3075")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_3046")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2528")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2527")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2526")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2525")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20190201_0032")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20160907_0024")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20161206_0029")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20171204_0069")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2475")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2474")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(""),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2473")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2472")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset/mlit_20140919_2471")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset?page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset?page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset?page=3"))
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.go.jp#")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset"),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://www.data.go.jp/data/en/dataset?page=1246")
                )
            )
        });
        return testConfigs;
    }

    public Datago_jpTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}

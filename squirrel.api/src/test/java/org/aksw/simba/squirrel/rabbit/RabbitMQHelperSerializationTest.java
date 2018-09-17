package org.aksw.simba.squirrel.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.aksw.simba.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.aksw.simba.squirrel.rabbit.msgs.CrawlingResult;
import org.aksw.simba.squirrel.rabbit.msgs.UriSet;
import org.aksw.simba.squirrel.rabbit.msgs.UriSetRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RabbitMQHelperSerializationTest {

    @Parameters
    public static Collection<Object[]> data() throws IOException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();

        CrawleableUriFactory4Tests factory = new CrawleableUriFactory4Tests();

        testConfigs.add(new Object[] { null });

        testConfigs.add(new Object[] { new UriSet() });
        testConfigs.add(new Object[] { new UriSet(null) });
        testConfigs.add(new Object[] { new UriSet(
                Arrays.asList(factory.create("http://example.org/1"), factory.create("http://example.org/2"))) });

        testConfigs.add(new Object[] { new UriSetRequest() });

        testConfigs.add(new Object[] { new CrawlingResult(
                Arrays.asList(factory.create("http://example.org/1"), factory.create("http://example.org/2"))) });
        testConfigs.add(new Object[] { new CrawlingResult(
                Arrays.asList(factory.create("http://example.org/1"), factory.create("http://example.org/2"), factory.create("http://example.org/99"), factory.create("http://example.org/45"),
                        factory.create("http://example.org/12"), factory.create("http://example.org/3"))) });
        testConfigs.add(new Object[] { new UriSet(null) });

        return testConfigs;
    }

    private Object original;

    public RabbitMQHelperSerializationTest(Object object) {
        this.original = object;
    }

    @Test
    public void test() {
        RabbitMQHelper helper = new RabbitMQHelper();

        Object result = helper.parseObject(helper.writeObject(original));

        if (original == null) {
            Assert.assertNull(result);
        } else {
            Assert.assertNotNull(result);
            Assert.assertEquals(original, result);
        }
    }
}

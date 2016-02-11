package org.aksw.simba.squirrel.seed.generator;

import java.io.IOException;

import org.aksw.simba.squirrel.data.uri.filter.BlacklistUriFilter;
import org.aksw.simba.squirrel.data.uri.filter.KnownUriFilter;
import org.aksw.simba.squirrel.frontier.Frontier;
import org.aksw.simba.squirrel.frontier.impl.FrontierImpl;
import org.aksw.simba.squirrel.frontier.impl.zeromq.ZeroMQBasedFrontier;
import org.aksw.simba.squirrel.queue.InMemoryQueue;
import org.aksw.simba.squirrel.queue.IpAddressBasedQueue;
import org.aksw.simba.squirrel.queue.UriQueue;
import org.aksw.simba.squirrel.seed.generator.impl.SeedGeneratorZeroMQImpl;

import junit.framework.TestCase;

public class SeedGeneratorZeroMQTest extends TestCase {
	//Using the same socket as from worker to frontier
	public static final String ADDRESS_FOR_MSG_FROM_SEED_TO_FRONTIER = "tcp://localhost:25002";
	public static final String ADDRESS_FOR_MSG_FROM_WORKERS_TO_FRONTIER = "tcp://localhost:25002";
	
	public static final String ADDRESS_FOR_MSG_FROM_FRONTIER_TO_WORKERS = "tcp://localhost:25001";
	
    public static void test() throws InterruptedException, IOException {
        IpAddressBasedQueue queue = new InMemoryQueue();
        FrontierImplQueueCheck frontier = new FrontierImplQueueCheck(new BlacklistUriFilter(), queue);
        //ZeroMQBasedFrontier frontierWrapper = ZeroMQBasedFrontier.create(frontier,
        //        ADDRESS_FOR_MSG_FROM_FRONTIER_TO_WORKERS, ADDRESS_FOR_MSG_FROM_WORKERS_TO_FRONTIER);
        //SeedGeneratorZeroMQImplSender seedGenerator = new SeedGeneratorZeroMQImplSender(ADDRESS_FOR_MSG_FROM_SEED_TO_FRONTIER);
        //FrontierStopper frontierStopper = new FrontierStopper(frontierWrapper);
        //
        //Thread[] threads = {new Thread(seedGenerator), new Thread(frontierStopper), new Thread(frontierWrapper)};
        //for (int i = 0; i < threads.length; ++i) {
        //    threads[i].start();
        //}
        //for (int i = 0; i < threads.length; ++i) {
        //    threads[i].join();
        //}
        //
        //UriQueue queueFrontier = frontier.getQueue();
        //System.out.println(queueFrontier);
        
        //frontierWrapper.close();
        //Check that frontier received the same URIs as were sent
        //take a look at ipAddressBasedQueueTest
    }
    
    public static class FrontierImplQueueCheck extends FrontierImpl {

		public FrontierImplQueueCheck(KnownUriFilter knownUriFilter, UriQueue queue) {
			super(knownUriFilter, queue);
		}
    	
		public UriQueue getQueue() {
			return this.queue;
		}
    }
    
    public static class FrontierStopper implements Runnable {

    	protected ZeroMQBasedFrontier frontier;
    	
    	public FrontierStopper(ZeroMQBasedFrontier frontier) {
    		this.frontier = frontier;
    	}
    	
		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				this.frontier.setRunning(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
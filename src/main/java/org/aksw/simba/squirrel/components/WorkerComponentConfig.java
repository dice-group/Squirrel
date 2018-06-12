package org.aksw.simba.squirrel.components;

import java.io.IOException;

import org.hobbit.core.components.AbstractComponent;
import org.hobbit.core.rabbit.DataSender;
import org.hobbit.core.rabbit.DataSenderImpl;
import org.hobbit.core.rabbit.RabbitRpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerComponentConfig extends AbstractComponent{
	
	 public WorkerComponentConfig() throws Exception {
		 super.init();
	}
	
	@Bean(name="sender")
	public DataSender sender() throws IllegalStateException, IOException {
		DataSender sender = DataSenderImpl.builder()
	            .queue(outgoingDataQueuefactory, FrontierComponent.FRONTIER_QUEUE_NAME)
	            .build();
		
		return sender;
	}
	
	@Bean(name="client")
	public RabbitRpcClient client() throws IOException {
		RabbitRpcClient client = RabbitRpcClient.create(outgoingDataQueuefactory.getConnection(),
                FrontierComponent.FRONTIER_QUEUE_NAME);
		return client;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

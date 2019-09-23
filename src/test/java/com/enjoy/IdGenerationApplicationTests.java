package com.enjoy;

import com.enjoy.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdGenerationApplicationTests {

	private final static  int  THREAD_NUM = 1000;

	@Autowired
	@Qualifier("redisServiceImpl")
	private OrderService orderService;

	private final static CyclicBarrier cb = new CyclicBarrier(THREAD_NUM);

	private final Set<Object> ids = new HashSet<>();
	@Test
	public void generationId() throws IOException, InterruptedException {
		for(int i=0; i<THREAD_NUM;i++){
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						cb.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}
					ids.add(orderService.getOrderId());
				}
			});

			thread.start();
		}
		Thread.sleep(1000);
        System.out.println("最终生成了" + ids.size() +"个不重复的id" );
        System.in.read();

	}

	@Test
	public void contextLoads() {
	}

}

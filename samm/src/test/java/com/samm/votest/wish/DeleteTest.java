package com.samm.votest.wish;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.samm.biz.WishBiz;

@SpringBootTest
class DeleteTest {
	
	@Autowired
	WishBiz biz;

	@Test
	void contextLoads() {
	
		try {
			biz.remove(1);
			System.out.println("delete test 완료");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

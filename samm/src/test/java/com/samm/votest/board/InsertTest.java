package com.samm.votest.board;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.samm.biz.BoardBiz;
import com.samm.vo.BoardVo;
import com.samm.vo.SearchCondition;

@SpringBootTest
class InsertTest {
	
	@Autowired
	BoardBiz biz;

	@Test
	void contextLoads() {
		SearchCondition sc = new SearchCondition("title1");
		BoardVo obj = new BoardVo("test","content223");
		List<BoardVo> list = null;
		
		try {
			list= biz.getSearchResultPage(sc);
					System.out.println("List = " + list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(obj);
		/*
		 * try { biz.write(obj); System.out.println("test 완료"); } catch (Exception e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

}

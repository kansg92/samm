package com.samm.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

 
import com.samm.biz.AreaBiz;
import com.samm.biz.BoardBiz;
import com.samm.biz.FestivalBiz;
import com.samm.biz.MngBiz;
import com.samm.biz.UsersBiz;
import com.samm.vo.AdmintblVo;
import com.samm.vo.BoardVo;
import com.samm.vo.FestivalVo;
import com.samm.vo.PageHadller;
import com.samm.vo.SearchCondition;
import com.samm.vo.UsersVo;

@Controller
public class MainController {

	@Autowired
	FestivalBiz fbiz;
	@Autowired
	AreaBiz abiz;
	@Autowired
	UsersBiz ubiz;
	@Autowired
	BoardBiz bbiz;

	@Autowired
	MngBiz mbiz;

	@RequestMapping("/")
	public String main(Model m, String areacode, String eventstartdate, String eventenddate, HttpSession session) {
		Date date = new Date();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		if (areacode == null || areacode.equals("")) {
			areacode = "1";
		}
		eventstartdate = today.format(date).toString();
		eventenddate = today.format(date).toString();
		List<FestivalVo> list = null;
		List<Map<String, String>> area = null;
		try {
			list = fbiz.searchFestival3(areacode, eventstartdate, eventenddate);
			area = abiz.get();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		m.addAttribute("area", area);
		m.addAttribute("festival", list);
		m.addAttribute("center", "center");
		return "index";
	}

	@RequestMapping("/about")
	public String about(Model m) {

		m.addAttribute("center", "test");
		return "index";
	}

	@RequestMapping("/map")
	public String map(Model m) {
		Date date = new Date();
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		String sdate = today.format(date).toString();
		List<FestivalVo> list = null;
		try {
			list = fbiz.searchFestival("1", sdate, sdate);
			System.out.println(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.addAttribute("festival", list);
		return "map";
	}

	@RequestMapping("/login")
	public String login(Model m) {

		m.addAttribute("center", "login");
		return "index";
	}

	@RequestMapping("/loginimpl")
	public String loginimpl(Model m, String id, String pwd, HttpSession session) {

		AdmintblVo admin = null;
		UsersVo u = null;

		try {
			u = ubiz.get(id);
			if (u != null) {
				if (u.getPwd().equals(pwd)) {
					session.setAttribute("loginuser", u);
					m.addAttribute("loginuser", u);
				}
			} else if (u == null) {
				admin = mbiz.aget(id);
				if (admin != null) {
					if (admin.getPwd().equals(pwd)) {
						session.setAttribute("loginadmin", admin);
						m.addAttribute("loginadmin", admin);
					} else {
						throw new Exception(); //
					}
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return login(m);
		}

		m.addAttribute("center", "/center");
		return "redirect:/";

	}

	@RequestMapping("/logout")
	public String logout(Model m, HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}

	@RequestMapping("/board")
	public String main(Model m, SearchCondition sc) {
		List<BoardVo> blist = null;
		try {
			/*
			 * blist = bbiz.getList(); 
			 * m.addAttribute("blist", blist);
			 * m.addAttribute("center", "board");
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			int totalCnt = bbiz.getSearchResultCnt(sc);
			sc.setOffset((sc.getPage()-1)*sc.getPageSize());
			blist = bbiz.getSearchResultPage(sc);
			
			
			PageHadller pageHandller = new PageHadller(totalCnt, sc);
			
			/*if(page==null) page=1;
			if(pageSize==null) pageSize=12;*/
			
			/*Map map = new HashMap();
			map.put("offset", (page-1)*pageSize);
			map.put("pageSize", pageSize);*/
//			System.out.println("sc::"+sc);
			

//			System.out.println("blist::"+blist);
//			System.out.println("ph::"+pageHandller);
			
			m.addAttribute("blist", blist);
			m.addAttribute("center", "board");
			m.addAttribute("ph",pageHandller);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	return "index";

	}

	@RequestMapping("/chatbot")
	public String goChatBot(Model m) {
		return "chatbot";
	}

	@RequestMapping("/mng/main")
	public String adminMain(Model m, HttpSession session) {
		session.setAttribute("loginadmin", session.getAttribute("loginadmin"));
		
		List<FestivalVo> fvolist = null;
		
		try {
			fvolist = mbiz.fnowget();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		m.addAttribute("fvolist", fvolist);
		m.addAttribute("center", "mng/center");
		return "/mng/main";
	}

}

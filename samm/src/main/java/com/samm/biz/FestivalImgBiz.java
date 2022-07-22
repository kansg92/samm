package com.samm.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samm.frame.Biz;
import com.samm.mapper.FestivalImgMapper;
import com.samm.vo.FestivalImgVo;

@Service
public class FestivalImgBiz implements Biz<Integer,FestivalImgVo> {

	@Autowired
	FestivalImgMapper dao;
	
	@Override
	public void register(FestivalImgVo v) throws Exception {
		dao.insert(v);
	}

	@Override
	public void modify(FestivalImgVo v) throws Exception {
		dao.update(v);
		
	}

	@Override
	public void remove(Integer k) throws Exception {
		dao.delete(k);
	}

	@Override
	public FestivalImgVo get(Integer k) throws Exception {
		return dao.select(k);
	}

	@Override
	public List<FestivalImgVo> get() throws Exception {
		return dao.selectAll();
	}



}

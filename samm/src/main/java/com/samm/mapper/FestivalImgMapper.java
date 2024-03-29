package com.samm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.samm.vo.FestivalImgVo;
import com.samm.vo.imgallowVo;

@Mapper
@Repository
public interface FestivalImgMapper{
	
	public void insert(FestivalImgVo v) throws Exception;
	public void update(FestivalImgVo v) throws Exception;
	public void delete(int k) throws Exception;
	public FestivalImgVo select(int k) throws Exception;
	public List<FestivalImgVo> selectAll() throws Exception;
	public List<FestivalImgVo> selectAllFid(int k) throws Exception;
	public List<FestivalImgVo> selectAllowY(int k) throws Exception;
	
	public void updateAllow(FestivalImgVo v) throws Exception;
	public List<FestivalImgVo> selectByUid(String uid) throws Exception;
 
	
}

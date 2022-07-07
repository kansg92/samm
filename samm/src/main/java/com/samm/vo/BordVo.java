package com.samm.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BordVo {
    private Integer pnum;
    private String contents;
    private String title;
    private Date regdate;
    private Date udate;
    private int views;
    private String uid;

    // 게시판 제목들
    public BordVo(Integer pnum, String title, Date regdate, int views, String uid) {
        this.pnum = pnum;
        this.title = title;
        this.regdate = regdate;
        this.views = views;
        this.uid = uid;
    }

    // 글 수정시
    public BordVo(Integer pnum, String contents, String title, Date udate, int views) {
        this.pnum = pnum;
        this.contents = contents;
        this.title = title;
        this.views = views;
    }

    // 조회수
    public BordVo(Integer pnum, int views) {
        this.pnum = pnum;
        this.views = views;
    }
}
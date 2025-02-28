package kh.springboot.board.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private int boardId;
    private String boardTitle;
    private String boardWriter;
    private String nickName;
    private String boardContent;
    private int boardCount;
    private Date boardCreateDate;
    private Date boardModifyDate;
    private String boardStatus;
    private int boardType;
}

package kh.springboot.board.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
    private int replyId;
    private String replyContent;
    private int refBoardId;
    private String replyWriter;
    private String nickName;
    private Date replyCreateDate;
    private Date replyModifyDate;
    private String replyStatus;
}

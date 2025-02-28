package kh.springboot.board.model.mapper;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;

@Mapper
public interface BoardMapper {
    int getListCount(int i);

    ArrayList<Board> selectBoardList(int i, RowBounds rb);

    int insertBoard(Board board);

    Board selectBoard(int bId);

    int updateCount(int bId);

    int updateBoard(Board b);

    int deleteBoard(int b);

    ArrayList<Attachment> selectAttmBoardList(Integer bId);

    int insertAttm(ArrayList<Attachment> list);

    void updateAttmLevel(int boardId);

    int deleteAttm(ArrayList<String> delRename);

    ArrayList<Board> selectTop();

    ArrayList<Reply> seleteReplyList(int bId);

    int insertReply(Reply r);

    int deleteReply(int replyId);

    int updateReply(Reply r);

    ArrayList<Board> selectRecentBoards();

//    int statusAttm(int bId);
}

package kh.springboot.board.model.service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;

    public int getListCount(int i) {
        return mapper.getListCount(i);
    }

    public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
        int offset = (pi.getCurrentPage() - 1) * pi.getBoardLimit();
        RowBounds rb = new RowBounds(offset, pi.getBoardLimit());
        return mapper.selectBoardList(i, rb);
    }

    public int insertBoard(Board board) {
        return mapper.insertBoard(board);
    }

    public Board selectBoard(int bId, Member m) {
        Board b = mapper.selectBoard(bId);
        if (b != null && m != null && !b.getBoardWriter().equals(m.getId())) {
            int result = mapper.updateCount(bId);
            if (result > 0) {
                b.setBoardCount(b.getBoardCount() + 1);
            }
        }
        return b;
    }

    public int updateBoard(Board b) {
        return mapper.updateBoard(b);
    }

    public int deleteBoard(int b) {
        return mapper.deleteBoard(b);
    }

    public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
        return mapper.selectAttmBoardList(bId);
    }

    public int insertAttm(ArrayList<Attachment> list) {
        return mapper.insertAttm(list);
    }

    public void updateAttmLevel(int boardId) {
        mapper.updateAttmLevel(boardId);
    }

    public int deleteAttm(ArrayList<String> delRename) {
        return mapper.deleteAttm(delRename);
    }

    public ArrayList<Board> selectTop() {
        return mapper.selectTop();
    }

    public ArrayList<Reply> selectReplyList(int bId) {
        return mapper.seleteReplyList(bId);
    }

    public int insertReply(Reply r) {
        return mapper.insertReply(r);
    }

    public int deleteReply(int replyId) {
        return mapper.deleteReply(replyId);
    }

    public int updateReply(Reply r) {
        return mapper.updateReply(r);
    }

    public ArrayList<Board> selectRecentBoards() {
        return mapper.selectRecentBoards();
    }

//    public int statusAttm(int bId) {
//        return mapper.statusAttm(bId);
//    }
}

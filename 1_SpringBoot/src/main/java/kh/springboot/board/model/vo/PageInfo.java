package kh.springboot.board.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int currentPage;
    private int listCount;
    private int pageLimit;
    private int maxPage;
    private int startPage;
    private int endPage;
    private int boardLimit;
}

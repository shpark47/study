package kh.springboot.member.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoList {
    private int todoNum;
    private String todo;
    private String writer;
    private String status;
    private String important;
}

package kh.springboot.member.model.vo;

import lombok.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private String id;
    private String pwd;
    private String name;
    private String nickName;
    private String email;
    private String gender;
    private int age;
    private String phone;
    private String address;
    private Date enrollDate;
    private Date updateDate;
    private String memberStatus;
    private String isAdmin;
    private String profile;
}

package kh.springboot.admin;

import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BoardService bService;
    private final MemberService mService;

    @GetMapping("/home")
    public String moveToMainAdmin(Model model) {
        File f = new File("D:/logs/member/");
        File[] files = f.listFiles();

        TreeMap<String, Integer> dateCount = new TreeMap<>();
        BufferedReader br = null;

        try {
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                String data;
                while ((data = br.readLine()) != null) {
                    String date = data.split(" ")[0];
                    if (dateCount.containsKey(date)) {
                        dateCount.put(date, dateCount.get(date) + 1);
                    } else {
                        dateCount.put(date, 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Board> list = bService.selectRecentBoards();

        model.addAttribute("list", list);
        model.addAttribute("dateCount", dateCount);

        return "admin";
    }

    @GetMapping("members")
    public String selectMembers(Model model) {
        ArrayList<Member> list = mService.selectMembers();
        model.addAttribute("list", list);
        return "members";
    }
}

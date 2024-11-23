package com.honsb.travel.controller;

import com.honsb.travel.domain.enum_class.BoardCategory;
import com.honsb.travel.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final UploadImageService uploadImageService;

    @GetMapping("/{category}")
    public String boardListPage(@PathVariable String category, Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @RequestParam(required = false) String sortType,
                                @RequestParam(required = false) String searchType,
                                @RequestParam(required = false) String keyword){
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null){
            model.addAttribute("message", "카테고리가 존재하지 않습니다.");
            model.addAttribute("nextUrl","/");
            return "printMessage";
        }

        model.addAttribute("notices", boardService.getNotice(boardCategory));

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        if (sortType != null){
            if (sortType.equals("date")){
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
            }else if (sortType.equals("like")){
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("likeCnt").descending());
            } else if (sortType.equals("comment")) {
                pageRequest = PageRequest.of(page - 1,10,Sort.by("commentCnt").descending());

            }
        }

        model.addAttribute("category",category);
        model.addAttribute("boards",boardService.getBoardList(boardCategory, pageRequest, searchType,keyword));
        model.addAttribute("boardSearchRequest", new BoardSearchRequest(sortType,searchType,keyword));
        return "boards/list";
    }
}

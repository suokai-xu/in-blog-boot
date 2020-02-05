package com.ijson.blog.controller.view;

import com.google.common.base.Strings;
import com.ijson.blog.controller.BaseController;
import com.ijson.blog.dao.entity.PostEntity;
import com.ijson.blog.dao.query.PostQuery;
import com.ijson.blog.model.Constant;
import com.ijson.blog.service.model.info.PostInfo;
import com.ijson.blog.util.Pageable;
import com.ijson.mongo.support.model.Page;
import com.ijson.mongo.support.model.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * desc:
 * version: 6.7
 * Created by cuiyongxu on 2019/12/10 11:23 PM
 */
@Slf4j
@Controller
public class HomeController extends BaseController {

    @RequestMapping("/")
    public ModelAndView root(HttpServletRequest request) {
        String keyWord = request.getParameter("keyWord");
        String pageNumber = request.getParameter("index");
        return index(getPageNumber(pageNumber), keyWord);
    }

    @RequestMapping("/index")
    public ModelAndView index(Integer index, String keyWord) {
        return getView(index, keyWord, null);
    }


    @RequestMapping("/activity")
    public ModelAndView activity(Integer index, String keyWord) {
        return getView(index, keyWord, "activity");
    }

    @RequestMapping("/software")
    public ModelAndView software(Integer index, String keyWord) {
        return getView(index, keyWord, "software");
    }


    private int getPageNumber(String pageNumber) {
        int index = 1;
        if (!Strings.isNullOrEmpty(pageNumber)) {
            boolean number = NumberUtils.isNumber(pageNumber);
            if (number) {
                index = Integer.parseInt(pageNumber);
            }
        }
        return index;
    }

    private ModelAndView getView(Integer index, String keyWord, String indexMenuEname) {
        ModelAndView view = new ModelAndView("view/index-list.html");

        Page page = new Page();
        if (Objects.isNull(index)) {
            index = 1;
        }
        page.setPageNumber(index);

        PostQuery query = new PostQuery();
        if (!Strings.isNullOrEmpty(keyWord)) {
            query.setLikeTitle(true);
            query.setTitle(keyWord);
        }
        if (!Strings.isNullOrEmpty(indexMenuEname)) {
            query.setIndexMenuEname(indexMenuEname);
        }
        query.setEnable(true);
        query.setStatus(Constant.PostStatus.pass);
        PageResult<PostEntity> result = postService.find(null, query, page);

        view.addObject("total", result.getTotal());
        view.addObject("page", new Pageable(((Long) result.getTotal()).intValue(), index));
        view.addObject("dataList", PostInfo.indexPost(result, keyWord));
        view.addObject("path", "/");
        view.addObject("keyWord", keyWord);
        addViewModelAndView(view);
        return view;
    }

}

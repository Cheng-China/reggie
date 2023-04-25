package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.CustomException;
import org.example.common.R;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 分类列表查询
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info("page:{},pageSize{}",page,pageSize);
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 新增菜品
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("保存菜品成功");
    }

    /**
     * 修改菜品
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(Long ids) throws CustomException {
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 查询菜品分类
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件，条件为type
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categories = categoryService.list(queryWrapper);
        return R.success(categories);
    }

}

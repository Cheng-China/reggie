package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.dto.SetmealDto;
import org.example.entity.Category;
import org.example.entity.Setmeal;
import org.example.service.CategoryService;
import org.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> post(@RequestBody SetmealDto setmealDto){
        log.info("setmealDao:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> list(int page,int pageSize){
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateUser);
        setmealService.page(pageInfo,queryWrapper);

        //查询到的 pageInfo 对象里的 records 记录中缺少套餐分类名称
        List<SetmealDto> setmealDtos = new ArrayList<>();
        List<Setmeal> records = pageInfo.getRecords();
        for (Setmeal setmeal:records){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto,"setmealDishes");
            setmealDtos.add(setmealDto);
        }
        for (SetmealDto setmealDto: setmealDtos) {
            Category category = categoryService.getById(setmealDto.getCategoryId());
            setmealDto.setCategoryName(category.getName());
        }
        pageInfo.setRecords(setmealDtos);
        return R.success(pageInfo);

        /*
        *  课程写法
        * //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);

        * */
    }

    @GetMapping("/{id}")
    public R<Setmeal> get(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        return R.success(setmeal);
    }
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }


}

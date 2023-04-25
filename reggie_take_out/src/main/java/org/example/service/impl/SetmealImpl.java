package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;
import org.example.mapper.SetmedalMapper;
import org.example.service.SetmealDishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealImpl extends ServiceImpl<SetmedalMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先保存数据到 setmeal 表
        this.save(setmealDto);

        //获取 setmeal_id
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getName,setmealDto.getName());
        Setmeal setmeal = this.getOne(queryWrapper);
        Long setmealId = setmeal.getId();
        
        // 再保存菜品到 stemeal_dish 表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }
}

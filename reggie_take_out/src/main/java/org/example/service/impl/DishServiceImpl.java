package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.DishDto;
import org.example.entity.Dish;
import org.example.entity.DishFlavor;
import org.example.mapper.DishMapper;
import org.example.service.DishFlavourService;
import org.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * 保存DishDto中的所有数据
     */
    @Autowired
    private DishFlavourService dishFlavourService;

    @Override
    @Transactional
    public void saveWithDishFlavour(DishDto dishDto) {
       //保存菜品的基本信息到菜品表中
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //保存DishFlavour到dish_flavour表中

        List<DishFlavor> flavours = dishDto.getFlavors();
        //采用流遍历
        flavours = flavours.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavourService.saveBatch(flavours);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavour(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavourService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithDishFlavour(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavourService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavourService.saveBatch(flavors);
    }
}
